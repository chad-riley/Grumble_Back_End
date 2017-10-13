package com.libertymutual.goforcode.grumble.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.libertymutual.goforcode.grumble.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByEmail(String email);

}
