package com.shopping.orderservice.repository;

import com.shopping.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(String userId);
    List<Order> findByStatus(String status);
}
