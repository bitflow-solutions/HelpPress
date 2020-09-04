package ai.bitflow.helppress.publisher.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.bitflow.helppress.publisher.constant.ApplicationConstant;
import ai.bitflow.helppress.publisher.dao.ChangeHistoryDao;
import ai.bitflow.helppress.publisher.dao.FileDao;
import ai.bitflow.helppress.publisher.domain.Contents;
import ai.bitflow.helppress.publisher.repository.ChangeHistoryRepository;
import ai.bitflow.helppress.publisher.repository.ContentsRepository;
import ai.bitflow.helppress.publisher.vo.req.ContentsReq;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ContentsService implements ApplicationConstant {

	private final Logger logger = LoggerFactory.getLogger(ContentsService.class);
	
	@Autowired
	private ContentsRepository contentsrepo;
	
	@Autowired
	private ChangeHistoryRepository hrepo;
	
	@Autowired
	private ChangeHistoryDao chdao;
	
	@Autowired
	private FileDao fdao;
	
	/**
	 * 새 "빈" 컨텐츠 추가
	 * e.g.) String textOnly = Jsoup.parse(params.getContent()).text();
	 * @param item
	 */
//	@Transactional
//	public Contents newContent(ContentsReq params) {
//		
//		Contents item1 = new Contents();
//		item1.setTitle(params.getTitle());
//		Contents item2 = contentsrepo.save(item1);
//		fdao.newContentFile(item2);
//		
//		// 변경이력 저장
//		ChangeHistory item3 = new ChangeHistory();
//		item3.setType(TYPE_CONTENT);
//		item3.setMethod(METHOD_ADD);
//		item3.setFilePath(String.format("%05d", item2.getId()) + ".html");
//		item3.setComment(params.getComment());
//		hrepo.save(item3);
////		chdao.addHistory(userid, type, method, null, filePath);
//		return item2;
//	}
	
	/**
	 * 폴더 추가
	 * @param params
	 * @return
	 */
//	@Transactional
//	public String newFolder(ContentsReq params) {
//		Contents item1 = new Contents();
//		item1.setTitle("");
//		Integer key = contentsrepo.save(item1).getId();
//		contentsrepo.delete(item1);
//		return String.format("%05d", key);
//	}

	/**
	 * 컨텐츠 삭제
	 * @param params
	 * @param key
	 * @return
	 */
	@Transactional
	public String updateContent(ContentsReq params, String key, String userid) {
		// id가 폴더이면 childDoc, id가 파일이면 업데이트
		Optional<Contents> row1 = contentsrepo.findById(Integer.parseInt(key));
		if (row1.isPresent()) {
			// 기존 파일 업데이트
			Contents item1 = row1.get();
			item1.setContent(params.getContent());
			Contents item2 = contentsrepo.save(item1);
			fdao.newContentFile(item2);
			
			// 변경이력 저장
			String type     = TYPE_CONTENT;
			String method   = METHOD_MODIFY;
			String filePath = String.format("%05d", item2.getId()) + ".html";
			chdao.addHistory(userid, type, method, null, filePath);
		
			return String.valueOf(item2.getId());
		}
		return null;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Contents getContent(String id) {
		Optional<Contents> row = contentsrepo.findById(Integer.parseInt(id));
		return row.isPresent()?row.get():null;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public boolean deleteContent(String id, String userid) {
		Optional<Contents> row = contentsrepo.findById(Integer.parseInt(id));
		if (row.isPresent()) {
			Contents item = row.get();
			contentsrepo.delete(item);
			
			// 변경이력 저장
			chdao.addHistory(userid, TYPE_CONTENT, METHOD_DELETE, null, String.format("%05d", item.getId()) + ".html");
		}
		return true;
	}

}
