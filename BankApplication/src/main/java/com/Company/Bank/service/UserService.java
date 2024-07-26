package com.Company.Bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Company.Bank.repository.UserRepository;
import com.Company.Bank.entity.userLogin;

@Service
public class UserService {
	
	@Autowired
	private UserRepository users;
	
    public void registerUser(userLogin user){
        users.save(user);
    }

	public userLogin login(String email, String password) {
		userLogin user = users.findByEmailAndPassword(email, password);
		return user;
	}

	public boolean emailExists(String email) {
	    return users.findByEmail(email) != null;

	}
}
