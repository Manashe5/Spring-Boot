package com.Company.Bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Company.Bank.entity.Transaction;
import com.Company.Bank.repository.TransactionRepo;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo repository;

    public void saveTransaction(Transaction transaction) {
        repository.save(transaction);
    }

    public List<Transaction> getTransactionsByAccountNo(String accountNo) {
        return repository.findByAccountNo(accountNo);
    }
}
