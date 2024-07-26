package com.Company.Bank.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Company.Bank.entity.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountNo(String accountNo);
}
