package com.shopping.frontendservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketDto {
    private Long id;
    private String userId;
    private List<BasketItemDto> items;
}
