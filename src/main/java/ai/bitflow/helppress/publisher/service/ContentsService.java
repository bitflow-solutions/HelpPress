package ai.bitflow.helppress.publisher.service;

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
import ai.bitflow.helppress.publisher.vo.req.NewContentReq;
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
	public Contents newContent(NewContentReq params) {
		
		Contents item1 = new Contents();
		item1.setTitle(params.getTitle());
		Contents item2 = contentsrepo.save(item1);
		fdao.newContentFile(item2);
		
		// 변경이력 저장
		ChangeHistory item3 = new ChangeHistory();
		item3.setType("CONTENT");
		item3.setMethod("ADD");
		item3.setFilePath(String.format("%05d", item2.getId()) + ".html");
		hrepo.save(item3);
		
		return item2;
	}

	/**
	 * 컨텐츠 삭제
	 * @param params
	 * @param key
	 * @return
	 */
	@Transactional
	public String updateContent(NewContentReq params, String key) {
		// id가 폴더이면 childDoc, id가 파일이면 업데이트
		Optional<Contents> row1 = contentsrepo.findById(Integer.parseInt(key));
		if (row1.isPresent()) {
			// 기존 파일 업데이트
			Contents item1 = row1.get();
			item1.setContent(params.getContent());
			Contents item2 = contentsrepo.save(item1);
			fdao.newContentFile(item2);
			
			// 변경이력 저장
			ChangeHistory item3 = new ChangeHistory();
			item3.setType("CONTENT");
			item3.setMethod("MOD");
			item3.setFilePath(String.format("%05d", item2.getId()) + ".html");
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
