package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByStoreNameAndPassword(String storeName, String password);
}