package com.shopping.frontendservice.service;

import com.shopping.frontendservice.dto.BasketDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

@Service
public class BasketService {
    
    private final RestTemplate restTemplate;
    private final String basketServiceUrl;

    public BasketService(RestTemplate restTemplate, 
                        @Value("${basket-service.url:http://basket-service:8081}") String basketServiceUrl) {
        this.restTemplate = restTemplate;
        this.basketServiceUrl = basketServiceUrl;
    }

    public BasketDto getBasket(String userId) {
        return restTemplate.getForObject(
            basketServiceUrl + "/api/basket/{userId}", 
            BasketDto.class,
            userId
        );
    }

    public BasketDto addToBasket(String userId, Long productId, Integer quantity, BigDecimal price, String productName) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("productId", productId);
        requestBody.put("quantity", quantity);
        requestBody.put("price", price);
        requestBody.put("productName", productName);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
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
