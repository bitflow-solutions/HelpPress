package ai.bitflow.helppress.publisher.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.zeroturnaround.zip.ZipUtil;

import ai.bitflow.helppress.publisher.dao.FileDao;
import ai.bitflow.helppress.publisher.domain.ChangeHistory;
import ai.bitflow.helppress.publisher.repository.ChangeHistoryRepository;
import lombok.extern.slf4j.Slf4j;


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
	private ChangeHistoryRepository hrepo;
	
	@Autowired
	private FileDao fdao;
	
	
	/**
	 * 
	 * @param res
	 * @return
	 */
	public boolean downloadAll(HttpServletResponse res) {
		
		String timestamp = sdf.format(Calendar.getInstance().getTime());
		String DEST_FILENAME = "cms-release-" + timestamp + ".zip";
		String destFilePath = DEST_FOLDER + DEST_FILENAME;
		File destFile = new File(destFilePath);
		ZipUtil.pack(new File(SRC_FOLDER), destFile);

		FileInputStream fis = null;
		ServletOutputStream out = null;
		// cms-release-yyyymmddhhmmss
		try {
			fis = new FileInputStream(destFile); 			// file not found exception || 엑세스가 거부되었습니다
			res.setHeader(HttpHeaders.PRAGMA, 				"no-cache");
			res.setHeader(HttpHeaders.CONTENT_TYPE, 		"application/pdf");
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
		return hrepo.findAllByOrderByUpdDtDesc();
	}
	
}
