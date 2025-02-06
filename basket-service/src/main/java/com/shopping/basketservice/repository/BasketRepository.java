package com.shopping.basketservice.repository;

import com.shopping.basketservice.model.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    Optional<Basket> findByUserId(String userId);
}
