package ai.bitflow.helppress.publisher.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.bitflow.helppress.publisher.domain.Contents;
import ai.bitflow.helppress.publisher.service.ContentsService;
import ai.bitflow.helppress.publisher.vo.req.ContentsReq;
import ai.bitflow.helppress.publisher.vo.res.ContentsRes;
import ai.bitflow.helppress.publisher.vo.res.result.ContentResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author method76
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ecm/content") 
public class ApiContentController {
	
	private final Logger logger = LoggerFactory.getLogger(ApiContentController.class);
	
	@Autowired
	private ContentsService cservice;
	
	@Autowired 
	private SimpMessagingTemplate broker;
	
	/**
	 * 도움말 추가
	 * @param params
	 * @return
	 */
	@PostMapping("")
	public ContentsRes newContent(ContentsReq params, HttpSession sess) {
		ContentsRes ret = new ContentsRes();
		Contents res = cservice.newContent(params);
		if (res!=null) {
			ContentResult rst = new ContentResult();
			rst.setKey(String.format("%05d", res.getId()));
			rst.setTitle(res.getTitle());
			ret.setResult(rst);
 			// broker.convertAndSend("/group", rst);
		} else {
			ret.setFailResponse();
		}
		return ret;
	}
	
	/**
	 * 폴더 추가
	 * @param params
	 * @return
	 */
	@PostMapping("/folder")
	public ContentsRes newFolder(ContentsReq params) {
		ContentsRes ret = new ContentsRes();
		String key = cservice.newFolder(params);
		if (key!=null) {
			ContentResult rst = new ContentResult();
			rst.setKey(key);
			ret.setResult(rst);
		} else {
			ret.setFailResponse();
		}
		return ret;
	}
	
	/**
	 * 컨텐츠 조회
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public ContentsRes get(@PathVariable String id) {
		ContentsRes ret = new ContentsRes();
		Contents item = cservice.getContents(id);
		ContentResult result = new ContentResult();
		if (item!=null) {
			result.setTitle(item.getTitle());
			result.setContents(item.getContent());
		} else {
			ret.setFailResponse(404);
		}
		ret.setResult(result);
		return ret;
	}
	
	/**
	 * 컨텐츠 조회
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}/download")
	public ContentsRes download(@PathVariable String id) {
		ContentsRes ret = new ContentsRes();
		Contents item = cservice.getContents(id);
		ContentResult result = new ContentResult();
		if (item!=null) {
			result.setTitle(item.getTitle());
			result.setContents(item.getContent());
		} else {
			ret.setFailResponse(404);
		}
		ret.setResult(result);
		return ret;
	}
	
	/**
	 * 컨텐츠 삭제
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	public ContentsRes delete(@PathVariable String id) {
		ContentsRes ret = new ContentsRes();
		boolean success = cservice.deleteContent(id);
		return ret;
	}
	
	/**
	 * 컨텐츠 수정
	 * @param params
	 * @param id
	 * @return
	 */
	@PutMapping("/{id}")
	public ContentsRes updateDoc(ContentsReq params, @PathVariable String id) {
		ContentsRes ret = new ContentsRes();
		ContentResult result = new ContentResult();
		cservice.updateContent(params, id);
		result.setKey(id);
		ret.setResult(result);
		return ret;
	}
	
}
