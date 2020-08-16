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

import ai.bitflow.helppress.publisher.domain.Category;
import ai.bitflow.helppress.publisher.service.CategoryService;
import ai.bitflow.helppress.publisher.vo.req.CategoryReq;
import ai.bitflow.helppress.publisher.vo.res.CategoryRes;
import ai.bitflow.helppress.publisher.vo.res.GeneralRes;
import ai.bitflow.helppress.publisher.vo.res.result.CategoryResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author method76
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ecm/category") 
public class ApiCategoryController {
	
	private final Logger logger = LoggerFactory.getLogger(ApiCategoryController.class);
	
	@Autowired
	private CategoryService cservice;
	
	/**
	 * 카테고리 저장
	 * @param params
	 * @return
	 */
	@PostMapping("/{categoryId}")
	public GeneralRes postCategory(@PathVariable String categoryId, CategoryReq params) {
		logger.debug("params " + params.toString());
		params.setCategoryId(categoryId);
		String res = cservice.createCategory(params);
		GeneralRes ret = new GeneralRes();
		if (res==null) {
			ret.setFailResponse();
		}
		return ret;
	}
	
	@GetMapping("/{categoryId}")
	public CategoryRes getCategory(@PathVariable String categoryId) {
		Category res = cservice.getCategory(categoryId);
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
	 * 
	 * @param categoryId
	 * @param params
	 * @return
	 */
	@PutMapping("/{categoryId}")
	public CategoryRes putCategory(@PathVariable String categoryId, CategoryReq params) {
		logger.debug("params " + params.toString());
		params.setCategoryId(categoryId);
		Category res = cservice.updateCategory(params);
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
	
	@DeleteMapping("/{categoryId}")
	public GeneralRes categoryDelete(@PathVariable String categoryId) {
		logger.debug("params " + categoryId);
		GeneralRes ret = new GeneralRes();
		try {
			cservice.deleteCategory(categoryId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			ret.setFailResponse();
		}
		return ret;
	}
	
	/**
	 * 카테고리 저장
	 * @param params
	 * @return
	 */
	@GetMapping("")
	public List<Category> getCategories() {
		return cservice.getCategories();
	}
	
}
