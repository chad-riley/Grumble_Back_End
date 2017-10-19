package com.libertymutual.goforcode.grumble.services;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.libertymutual.goforcode.grumble.models.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>{

	List<Restaurant> findAllBySessionKey(String key);
	
	@Transactional
	void deleteBySessionKey(String sessionKey);
	

}
