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
import ai.bitflow.helppress.publisher.vo.res.CategoryRes;
import ai.bitflow.helppress.publisher.vo.res.GeneralRes;
import ai.bitflow.helppress.publisher.vo.res.result.CategoryResult;
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
	@PostMapping("/{categoryId}")
	public GeneralRes postGroup(@PathVariable String categoryId, ContentsGroupReq params) {
		logger.debug("params " + params.toString());
		params.setCategoryId(categoryId);
		String res = gservice.createGroup(params);
		GeneralRes ret = new GeneralRes();
		if (res==null) {
			ret.setFailResponse();
		}
		return ret;
	}
	
	@GetMapping("/{categoryId}")
	public CategoryRes getGroup(@PathVariable String categoryId) {
		ContentsGroup res = gservice.getGroup(categoryId);
		CategoryRes ret = new CategoryRes();
		if (res!=null) {
			CategoryResult rst = new CategoryResult();
			rst.setCategoryId(res.getCategoryId());
			rst.setTree(res.getTree());
			ret.setResult(rst);
		}
		return ret;
	}
	
	/**
	 * 도움말그룹 수정
	 * @param categoryId
	 * @param params
	 * @return
	 */
	@PutMapping("/{categoryId}")
	public CategoryRes putGroup(@PathVariable String categoryId, ContentsGroupReq params) {
		logger.debug("params " + params.toString());
		params.setCategoryId(categoryId);
		ContentsGroup res = gservice.updateGroup(params);
		CategoryRes ret = new CategoryRes();
		if (res==null) {
			ret.setFailResponse();
		} else {
			CategoryResult rst = new CategoryResult();
			rst.setCategoryId(res.getCategoryId());
			rst.setTree(res.getTree());
			ret.setResult(rst);
		}
		return ret;
	}
	
	/**
	 * 도움말그룹 삭제
	 * @param categoryId
	 * @return
	 */
	@DeleteMapping("/{categoryId}")
	public GeneralRes deleteGroup(@PathVariable String categoryId) {
		logger.debug("params " + categoryId);
		GeneralRes ret = new GeneralRes();
		try {
			gservice.deleteGroup(categoryId);
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
