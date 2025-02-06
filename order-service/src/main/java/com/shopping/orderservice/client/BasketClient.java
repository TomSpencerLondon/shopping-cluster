package com.shopping.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "basket-service")
public interface BasketClient {
    @DeleteMapping("/api/basket/{userId}")
    ResponseEntity<Void> clearBasket(@PathVariable("userId") String userId);
}
