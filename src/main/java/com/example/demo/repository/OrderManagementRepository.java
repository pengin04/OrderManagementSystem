package com.example.demo.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.OrderManagement;

public interface OrderManagementRepository extends JpaRepository<OrderManagement, Integer> {
	 List<OrderManagement> findByPickedUpTrue(); // pickedUp=true の注文を取得
	 List<OrderManagement> findByDeliveryType(String deliveryType); // 追加
	// 店舗名で検索
	 List<OrderManagement> findByStoreName(String storeName);
	    List<OrderManagement> findByStoreNameAndPickedUpTrue(String storeName);
	    List<OrderManagement> findByStoreNameAndDeliveryType(String storeName, String deliveryType);
}
