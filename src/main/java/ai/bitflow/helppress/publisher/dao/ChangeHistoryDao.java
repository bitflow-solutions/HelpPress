package ai.bitflow.helppress.publisher.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.bitflow.helppress.publisher.domain.ChangeHistory;
import ai.bitflow.helppress.publisher.repository.ChangeHistoryRepository;

@Component
public class ChangeHistoryDao {

	private final Logger logger = LoggerFactory.getLogger(ChangeHistoryDao.class);

	@Autowired
	private ChangeHistoryRepository chrepo;

	
	/**
	 * 변경이력 저장
	 * @param userid
	 * @param type
	 * @param method
	 * @param title
	 * @param filePath
	 */
    public void addHistory(String userid, String type, String method, String title, String filePath) {
		ChangeHistory item = new ChangeHistory();
		item.setUserid(userid);
		item.setType(type);
		item.setTitle(title);
		item.setMethod(method);
		item.setFilePath(filePath);
		item.setReleased(null);
		chrepo.save(item);
	}

	/**
	 * 변경이력 가져오기
	 * @return
	 */
	public List<ChangeHistory> getHistories() {
		return chrepo.findTop300ByOrderByUpdDtDesc();
	}
	
	public List<ChangeHistory> findAllChanged() {
		List<Integer> list = chrepo.findAllChangedFileIds();
		return chrepo.findAllByIdInOrderByUpdDtDesc(list);
	}
	
	public List<ChangeHistory> findAllChangedByMe(String userid) {
		List<Integer> list = chrepo.findAllChangedFileIdsByMe(userid);
		return chrepo.findAllByIdInOrderByUpdDtDesc(list);
	}
	
	public void releaseAll() {
		List<ChangeHistory> list = chrepo.findAll();
		for (ChangeHistory item : list) {
			item.setReleased('Y');
		}
		chrepo.saveAll(list);
	}
	
	public void releasePart(List<ChangeHistory> list) {
		for (ChangeHistory item : list) {
			item.setReleased('Y');
		}
		chrepo.saveAll(list);
	}

}
