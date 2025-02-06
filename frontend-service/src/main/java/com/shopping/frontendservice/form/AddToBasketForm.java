package com.shopping.frontendservice.form;

import lombok.Data;

@Data
public class AddToBasketForm {
    private Long productId;
    private Integer quantity;
}
