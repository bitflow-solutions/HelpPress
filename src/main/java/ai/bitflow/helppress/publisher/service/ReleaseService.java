package ai.bitflow.helppress.publisher.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipUtil;

import ai.bitflow.helppress.publisher.constant.ApplicationConstant;
import ai.bitflow.helppress.publisher.dao.ChangeHistoryDao;
import ai.bitflow.helppress.publisher.domain.ChangeHistory;
import ai.bitflow.helppress.publisher.domain.Contents;
import ai.bitflow.helppress.publisher.domain.ReleaseHistory;
import ai.bitflow.helppress.publisher.repository.ContentsRepository;
import ai.bitflow.helppress.publisher.repository.ReleaseHistoryRepository;


/**
 * 배포 파일 처리 서비스
 * @author 김성준
 */
@Service
public class ReleaseService {

	private final Logger logger = LoggerFactory.getLogger(ReleaseService.class);
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
	
	@Value("${app.upload.root.path}")
	private String SRC_FOLDER;
	
	@Value("${app.release.root.path}")
	private String DEST_FOLDER;
	
	@Autowired
	private ReleaseHistoryRepository rhrepo;
	
	@Autowired
	private ContentsRepository crepo;
	
	@Autowired
	private ChangeHistoryDao chdao;
	
	
	/**
	 * 
	 * @param res
	 * @return
	 */
	@Transactional
	public boolean downloadAll(Boolean release, HttpServletResponse res, String userid) {
		
		boolean released = release!=null?release:false;
		String timestamp = sdf.format(Calendar.getInstance().getTime());
		String DEST_FILENAME = "release-all-" + timestamp + ".zip";
		
		// 배포이력 저장
		if (released) {
        	ReleaseHistory item = new ReleaseHistory();
        	item.setType(ApplicationConstant.RELEASE_ALL);
        	item.setFileName(DEST_FILENAME);
        	item.setUserid(userid);
        	rhrepo.save(item);
        	chdao.releaseAll();
        }
		
		File destFolder = new File(DEST_FOLDER);
		if (!destFolder.exists()) {
			destFolder.mkdirs();
		}
		
		String destFilePath = DEST_FOLDER + DEST_FILENAME;
		File destFile = new File(destFilePath);
		ZipUtil.pack(new File(SRC_FOLDER), destFile);

		FileInputStream fis = null;
		ServletOutputStream out = null;
		
		try {
			fis = new FileInputStream(destFile); 			// file not found exception || 엑세스가 거부되었습니다
			res.setHeader(HttpHeaders.PRAGMA, 				"no-cache");
			res.setHeader(HttpHeaders.CONTENT_TYPE, 		"application/zip");
			res.setHeader(HttpHeaders.CONTENT_LENGTH, 		"" + destFile.length());
			res.setHeader(HttpHeaders.CONTENT_DISPOSITION, 	"attachment; filename=\"" + DEST_FILENAME + "\"");
			out = res.getOutputStream();
            FileCopyUtils.copy(fis, out);
			return true;
		} catch(FileNotFoundException e1){
			e1.printStackTrace();
			return false;
        } catch(Exception e2){
        	e2.printStackTrace();
			return false;
        } finally {
            if(fis != null){
                try{
                    fis.close();
                }catch(Exception e1){}
            }
            if (out!=null) {
            	try{
            		out.flush();
            		out.close();
            	} catch(Exception e2) {}
            }
        }
	}
	
	/**
	 * 
	 * @param key
	 * @param res
	 * @return
	 */
	public boolean downloadOne(String key, HttpServletResponse res) {
		
		File destFolder = new File(DEST_FOLDER);
		if (!destFolder.exists()) {
			destFolder.mkdirs();
		}
		String timestamp = sdf.format(Calendar.getInstance().getTime());
		String DEST_FILENAME = "release-" + key + "-" + timestamp + ".zip";
		String destFilePath = DEST_FOLDER + DEST_FILENAME;
		File destFile = new File(destFilePath);
		try {
			destFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		Contents item1 = null;
		Optional<Contents> row1 = crepo.findById(Integer.parseInt(key));
		if (row1.isPresent()) {
			// 기존 파일 업데이트
			item1 = row1.get();
		} else {
			return false;
		}

		if (ApplicationConstant.TYPE_PDF.equals(item1.getType())) {
			ZipUtil.packEntry(new File(SRC_FOLDER + key + ".pdf"), destFile);
		} else {
			String resourcePath = SRC_FOLDER + ApplicationConstant.UPLOAD_REL_PATH + File.separator + key;
			File resourceDir = new File(resourcePath);
			if (resourceDir.exists() && resourceDir.isDirectory()) {
				ZipUtil.pack(new File(resourcePath), destFile, new NameMapper() {
					@Override
					public String map(String name) {
						 return ApplicationConstant.UPLOAD_REL_PATH + "/" + key + "/" + name;
					}
				});
				ZipUtil.addEntry(destFile, key + ".html", new File(SRC_FOLDER + key + ".html"));
			} else {
				ZipUtil.packEntry(new File(SRC_FOLDER + key + ".html"), destFile);
			}
		}
		
		FileInputStream fis = null;
		ServletOutputStream out = null;
		try {
			fis = new FileInputStream(destFile); 			// file not found exception || 엑세스가 거부되었습니다
			res.setHeader(HttpHeaders.PRAGMA, 				"no-cache");
			res.setHeader(HttpHeaders.CONTENT_TYPE, 		"application/zip");
			res.setHeader(HttpHeaders.CONTENT_LENGTH, 		"" + destFile.length());
			res.setHeader(HttpHeaders.CONTENT_DISPOSITION, 	"attachment; filename=\"" + DEST_FILENAME + "\"");
			out = res.getOutputStream();
            FileCopyUtils.copy(fis, out);
			return true;
		} catch(FileNotFoundException e1){
			e1.printStackTrace();
			return false;
        } catch(Exception e2){
        	e2.printStackTrace();
			return false;
        } finally {
            if(fis != null){
                try{
                    fis.close();
                }catch(Exception e1){}
            }
            if (out!=null) {
            	try{
            		out.flush();
            		out.close();
            	} catch(Exception e2) {}
            }
        }
	}

	/**
	 * 
	 * @param id
	 * @param res
	 * @return
	 */
	public boolean downloadFromHistory(Integer id, HttpServletResponse res) {
		
    	Optional<ReleaseHistory> row = rhrepo.findById(id);
		if (!row.isPresent()) {
			return false;
		}
				
		String DEST_FILENAME = row.get().getFileName();
		String destFilePath = DEST_FOLDER + DEST_FILENAME;
		File destFile = new File(destFilePath);

		FileInputStream fis = null;
		ServletOutputStream out = null;
		
		try {
			fis = new FileInputStream(destFile); 			// file not found exception || 엑세스가 거부되었습니다
			res.setHeader(HttpHeaders.PRAGMA, 				"no-cache");
			res.setHeader(HttpHeaders.CONTENT_TYPE, 		"application/zip");
			res.setHeader(HttpHeaders.CONTENT_LENGTH, 		"" + destFile.length());
			res.setHeader(HttpHeaders.CONTENT_DISPOSITION, 	"attachment; filename=\"" + DEST_FILENAME + "\"");
			out = res.getOutputStream();
            FileCopyUtils.copy(fis, out);
			return true;
		} catch(FileNotFoundException e1){
			e1.printStackTrace();
			return false;
        } catch(Exception e2){
        	e2.printStackTrace();
			return false;
        } finally {
            if(fis != null){
                try{
                    fis.close();
                }catch(Exception e1){}
            }
            if (out!=null) {
            	try{
            		out.flush();
            		out.close();
            	} catch(Exception e2) {}
            }
        }
	}

	/**
	 * 
	 * @param res
	 * @return
	 */
	@Transactional
	public boolean downloadChanged(String[] fileIds, HttpServletResponse res, String username, Boolean release) {
		
		if (fileIds==null || fileIds.length<1) {
			return false;
		}
		
		File destFolder = new File(DEST_FOLDER);
		if (!destFolder.exists()) {
			destFolder.mkdirs();
		}
		String timestamp = sdf.format(Calendar.getInstance().getTime());
		String DEST_FILENAME = "release-part-" + timestamp + ".zip";
		String destFilePath = DEST_FOLDER + DEST_FILENAME;
		File destFile = new File(destFilePath);
		try {
			destFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		int i = 0;
		for (String fileId : fileIds) {
			// 도움말그룹인 경우 파일만, 도움말인 경우 파일과 폴더
			String fileName = fileId + ".html";
			File file = new File(SRC_FOLDER + fileName);
			if (file.exists() && file.isFile()) {
				if (i==0) {
					ZipUtil.packEntry(file, destFile);
				} else {
					ZipUtil.addEntry(destFile, fileName, file);
				}
				String resourcePath = SRC_FOLDER + ApplicationConstant.UPLOAD_REL_PATH + File.separator + fileId;
				File resourceDir = new File(resourcePath);
				if (resourceDir.exists() && resourceDir.isDirectory()) {
					// 도움말 하위 폴더
					ZipUtil.addEntry(resourceDir, destFile, ApplicationConstant.UPLOAD_REL_PATH + File.separator + fileId);
				}	
			}
			i++;
		}
		
		// Todo:
		if (release) {
        	ReleaseHistory item = new ReleaseHistory();
        	item.setType(ApplicationConstant.RELEASE_PART);
        	item.setFileName(DEST_FILENAME);
        	item.setUserid(username);
        	rhrepo.save(item);
			chdao.addHistory(username, ApplicationConstant.TYPE_RELEASE, ApplicationConstant.METHOD_ADD, 
					String.valueOf(item.getId()), destFile.getName());
		}
		
		FileInputStream fis = null;
		ServletOutputStream out = null;
		try {
			fis = new FileInputStream(destFile); 			// file not found exception || 엑세스가 거부되었습니다
			res.setHeader(HttpHeaders.PRAGMA, 				"no-cache");
			res.setHeader(HttpHeaders.CONTENT_TYPE, 		"application/zip");
			res.setHeader(HttpHeaders.CONTENT_LENGTH, 		"" + destFile.length());
			res.setHeader(HttpHeaders.CONTENT_DISPOSITION, 	"attachment; filename=\"" + DEST_FILENAME + "\"");
			out = res.getOutputStream();
            FileCopyUtils.copy(fis, out);
			return true;
		} catch(FileNotFoundException e1){
			e1.printStackTrace();
			return false;
        } catch(Exception e2){
        	e2.printStackTrace();
			return false;
        } finally {
            if(fis != null){
                try{
                    fis.close();
                }catch(Exception e1){}
            }
            if (out!=null) {
            	try{
            		out.flush();
            		out.close();
            	} catch(Exception e2) {}
            }
        }
	}
	
	
	public List<ChangeHistory> getAllChangesOrderByNameAsc() {
		List<ChangeHistory> ret = chdao.findAllChangedByName();
		for (ChangeHistory item : ret) {
			String status = "";
			if (ApplicationConstant.METHOD_ADD.equals(item.getMethod())) {
				status = "fi-plus";
			} else if (ApplicationConstant.METHOD_MODIFY.equals(item.getMethod())
					|| ApplicationConstant.METHOD_RENAME.equals(item.getMethod())) {
				status = "fi-page-edit";
			} else if (ApplicationConstant.METHOD_DELETE.equals(item.getMethod())) {
				status = "fi-minus";
				item.setDel(true);
			}
			item.setFileId(item.getFilePath().replace(".html", ""));
			item.setStatus(status);
		}
		return ret;
	}
	
	public List<ChangeHistory> getAllChangesExcludeReleaseOrderByNameAsc() {
		List<ChangeHistory> ret = chdao.findAllChangedFileIdsExcludeRelease();
		for (ChangeHistory item : ret) {
			String status = "";
			if (ApplicationConstant.METHOD_ADD.equals(item.getMethod())) {
				status = "fi-plus";
			} else if (ApplicationConstant.METHOD_MODIFY.equals(item.getMethod())
					|| ApplicationConstant.METHOD_RENAME.equals(item.getMethod())) {
				status = "fi-page-edit";
			} else if (ApplicationConstant.METHOD_DELETE.equals(item.getMethod())) {
				status = "fi-minus";
				item.setDel(true);
			}
			item.setFileId(item.getFilePath().replace(".html", ""));
			item.setStatus(status);
		}
		return ret;
	}
	
	
	public List<ChangeHistory> getAllChangesByMe(String userid) {
		List<ChangeHistory> ret = chdao.findAllChangedByMe(userid);
		return ret;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public List<ChangeHistory> getHistories() {
		List<ChangeHistory> ret = chdao.getHistories();
		if (ret!=null && ret.size()>0) {
			for (ChangeHistory item : ret) {
				String status = "";
				String type   = "";
				if (ApplicationConstant.METHOD_ADD.equals(item.getMethod())) {
					status = "fi-plus";
				} else if (ApplicationConstant.METHOD_MODIFY.equals(item.getMethod())) {
					status = "fi-page-edit";
				} else if (ApplicationConstant.METHOD_DELETE.equals(item.getMethod())) {
					status = "fi-minus";
				} else  if (ApplicationConstant.METHOD_RENAME.equals(item.getMethod())) {
					status = "fi-text-color";
				}
				item.setStatus(status);
				if (ApplicationConstant.TYPE_FOLDER.equals(item.getType())) {
					type = "폴더";
				} else if (ApplicationConstant.TYPE_CONTENT.equals(item.getType())) {
					type = "도움말";
				} else  if (ApplicationConstant.TYPE_GROUP.equals(item.getType())) {
					type = "도움말그룹";
				} else  if (ApplicationConstant.TYPE_RELEASE.equals(item.getType())) {
					type = "배포";
					item.setTitle("배포버전 v" + item.getTitle());
					item.setClassName("tr-release");
				}
				if (item.getReleased()!=null && item.getReleased()=='Y') {
					item.setClassName("tr-released");
					logger.debug("tr-released");
				}
				item.setType(type);
			}
		}
		return ret;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public List<ReleaseHistory> getReleases() {
		List<ReleaseHistory> ret = rhrepo.findTop300ByOrderByUpdDtDesc();
		for (ReleaseHistory item : ret) {
			if (ApplicationConstant.RELEASE_ALL.equals(item.getType())) {
				item.setTypeKr("전체파일");
			} else {
				item.setTypeKr("변경파일");
			}
		}
		return ret;
	}
	
	/**
	 * 불필요 작업 히스토리 삭제
	 */
	public void deleteUnusedHistories() {
		chdao.deleteUnused();
	}
	
}
