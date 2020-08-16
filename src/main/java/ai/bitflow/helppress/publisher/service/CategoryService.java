package ai.bitflow.helppress.publisher.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring5.SpringTemplateEngine;

import ai.bitflow.helppress.publisher.dao.FileDao;
import ai.bitflow.helppress.publisher.domain.Category;
import ai.bitflow.helppress.publisher.domain.ChangeHistory;
import ai.bitflow.helppress.publisher.repository.CategoryRepository;
import ai.bitflow.helppress.publisher.repository.ChangeHistoryRepository;
import ai.bitflow.helppress.publisher.vo.req.CategoryReq;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class CategoryService {

	private final Logger logger = LoggerFactory.getLogger(CategoryService.class);
	
	@Autowired
	private CategoryRepository crepo;
	
	@Autowired
	private ChangeHistoryRepository hrepo;
	
    @Autowired
    private SpringTemplateEngine tengine;
	
	@Autowired
	private FileDao fdao;
	
	/**
	 * 전체 카테고리 조회
	 * @return
	 */
	public List<Category> getCategories() {
		return crepo.findAllByOrderByOrderNo();
    }
	
	/**
	 * 카테고리 1건 조회
	 */
	public Category getCategory(String id) {
		Optional<Category> row = crepo.findById(id);
		if (row.isPresent()) {
			return row.get();
		} else {
			return null;
		}
    }
	
	/**
	 * 카테고리 생성
	 * @param params
	 * @return
	 */
	@Transactional
    public String createCategory(CategoryReq params) {
		Category item = new Category();
		item.setCategoryId(params.getCategoryId());
		item.setName(params.getName());
		item.setOrderNo(params.getOrderNo());
		String ret = crepo.save(item).getCategoryId();
		
		List<Category> list = crepo.findAll();
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
	 * 카테고리 수정
	 * @param params
	 * @return
	 */
	@Transactional
    public Category updateCategory(CategoryReq params) {
		Optional<Category> row = crepo.findById(params.getCategoryId());
		Category item1 = null;
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
		Category ret = crepo.save(item1);
		
		List<Category> list = crepo.findAll();
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
	 * 카테고리 삭제
	 * @param id
	 */
	@Transactional
    public void deleteCategory(String id) {
		crepo.deleteById(id);
		
		List<Category> list = crepo.findAll();
		fdao.makeAllContentGroupHTML(list);
		
		// 변경이력 저장
		ChangeHistory item3 = new ChangeHistory();
		item3.setType("GROUP");
		item3.setMethod("DEL");
		item3.setFilePath(id + ".html");
		hrepo.save(item3);
    }
	
}
