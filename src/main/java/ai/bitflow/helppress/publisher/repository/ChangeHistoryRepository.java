package ai.bitflow.helppress.publisher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.bitflow.helppress.publisher.domain.ChangeHistory;


@Repository
public interface ChangeHistoryRepository extends JpaRepository <ChangeHistory, Integer> {

	public List<ChangeHistory> findAllByOrderByUpdDtDesc();
	
}
