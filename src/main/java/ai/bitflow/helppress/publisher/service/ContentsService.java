package ai.bitflow.helppress.publisher.service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.bitflow.helppress.publisher.dao.FileDao;
import ai.bitflow.helppress.publisher.domain.ChangeHistory;
import ai.bitflow.helppress.publisher.domain.Contents;
import ai.bitflow.helppress.publisher.repository.ChangeHistoryRepository;
import ai.bitflow.helppress.publisher.repository.ContentsRepository;
import ai.bitflow.helppress.publisher.vo.req.ContentsReq;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ContentsService {

	private final Logger logger = LoggerFactory.getLogger(ContentsService.class);
	
	@Autowired
	private ContentsRepository contentsrepo;
	
	@Autowired
	private ChangeHistoryRepository hrepo;
	
	@Autowired
	private FileDao fdao;
	
	/**
	 * 새 "빈" 컨텐츠 추가
	 * e.g.) String textOnly = Jsoup.parse(params.getContent()).text();
	 * @param item
	 */
	@Transactional
	public Contents newContent(ContentsReq params) {
		
		Contents item1 = new Contents();
		item1.setTitle(params.getTitle());
		Contents item2 = contentsrepo.save(item1);
		fdao.newContentFile(item2);
		
		// 변경이력 저장
		ChangeHistory item3 = new ChangeHistory();
		item3.setType("CONTENT");
		item3.setMethod("ADD");
		item3.setFilePath(String.format("%05d", item2.getId()) + ".html");
		item3.setComment(params.getComment());
		hrepo.save(item3);
		
		return item2;
	}
	
	/**
	 * 폴더 추가
	 * @param params
	 * @return
	 */
	@Transactional
	public String newFolder(ContentsReq params) {
		Contents item1 = new Contents();
		item1.setTitle("");
		Integer key = contentsrepo.save(item1).getId();
		contentsrepo.delete(item1);
		return String.format("%05d", key);
	}

	/**
	 * 컨텐츠 삭제
	 * @param params
	 * @param key
	 * @return
	 */
	@Transactional
	public String updateContent(ContentsReq params, String key) {
		// id가 폴더이면 childDoc, id가 파일이면 업데이트
		Optional<Contents> row1 = contentsrepo.findById(Integer.parseInt(key));
		if (row1.isPresent()) {
			// 기존 파일 업데이트
			Contents item1 = row1.get();
			item1.setContent(params.getContent());
			Contents item2 = contentsrepo.save(item1);
			fdao.newContentFile(item2);
			
			// 변경이력 저장 - 1분이내 수정건은 업데이트
			String type = "CONTENT";
			String method = "MOD";
			String filePath = String.format("%05d", item2.getId()) + ".html";
			LocalDateTime prevTime = LocalDateTime.now().withSecond(0).withNano(0);
			Optional<ChangeHistory> row2 = hrepo.findTopByTypeAndMethodAndFilePathAndUpdDtGreaterThanEqual
					(type, method, filePath, prevTime);
			ChangeHistory item3 = null;
			if (row2.isPresent()) {
				item3 = row2.get();
			} else {
				item3 = new ChangeHistory();
			}
			item3.setType(type);
			item3.setMethod(method);
			item3.setFilePath(filePath);
			hrepo.save(item3);
		
			return String.valueOf(item2.getId());
		}
		return null;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Contents getContents(String id) {
		Optional<Contents> row = contentsrepo.findById(Integer.parseInt(id));
		return row.isPresent()?row.get():null;
	}
	
	@Transactional
	public boolean deleteContent(String id) {
		Optional<Contents> row = contentsrepo.findById(Integer.parseInt(id));
		if (row.isPresent()) {
			Contents item = row.get();
			contentsrepo.delete(item);
			
			// 변경이력 저장
			ChangeHistory item3 = new ChangeHistory();
			item3.setType("CONTENT");
			item3.setMethod("DEL");
			item3.setFilePath(String.format("%05d", item.getId()) + ".html");
			hrepo.save(item3);
		
		}
		return true;
	}

}
