package com.Company.Bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.Company.Bank.entity.Account;

@Repository
public interface accountRepo extends JpaRepository<Account, Long> {
	@Query("SELECT acc FROM Account acc WHERE acc.accountNo = ?1")
	Account findByAccountNo(String AccountNo);
	
	@Query("SELECT acc FROM Account acc WHERE acc.first_name=?1 and acc.last_name=?2 and acc.phoneNo=?3")
	Account findByNameandPhone(String first,String last, String phoneNo);

	@Query("SELECT acc FROM Account acc WHERE acc.id = ?1")
	Optional<Account> findById(Long id);

	
}
