package ai.bitflow.helppress.publisher.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import ai.bitflow.helppress.publisher.constant.ApplicationConstant;
import ai.bitflow.helppress.publisher.domain.ChangeHistory;
import ai.bitflow.helppress.publisher.repository.ChangeHistoryRepository;

@Component
public class ChangeHistoryDao {

	private final Logger logger = LoggerFactory.getLogger(ChangeHistoryDao.class);

	@Autowired
	private ChangeHistoryRepository chrepo;

	
//	Character released;
	
	/**
	 * 변경이력 저장
	 * @param userid
	 * @param type
	 * @param method
	 * @param title
	 * @param filePath
	 */
	// @CacheEvict(value="history", allEntries=true)
    public void addHistory(String userid, String type, String method, String title, String filePath) {
		
		ChangeHistory item = new ChangeHistory();
		item.setUserid(userid);
		item.setType(type);
		item.setTitle(title);
		item.setMethod(method);
		item.setFilePath(filePath);
		chrepo.save(item);
	}

	/**
	 * 변경이력 가져오기
	 * @return
	 */
    // @Cacheable(value="history")
	public List<ChangeHistory> getHistories() {
		return chrepo.findTop300ByOrderByUpdDtDesc();
	}
	
	public List<ChangeHistory> findAllChanged() {
		List<Integer> list = chrepo.findAllChangedFileIds();
		return chrepo.findAllByIdInOrderByUpdDtDesc(list);
	}

}
