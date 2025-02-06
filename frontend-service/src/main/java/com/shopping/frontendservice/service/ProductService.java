package com.shopping.frontendservice.service;

import com.shopping.frontendservice.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class ProductService {
    
    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductService(RestTemplate restTemplate, 
                         @Value("${product-service.url:http://product-service:8082}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
        log.info("ProductService initialized with URL: {}", productServiceUrl);
    }

    public List<ProductDto> getAllProducts() {
        log.info("Fetching all products from {}", productServiceUrl);
        try {
            var products = restTemplate.exchange(
                productServiceUrl + "/api/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProductDto>>() {}
            ).getBody();
            log.info("Retrieved {} products", products != null ? products.size() : 0);
            return products;
        } catch (Exception e) {
            log.error("Error fetching products: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProductDto addProduct(ProductDto product) {
        log.info("Adding new product: {}", product);
        try {
            var result = restTemplate.postForObject(
                productServiceUrl + "/api/products",
                product,
                ProductDto.class
            );
            log.info("Successfully added product: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error adding product: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProductDto getProduct(Long productId) {
        log.info("Fetching product with id: {}", productId);
        try {
            var product = restTemplate.getForObject(
                productServiceUrl + "/api/products/{productId}",
                ProductDto.class,
                productId
            );
            log.info("Retrieved product: {}", product);
            return product;
        } catch (Exception e) {
            log.error("Error fetching product: {}", e.getMessage(), e);
            throw e;
        }
    }
}
