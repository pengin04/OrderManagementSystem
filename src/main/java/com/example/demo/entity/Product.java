package com.example.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productmanagement")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL でもIDENTITYでOK
    private Integer id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(precision = 10, scale = 2) // PostgreSQL用に精度指定
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    // --- Getter & Setter ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
