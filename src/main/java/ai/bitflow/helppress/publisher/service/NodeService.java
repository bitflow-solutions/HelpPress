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
import ai.bitflow.helppress.publisher.vo.req.DeleteNodeReq;
import ai.bitflow.helppress.publisher.vo.req.NewNodeReq;
import ai.bitflow.helppress.publisher.vo.res.result.NodeUpdateResult;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class NodeService {

	private final Logger logger = LoggerFactory.getLogger(NodeService.class);
	
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
	public NodeUpdateResult newNode(NewNodeReq params) {
		
		NodeUpdateResult ret = new NodeUpdateResult();
		ret.setMethod("ADD");
		
		Contents item1 = new Contents();
		if (params.getFolder()==null || params.getFolder()==false) {
			item1.setTitle("새 도움말");
		} else {
			item1.setTitle("새 폴더");	
		}
		
		// 파일 생성
		contentsrepo.save(item1);
		String idstring = String.format("%05d", item1.getId());
		if (params.getFolder()==null || params.getFolder()==false) {
			fdao.newContentFile(item1, idstring);
		}
		
		// 변경이력 저장
		ChangeHistory item2 = new ChangeHistory();
		if (params.getFolder()==null || params.getFolder()==false) {
			item2.setType("CONTENT");
			item2.setFilePath(idstring + ".html");
		} else {
			item2.setType("FOLDER");
		}
		item2.setMethod("ADD");
		hrepo.save(item2);
		
		ret.setParentKey(params.getParentKey());
		ret.setGroupId(params.getGroupId());
		ret.setFolder(params.getFolder());
		ret.setKey(idstring);
		ret.setTitle(item1.getTitle());
		
		return ret;
	}
	
	/**
	 * 노드 삭제
	 * @param params
	 * @return
	 */
	@Transactional
	public NodeUpdateResult deleteNode(DeleteNodeReq params) {
		
		NodeUpdateResult ret = new NodeUpdateResult();
		ret.setGroupId(params.getGroupId());
		ret.setMethod("DEL");
		ret.setKey(params.getKey());
		
		// 변경 이력
		ChangeHistory item2 = new ChangeHistory();
		item2.setMethod("DEL");
		
		if (params.getFolder()==null || params.getFolder()==false) {
			// 1. 도움말인 경우
			item2.setType("CONTENT");
			item2.setFilePath(params.getKey() + ".html");
			// 1) 테이블 행삭제
			Optional<Contents> row = contentsrepo.findById(Integer.parseInt(params.getKey()));
			if (row.isPresent()) {
				Contents item1 = row.get();
				contentsrepo.delete(item1);
			}
			// 2) 파일 삭제
			boolean success = fdao.deleteFile(params.getKey());
		} else {
			item2.setType("FOLDER");
			// 2. 폴더인 경우: 하위 노드도 삭제
			if (params.getChild()!=null && params.getChild().size()>0) {
				for (String contentKey : params.getChild()) {
					// 1) 테이블 행삭제
					Optional<Contents> row = contentsrepo.findById(Integer.parseInt(contentKey));
					if (row.isPresent()) {
						Contents item1 = row.get();
						contentsrepo.delete(item1);
					}
					// 2) 파일 삭제
					boolean success = fdao.deleteFile(contentKey);
				}
			}
			item2.setFilePath(params.getKey());
		}
		// 변경이력 저장
		hrepo.save(item2);
		return ret;
	}
	
}
