package com.shopping.frontendservice.controller;

import com.shopping.frontendservice.dto.ProductDto;
import com.shopping.frontendservice.service.BasketService;
import com.shopping.frontendservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ShoppingController {

    private final ProductService productService;
    private final BasketService basketService;
    private static final String DEFAULT_USER_ID = "user123";

    @GetMapping("/")
    public String home(Model model) {
        log.info("Loading home page");
        var products = productService.getAllProducts();
        log.info("Found {} products", products.size());
        model.addAttribute("products", products);
        model.addAttribute("basket", basketService.getBasket(DEFAULT_USER_ID));
        return "index";
    }

    @PostMapping("/basket/add")
    public String addToBasket(@RequestParam Long productId, @RequestParam Integer quantity) {
        log.info("Adding product {} with quantity {} to basket", productId, quantity);
        // Get product details first
        var product = productService.getProduct(productId);
        if (product != null) {
            basketService.addToBasket(DEFAULT_USER_ID, productId, quantity, product.getPrice(), product.getName());
        }
        return "redirect:/";
    }

    @PostMapping("/basket/remove/{itemId}")
    public String removeFromBasket(@PathVariable Long itemId) {
        log.info("Removing basket item {} from basket", itemId);
        basketService.removeFromBasket(DEFAULT_USER_ID, itemId);
        return "redirect:/";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        log.info("Showing add product form");
        model.addAttribute("product", new ProductDto());
        return "add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute ProductDto product) {
        log.info("Adding new product: {}", product);
        productService.addProduct(product);
        return "redirect:/";
    }
}
