package com.libertymutual.goforcode.grumble.services;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.libertymutual.goforcode.grumble.models.MenuItem;
import com.libertymutual.goforcode.grumble.models.Restaurant;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long>{
	
	List<MenuItem> findByNameContaining (String name);


}
