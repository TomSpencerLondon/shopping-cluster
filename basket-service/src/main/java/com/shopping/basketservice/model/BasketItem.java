package com.shopping.basketservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BasketItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
    
    @ManyToOne
    private Basket basket;
}
