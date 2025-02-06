package com.shopping.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductClient {
    @PutMapping("/api/products/{id}/stock")
    ResponseEntity<Void> updateStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);
}
