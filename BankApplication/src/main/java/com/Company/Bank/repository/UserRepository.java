package com.Company.Bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Company.Bank.entity.userLogin;

@Repository
public interface UserRepository extends JpaRepository<userLogin, Long>{	
	userLogin findByEmailAndPassword(String email, String password);

	userLogin findByEmail(String email);
}
