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
import ai.bitflow.helppress.publisher.domain.ChangeHistory;
import ai.bitflow.helppress.publisher.domain.Contents;
import ai.bitflow.helppress.publisher.repository.ContentsRepository;
import ai.bitflow.helppress.publisher.vo.req.DeleteNodeReq;
import ai.bitflow.helppress.publisher.vo.req.NewNodeReq;
import ai.bitflow.helppress.publisher.vo.req.UpdateNodeReq;
import ai.bitflow.helppress.publisher.vo.res.result.NodeUpdateResult;


@Service
public class NodeService {

	private final Logger logger = LoggerFactory.getLogger(NodeService.class);
	
	@Autowired
	private ContentsRepository contentsrepo;
	
	@Autowired
	private ChangeHistoryDao chdao;
	
	@Autowired
	private FileDao fdao;
	
	/**
	 * 새 "빈" 컨텐츠 추가
	 * e.g.) String textOnly = Jsoup.parse(params.getContent()).text();
	 * @param item
	 */
	@Transactional
	public NodeUpdateResult newNode(NewNodeReq params, String userid) {
		
		String method = ApplicationConstant.ADD;
		String title = "";
		
		NodeUpdateResult ret = new NodeUpdateResult();
		ret.setMethod(method);
		
		Contents item1 = new Contents();
		if (params.getFolder()==null || params.getFolder()==false) {
			title = "새 도움말";
			item1.setAuthor(userid);		
		} else {
			title = "새 폴더";
		}
		item1.setTitle(title);
		
		// 테이블 저장 후 ID 반환
		contentsrepo.save(item1);
		// 파일 생성
		String contentidstr = String.format("%05d", item1.getId());
		String groupid = params.getGroupId();
		if (params.getFolder()==null || params.getFolder()==false) {
			fdao.newContentFile(item1, contentidstr);
		} else {
			contentsrepo.delete(item1);
		}
		
		// 변경이력 저장
		String type = "";
		String filePath = null;
		if (params.getFolder()==null || params.getFolder()==false) {
			type = "CONTENT";
		} else {
			type = "FOLDER";
		}
		filePath = groupid + ".html";
		
		chdao.addHistory(userid, type, method, title, filePath);
		
		ret.setParentKey(params.getParentKey());
		ret.setGroupId(params.getGroupId());
		ret.setFolder(params.getFolder());
		ret.setKey(contentidstr);
		ret.setTitle(title);
		
		return ret;
	}
	
	/**
	 * 노드 삭제 - 폴더인 경우 그룹변경, 도움말인 경우 그룹변경 + 컨텐츠 삭제
	 * @param params
	 * @return
	 */
	@Transactional
	public NodeUpdateResult deleteNode(DeleteNodeReq params, String userid) {
		
		String method = ApplicationConstant.DELETE;
		String type = "";
		
		NodeUpdateResult ret = new NodeUpdateResult();
		ret.setGroupId(params.getGroupId());
		ret.setMethod(method);
		ret.setKey(params.getKey());
		
		// 변경 이력
		ChangeHistory item2 = new ChangeHistory();
		item2.setMethod(method);
		
		if (params.getFolder()==null || params.getFolder()==false) {
			// 1. 도움말인 경우
			type = "CONTENT";
			item2.setType(type);
			item2.setFilePath(params.getKey() + ".html");
			// 1) 테이블 행삭제
			Optional<Contents> row = contentsrepo.findById(Integer.parseInt(params.getKey()));
			if (row.isPresent()) {
				Contents item1 = row.get();
				contentsrepo.delete(item1);
			}
			// 2) 파일 삭제
			boolean success = fdao.deleteFile(params.getKey());
			// 3) Todo: 첨부 이미지 폴더 삭제
		} else {
			type = "FOLDER";
			item2.setType(type);
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
		}
		
		// 변경이력 저장 - 도움말 또는 그룹
		chdao.addHistory(userid, type, method, params.getTitle(), item2.getFilePath());
		chdao.addHistory(userid, type, ApplicationConstant.MODIFY, params.getTitle(), ret.getGroupId() + ".html");
		
		return ret;
	}
	
	public NodeUpdateResult renameNode(UpdateNodeReq params, String userid) {
		
		NodeUpdateResult ret = new NodeUpdateResult();
		
		String method = ApplicationConstant.RENAME;
		String type = "";
		String filePath = params.getGroupId() + ".html";
		
		if (params.getFolder()!=null && params.getFolder()==true) {
			type = ApplicationConstant.FOLDER;
		} else {
			type = ApplicationConstant.CONTENT;
		}
		
		// 변경이력 저장
		chdao.addHistory(userid, type, method, params.getTitle(), filePath);

		ret.setMethod(method);
		ret.setUsername(userid);
		ret.setKey(params.getKey());
		ret.setGroupId(params.getGroupId());
		ret.setTitle(params.getTitle());
		
		return ret;
	}
}
