package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.StoreRepository;

@Service
public class LoginService {

    @Autowired
    private StoreRepository storeRepository;

    public boolean login(String storeName, String password) {
        return storeRepository.findByStoreNameAndPassword(storeName, password).isPresent();
    }
}