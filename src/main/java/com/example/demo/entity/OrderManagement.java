package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_management")
public class OrderManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer order_id;

    private String customer_name;
    private String email;
    private String phone_number;
    private String ordered_item;
    private Integer store_id;
    @Column(name = "store_name")  // ★追加
    private String storeName;
    private LocalDateTime pickup_time;
    private String item_details; // 新しいカラム（商品名＋数量）
    private Integer total_price;
    // 新しいカラム（合計金額）
    
    private Boolean mailSent = false;   // ← 追加
    private Boolean pickedUp = false;   // ← 追加
    
    @Column(name = "delivery_type")
    private String deliveryType;    // "pickup" or "delivery"
    private String delivery_address; // 配達先住所（現在地など）
    
    private LocalDateTime order_time;       // 注文日時
    private LocalDateTime completed_time;   // 受け渡し完了日時
    
    @Column(name = "reservation_number")
    private Integer reservationNumber;

    public Integer getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(Integer reservationNumber) {
        this.reservationNumber = reservationNumber;
    }
    
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    
 // getter/setter
    public LocalDateTime getOrder_time() { return order_time; }
    public void setOrder_time(LocalDateTime order_time) { this.order_time = order_time; }

    public LocalDateTime getCompleted_time() { return completed_time; }
    public void setCompleted_time(LocalDateTime completed_time) { this.completed_time = completed_time; }
    
    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public String getDelivery_address() { return delivery_address; }
    public void setDelivery_address(String delivery_address) { this.delivery_address = delivery_address; }
    
    // getter/setter
    public Boolean getMailSent() { return mailSent; }
    public void setMailSent(Boolean mailSent) { this.mailSent = mailSent; }

    public Boolean getPickedUp() { return pickedUp; }
    public void setPickedUp(Boolean pickedUp) { this.pickedUp = pickedUp; }

	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public String getOrdered_item() {
		return ordered_item;
	}
	public void setOrdered_item(String ordered_item) {
		this.ordered_item = ordered_item;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public LocalDateTime getPickup_time() {
		return pickup_time;
	}
	public void setPickup_time(LocalDateTime pickup_time) {
		this.pickup_time = pickup_time;
	}
	public String getItem_details() {
		return item_details;
	}
	public void setItem_details(String item_details) {
		this.item_details = item_details;
	}
	public Integer getTotal_price() {
		return total_price;
	}
	public void setTotal_price(Integer total_price) {
		this.total_price = total_price;
	}
	
	

    // --- Getter/Setter省略 ---
    
}
