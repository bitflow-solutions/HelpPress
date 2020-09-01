package ai.bitflow.helppress.publisher.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ai.bitflow.helppress.publisher.constant.ApplicationConstant;
import ai.bitflow.helppress.publisher.domain.ChangeHistory;
import ai.bitflow.helppress.publisher.domain.ContentsGroup;
import ai.bitflow.helppress.publisher.domain.ReleaseHistory;
import ai.bitflow.helppress.publisher.domain.User;
import ai.bitflow.helppress.publisher.service.ContentsGroupService;
import ai.bitflow.helppress.publisher.service.ReleaseService;
import ai.bitflow.helppress.publisher.service.UserService;

/**
 * 관리페이지 화면 컨트롤러 
 * @author 김성준
 */
@Controller
@RequestMapping("") 
public class ViewController {
	
	private final Logger logger = LoggerFactory.getLogger(ViewController.class);
	
	@Value("${app.upload.root.path}")
	private String SRC_FOLDER;
	
	@Autowired
	private ContentsGroupService cservice;
	
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
		logger.debug("listj " + list.toString());
		if (list!=null && list.size()>0) {
			return "redirect:/login";
		} else {
			return "page/join";			
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
		logger.debug("listl " + list.toString());
		if (list==null || list.size()<1) {
			return "redirect:/join";
		} else {
			return "page/login";
		}
	}
	
	/**
	 * 도움말그룹 관리
	 * @param mo
	 * @return
	 */
	@GetMapping("/group") 
	public String group(Model mo, HttpServletRequest req) {
		List<ContentsGroup> list = cservice.getGroups();
		String add = req.getParameter("add");
		mo.addAttribute("tab1", " active");
		mo.addAttribute("list", list);
		return "page/group";
	}
	
	/**
	 * 도움말 관리
	 * @param mo
	 * @param sess
	 * @return
	 */
	@GetMapping("/content") 
	public String content(Model mo) {
		mo.addAttribute("tab2", " active");
		List<ContentsGroup> list = cservice.getGroups();
		mo.addAttribute("list", list);
		return "page/content";
	}

	/**
	 * 배포 관리
	 * @param mo
	 * @return
	 */
	@GetMapping("/release") 
	public String release(Model mo) {
		mo.addAttribute("tab3", " active");
		List<ChangeHistory> hlist = rservice.getHistories();
		List<ReleaseHistory> rlist = rservice.getReleases();
		
		mo.addAttribute("hlist", hlist);
		mo.addAttribute("rlist", rlist);
		
		File previewPath = new File(SRC_FOLDER);
		mo.addAttribute("previewPath", previewPath.getAbsolutePath());
		return "page/release";
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
		return "page/user";
	}

}
