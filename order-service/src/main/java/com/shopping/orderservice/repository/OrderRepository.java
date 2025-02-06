package com.shopping.orderservice.repository;

import com.shopping.orderservice.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
    // Add custom queries if needed
}
