package ai.bitflow.helppress.publisher.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ai.bitflow.helppress.publisher.domain.ChangeHistory;


@Repository
public interface ChangeHistoryRepository extends JpaRepository <ChangeHistory, Integer> {

	public List<ChangeHistory> findTop300ByOrderByUpdDtDesc();
	public Optional<ChangeHistory> findTopByTypeAndMethodAndFilePathAndUpdDtGreaterThanEqual
			(String type, String method, String filePath, LocalDateTime time);
	
	// Todo: MAX(id)를 그룹바이 한 다음 해당 ROW들을 IN으로 가져옴
	@Query(value =
	        "SELECT "
	        + " MAX(id) as id"
	        + " FROM ChangeHistory WHERE released IS NULL" 
	        + " GROUP BY filePath"
	        + " ORDER BY MAX(updDt) DESC")
	public List<Integer> findAllChangedFileIds();
	
	public List<ChangeHistory> findAllByIdInOrderByUpdDtDesc(List<Integer> ids);
	
}
