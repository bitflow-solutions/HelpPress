package ai.bitflow.helppress.publisher.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.bitflow.helppress.publisher.dao.FileDao;
import ai.bitflow.helppress.publisher.domain.ChangeHistory;
import ai.bitflow.helppress.publisher.domain.ContentsGroup;
import ai.bitflow.helppress.publisher.repository.ChangeHistoryRepository;
import ai.bitflow.helppress.publisher.repository.ContentsGroupRepository;
import ai.bitflow.helppress.publisher.vo.req.ContentsGroupReq;
import lombok.extern.slf4j.Slf4j;

/**
 * 도움말그룹 관련 서비스
 * @author 김성준
 */
@Slf4j
@Service
public class ContentsGroupService {

	private final Logger logger = LoggerFactory.getLogger(ContentsGroupService.class);
	
	@Autowired
	private ContentsGroupRepository grepo;
	
	@Autowired
	private ChangeHistoryRepository hrepo;
	
	@Autowired
	private FileDao fdao;
	
	
	/**
	 * 전체 카테고리 조회
	 * @return
	 */
	public List<ContentsGroup> getGroups() {
		return grepo.findAllByOrderByOrderNo();
    }
	
	/**
	 * 도움말그룹 조회
	 * @param id
	 * @return
	 */
	public ContentsGroup getGroup(String id) {
		Optional<ContentsGroup> row = grepo.findById(id);
		if (row.isPresent()) {
			return row.get();
		} else {
			return null;
		}
    }
	
	/**
	 * 도움말그룹 생성
	 * @param params
	 * @return
	 */
	@Transactional
    public String createGroup(ContentsGroupReq params) {
		ContentsGroup item = new ContentsGroup();
		item.setCategoryId(params.getCategoryId());
		item.setName(params.getName());
		item.setOrderNo(params.getOrderNo());
		String ret = grepo.save(item).getCategoryId();
		
		List<ContentsGroup> list = grepo.findAll();
		fdao.makeAllContentGroupHTML(list);
		
		// 변경이력 저장
		ChangeHistory item3 = new ChangeHistory();
		item3.setType("GROUP");
		item3.setMethod("ADD");
		item3.setFilePath(params.getCategoryId() + ".html");
		hrepo.save(item3);
		
		return ret;
	}
	
	/**
	 * 도움말그룹 수정
	 * @param params
	 * @return
	 */
	@Transactional
    public ContentsGroup updateGroup(ContentsGroupReq params) {
		Optional<ContentsGroup> row = grepo.findById(params.getCategoryId());
		ContentsGroup item1 = null;
		if (!row.isPresent()) {
			return null;
		} else {
			item1 = row.get();
			if (params.getTree()!=null) {
				item1.setTree(params.getTree());
			}
			if (params.getName()!=null) {
				item1.setName(params.getName());
			}
			if (params.getOrderNo()!=null) {
				item1.setOrderNo(params.getOrderNo());
			}
		}
		ContentsGroup ret = grepo.save(item1);
		
		List<ContentsGroup> list = grepo.findAll();
		fdao.makeAllContentGroupHTML(list);
		
		// 변경이력 저장
		ChangeHistory item3 = new ChangeHistory();
		item3.setType("GROUP");
		item3.setMethod("MOD");
		item3.setFilePath(params.getCategoryId() + ".html");
		hrepo.save(item3);
		
		return ret;
    }
	
	/**
	 * 도움말그룹 삭제
	 * @param id
	 */
	@Transactional
    public void deleteGroup(String id) {
		grepo.deleteById(id);
		
		List<ContentsGroup> list = grepo.findAll();
		fdao.makeAllContentGroupHTML(list);
		
		// 변경이력 저장
		ChangeHistory item3 = new ChangeHistory();
		item3.setType("GROUP");
		item3.setMethod("DEL");
		item3.setFilePath(id + ".html");
		hrepo.save(item3);
    }
	
}
