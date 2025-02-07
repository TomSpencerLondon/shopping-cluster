package com.shopping.basketservice.controller;

import com.shopping.basketservice.model.Basket;
import com.shopping.basketservice.model.BasketItem;
import com.shopping.basketservice.service.BasketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/basket")
public class BasketController {
    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Basket> getBasket(@PathVariable String userId) {
        return ResponseEntity.ok(basketService.getBasket(userId));
    }

    @PostMapping(value = "/{userId}/items", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Basket> addItem(@PathVariable String userId, @RequestBody BasketItem item) {
        return ResponseEntity.ok(basketService.addItem(userId, item));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Basket> removeItem(@PathVariable String userId, @PathVariable Long itemId) {
        return ResponseEntity.ok(basketService.removeItem(userId, itemId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearBasket(@PathVariable String userId) {
        basketService.clearBasket(userId);
        return ResponseEntity.ok().build();
    }
}
