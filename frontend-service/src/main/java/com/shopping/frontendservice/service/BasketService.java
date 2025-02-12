package com.shopping.frontendservice.service;

import com.shopping.frontendservice.dto.BasketDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BasketService {
    
    private static final Logger logger = LoggerFactory.getLogger(BasketService.class);
    
    private final RestTemplate restTemplate;
    private final String basketServiceUrl;

    public BasketService(RestTemplate restTemplate, 
                        @Value("${basket-service.url:http://basket-service:8081}") String basketServiceUrl) {
        this.restTemplate = restTemplate;
        this.basketServiceUrl = basketServiceUrl;
    }

    public BasketDto getBasket(String userId) {
        try {
            logger.info("Fetching basket for userId: " + userId);
            BasketDto basket = restTemplate.getForObject(
                basketServiceUrl + "/api/basket/{userId}", 
                BasketDto.class,
                userId
            );

            if (basket != null && basket.getItems() != null) {
                logger.info("Calculating total for basket with " + basket.getItems().size() + " items.");
                BigDecimal total = basket.getItems().stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                basket.setTotal(total);
                logger.info("Total calculated: " + total);
            } else {
                logger.warn("Basket or items are null for userId: " + userId);
            }

            return basket;
        } catch (Exception e) {
            logger.error("Failed to retrieve basket for userId: " + userId, e);
            return null;
        }
    }

    public BasketDto addToBasket(String userId, Long productId, Integer quantity, BigDecimal price, String productName) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var requestBody = new HashMap<String, Object>();
        requestBody.put("productId", productId);
        requestBody.put("quantity", quantity);
        requestBody.put("price", price.doubleValue());
        requestBody.put("productName", productName);

        var request = new HttpEntity<>(requestBody, headers);
        
        return restTemplate.postForObject(
            basketServiceUrl + "/api/basket/{userId}/items",
            request,
            BasketDto.class,
            userId
        );
    }

    public void removeFromBasket(String userId, Long productId) {
        restTemplate.delete(
            basketServiceUrl + "/api/basket/{userId}/items/{productId}",
            userId,
            productId
        );
    }
}
