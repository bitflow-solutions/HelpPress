package ai.bitflow.helppress.publisher.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.bitflow.helppress.publisher.domain.ChangeHistory;


@Repository
public interface ChangeHistoryRepository extends JpaRepository <ChangeHistory, Integer> {

	public List<ChangeHistory> findTop300ByOrderByUpdDtDesc();
	public Optional<ChangeHistory> findTopByTypeAndMethodAndFilePathAndUpdDtGreaterThanEqual
			(String type, String method, String filePath, LocalDateTime time);
	
}
