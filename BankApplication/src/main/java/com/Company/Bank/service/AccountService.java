package com.Company.Bank.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Company.Bank.entity.Account;
import com.Company.Bank.repository.accountRepo;

@Service
public class AccountService {
    
    @Autowired
    private accountRepo repo;
    
	public boolean accountExists(String acc) {
	    return repo.findByAccountNo(acc) != null;
	}
	
    public void createAccount(Account acc) {
        repo.save(acc);
    }

    public List<Account> listAll() {
        return repo.findAll();
    }
    
    public Account getAccount(String accountNo) {
    	return repo.findByAccountNo(accountNo);
    }
    
	public void updateAccount(Account account) 
	{
		repo.save(account);
	}

	public Account getAccountNumber(String first,String last, String phoneNo) {
		return repo.findByNameandPhone(first,last,phoneNo);
	}
	
	public void delete(long id) {
		repo.deleteById(id);
	}
	public Account get(long id) {
		return repo.findById(id).get();
	}
}
