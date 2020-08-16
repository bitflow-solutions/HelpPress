package ai.bitflow.helppress.publisher.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.bitflow.helppress.publisher.service.ReleaseService;
import ai.bitflow.helppress.publisher.vo.req.UserReq;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author method76
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ecm/release") 
public class ApiReleaseController {
	
	private final Logger logger = LoggerFactory.getLogger(ApiReleaseController.class);
	
	@Autowired
	private ReleaseService rservice;
	
	/**
	 * 관리자 등록 처리
	 * @param params
	 * @return
	 */
	@GetMapping("/all") 
	public void downloadAll(UserReq params, HttpServletResponse res) {
		log.debug("downloadAll");
		rservice.downloadAll(res);
	}
	
}
