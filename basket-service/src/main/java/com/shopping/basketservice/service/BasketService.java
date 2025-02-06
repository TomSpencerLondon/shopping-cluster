package com.shopping.basketservice.service;

import com.shopping.basketservice.model.Basket;
import com.shopping.basketservice.model.BasketItem;
import com.shopping.basketservice.repository.BasketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class BasketService {
    private final BasketRepository basketRepository;

    public BasketService(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    public Basket getBasket(String userId) {
        return basketRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Basket newBasket = new Basket();
                    newBasket.setUserId(userId);
                    return basketRepository.save(newBasket);
                });
    }

    public Basket addItem(String userId, BasketItem item) {
        Basket basket = getBasket(userId);
        basket.getItems().add(item);
        updateTotalPrice(basket);
        return basketRepository.save(basket);
    }

    public Basket removeItem(String userId, Long itemId) {
        Basket basket = getBasket(userId);
        basket.getItems().removeIf(item -> item.getId().equals(itemId));
        updateTotalPrice(basket);
        return basketRepository.save(basket);
    }

    public void clearBasket(String userId) {
        Optional<Basket> basket = basketRepository.findByUserId(userId);
        basket.ifPresent(b -> {
            b.getItems().clear();
            updateTotalPrice(b);
            basketRepository.save(b);
        });
    }

    private void updateTotalPrice(Basket basket) {
        double total = basket.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        basket.setTotalPrice(total);
    }
}
