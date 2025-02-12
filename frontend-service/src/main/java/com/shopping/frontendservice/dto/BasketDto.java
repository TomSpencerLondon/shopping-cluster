package com.shopping.frontendservice.dto;

import java.math.BigDecimal;
import java.util.List;

public class BasketDto {
    private Long id;
    private String userId;
    private List<BasketItemDto> items;
    private BigDecimal total = BigDecimal.ZERO;

    public BasketDto() {
    }

    public BasketDto(Long id, String userId, List<BasketItemDto> items, BigDecimal total) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.total = total != null ? total : BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<BasketItemDto> getItems() {
        return items;
    }

    public void setItems(List<BasketItemDto> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total != null ? total : BigDecimal.ZERO;
    }
}
