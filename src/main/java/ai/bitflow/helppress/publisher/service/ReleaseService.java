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
import ai.bitflow.helppress.publisher.domain.ReleaseHistory;
import ai.bitflow.helppress.publisher.repository.ReleaseHistoryRepository;
import lombok.extern.slf4j.Slf4j;


/**
 * 배포 파일 처리 서비스
 * @author 김성준
 */
@Slf4j
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
	private ChangeHistoryDao chdao;
	
	
	/**
	 * 
	 * @param res
	 * @return
	 */
	@Transactional
	public boolean downloadAll(Boolean release, HttpServletResponse res) {
		
		boolean released = release!=null?release:false;
		String timestamp = sdf.format(Calendar.getInstance().getTime());
		String DEST_FILENAME = "release-all-" + timestamp + ".zip";
		if (released) {
        	ReleaseHistory item = new ReleaseHistory();
        	item.setType(ApplicationConstant.RELEASE_ALL);
        	item.setFileName(DEST_FILENAME);
        	rhrepo.save(item);
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
	 * @return
	 */
	public List<ChangeHistory> getHistories() {
		List<ChangeHistory> ret = chdao.getHistories();
		if (ret!=null && ret.size()>0) {
			for (ChangeHistory item : ret) {
				String status = "";
				if (ApplicationConstant.ADD.equals(item.getMethod())) {
					status = "post_add";
				} else if (ApplicationConstant.MODIFY.equals(item.getMethod())) {
					status = "update";
				} else if (ApplicationConstant.DELETE.equals(item.getMethod())) {
					status = "delete_outline";
				} else  if (ApplicationConstant.RENAME.equals(item.getMethod())) {
					status = "text_fields";
				}
				item.setStatus(status);
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
	
}
