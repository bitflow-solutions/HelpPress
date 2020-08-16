package ai.bitflow.helppress.publisher.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ai.bitflow.helppress.publisher.constant.CmsConstant;
import ai.bitflow.helppress.publisher.domain.Category;
import ai.bitflow.helppress.publisher.domain.ChangeHistory;
import ai.bitflow.helppress.publisher.domain.User;
import ai.bitflow.helppress.publisher.service.CategoryService;
import ai.bitflow.helppress.publisher.service.ReleaseService;
import ai.bitflow.helppress.publisher.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author method76
 */
@Slf4j
@Controller
@RequestMapping("") 
public class AdminController {
	
	private final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Value("${app.upload.root.path}")
	private String SRC_FOLDER;
	
	@Autowired
	private CategoryService cservice;
	
	@Autowired
	private ReleaseService rservice;

	@Autowired
	private UserService uservice;
	
	@GetMapping("") 
	public String main() {
		List<User> list = uservice.getUsers();
		if (list==null || list.size()<1) {
			return "redirect:/join";
		} else {
			return "redirect:/content";
		}
	}

	/**
	 * 회원 가입
	 * @param mo
	 * @param req
	 * @return
	 */
	@GetMapping("/join") 
	public String join(Model mo, HttpServletRequest req) {
		List<User> list = uservice.getUsers();
		if (list!=null && list.size()>0) {
			return "redirect:/login";
		} else {
			return "admin/join";			
		}
	}
	
	/**
	 * 로그인
	 * @param mo
	 * @param req
	 * @return
	 */
	@GetMapping("/login") 
	public String login(Model mo, HttpServletRequest req) {
		List<User> list = uservice.getUsers();
		if (list==null || list.size()<1) {
			return "redirect:/join";
		} else {
			return "admin/login";
		}
	}
	
	/**
	 * 카테고리 관리
	 * @param mo
	 * @return
	 */
	@GetMapping("/category") 
	public String category(Model mo, HttpServletRequest req) {
		List<Category> list = cservice.getCategories();
		String add = req.getParameter("add");
		logger.debug("add " + add);
		mo.addAttribute("tab1", " active");
		mo.addAttribute("list", list);
		return "admin/category";
	}
	
	/**
	 * 도움말 관리
	 * @param mo
	 * @param sess
	 * @return
	 */
	@GetMapping("/content") 
	public String content(Model mo, HttpSession sess) {
		mo.addAttribute("tab2", " active");
		List<Category> list = cservice.getCategories();
		mo.addAttribute("list", list);
		return "admin/content";
	}

	/**
	 * 배포 관리
	 * @param mo
	 * @return
	 */
	@GetMapping("/release") 
	public String release(Model mo) {
		mo.addAttribute("tab3", " active");
		List<ChangeHistory> list = rservice.getHistories();
		for (ChangeHistory item : list) {
			String statusKr = "";
			if (CmsConstant.ADD.equals(item.getMethod())) {
				statusKr += "(추가)";
			} else if (CmsConstant.MODIFY.equals(item.getMethod())) {
				statusKr += "(변경)";
			} else if (CmsConstant.DELETE.equals(item.getMethod())) {
				statusKr += "(삭제)";
			}
			item.setStatusKr(statusKr);
		}
		mo.addAttribute("list", list);
		File previewPath = new File(SRC_FOLDER);
		mo.addAttribute("previewPath", previewPath.getAbsolutePath());
		return "admin/release";
	}

	/**
	 * 관리자 관리
	 * @param mo
	 * @return
	 */
	@GetMapping("/user") 
	public String user(Model mo) {
		mo.addAttribute("tab4", " active");
		List<User> list = uservice.getUsers();
		for (User item : list) {
			item.setUserid(item.getUsername());
		}
		mo.addAttribute("list", list);
		if (list!=null && list.size()>1) {
			mo.addAttribute("notOnlyOne", true);
		}
		return "admin/user";
	}

}
