package ai.bitflow.helppress.publisher.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.bitflow.helppress.publisher.service.ReleaseService;
import ai.bitflow.helppress.publisher.vo.req.UserReq;
import lombok.extern.slf4j.Slf4j;

/**
 * 배포를 위한 다운로드
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
	 * 전체 파일 ZIP 다운로드
	 * @param params
	 * @return
	 */
	@GetMapping("/all") 
	public void downloadAll(@RequestParam Boolean release, HttpServletResponse res) {
		log.debug("downloadAll " + release);
		rservice.downloadAll(release, res);
	}
	
	/**
	 * 1개 도움말 ZIP 파일 다운로드
	 * @param key
	 * @param res
	 */
	@GetMapping("/{key}") 
	public void downloadOne(@PathVariable String key, HttpServletResponse res) {
		log.debug("downloadOne");
		rservice.downloadOne(key, res);
	}
	
}
