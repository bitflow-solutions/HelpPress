package ai.bitflow.helppress.publisher.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.bitflow.helppress.publisher.domain.ContentsGroup;
import ai.bitflow.helppress.publisher.service.ContentsGroupService;
import ai.bitflow.helppress.publisher.vo.req.ContentsGroupReq;
import ai.bitflow.helppress.publisher.vo.res.ContentsGroupRes;
import ai.bitflow.helppress.publisher.vo.res.GeneralRes;
import ai.bitflow.helppress.publisher.vo.res.result.ContentsGroupResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 도움말그룹 관련 API
 * @author 김성준
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ecm/group") 
public class ApiContentsGroupController {
	
	private final Logger logger = LoggerFactory.getLogger(ApiContentsGroupController.class);
	
	@Autowired
	private ContentsGroupService gservice;
	
	
	/**
	 * 도움말그룹 저장
	 * @param params
	 * @return
	 */
	@PostMapping("/{groupId}")
	public GeneralRes postGroup(@PathVariable String groupId, ContentsGroupReq params) {
		logger.debug("params " + params.toString());
		params.setGroupId(groupId);
		String res = gservice.createGroup(params);
		GeneralRes ret = new GeneralRes();
		if (res==null) {
			ret.setFailResponse();
		}
		return ret;
	}
	
	@GetMapping("/{groupId}")
	public ContentsGroupRes getGroup(@PathVariable String groupId) {
		ContentsGroup res = gservice.getGroup(groupId);
		ContentsGroupRes ret = new ContentsGroupRes();
		if (res!=null) {
			ContentsGroupResult rst = new ContentsGroupResult();
			rst.setGroupId(res.getGroupId());
			rst.setTree(res.getTree());
			ret.setResult(rst);
		}
		return ret;
	}
	
	/**
	 * 도움말그룹 수정
	 * @param groupId
	 * @param params
	 * @return
	 */
	@PutMapping("/{groupId}")
	public ContentsGroupRes putGroup(@PathVariable String groupId, ContentsGroupReq params) {
		logger.debug("params " + params.toString());
		params.setGroupId(groupId);
		ContentsGroup res = gservice.updateGroup(params);
		ContentsGroupRes ret = new ContentsGroupRes();
		if (res==null) {
			ret.setFailResponse();
		} else {
			ContentsGroupResult rst = new ContentsGroupResult();
			rst.setGroupId(res.getGroupId());
			rst.setTree(res.getTree());
			ret.setResult(rst);
		}
		return ret;
	}
	
	/**
	 * 도움말그룹 삭제
	 * @param groupId
	 * @return
	 */
	@DeleteMapping("/{groupId}")
	public GeneralRes deleteGroup(@PathVariable String groupId) {
		logger.debug("params " + groupId);
		GeneralRes ret = new GeneralRes();
		try {
			gservice.deleteGroup(groupId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			ret.setFailResponse();
		}
		return ret;
	}
	
	/**
	 * 도움말그룹 목록 조회
	 * @param params
	 * @return
	 */
	@GetMapping("")
	public List<ContentsGroup> getGroups() {
		return gservice.getGroups();
	}
	
}
