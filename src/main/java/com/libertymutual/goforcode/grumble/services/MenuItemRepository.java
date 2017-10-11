package com.libertymutual.goforcode.grumble.services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.libertymutual.goforcode.grumble.models.MenuItem;
import com.libertymutual.goforcode.grumble.models.Restaurant;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long>{


}
