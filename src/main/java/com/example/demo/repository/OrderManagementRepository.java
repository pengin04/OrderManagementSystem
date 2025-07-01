package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.OrderManagement;

public interface OrderManagementRepository extends JpaRepository<OrderManagement, Integer> {
}
