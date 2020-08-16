package ai.bitflow.helppress.publisher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.bitflow.helppress.publisher.domain.Category;


@Repository
public interface CategoryRepository extends JpaRepository <Category, String> {

	List<Category> findAllByOrderByOrderNo();
	
}
