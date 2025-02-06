package com.shopping.orderservice.service;

import com.shopping.orderservice.client.BasketClient;
import com.shopping.orderservice.client.ProductClient;
import com.shopping.orderservice.model.Order;
import com.shopping.orderservice.model.OrderItem;
import com.shopping.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final BasketClient basketClient;

    public OrderService(OrderRepository orderRepository, 
                       ProductClient productClient,
                       BasketClient basketClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.basketClient = basketClient;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order createOrder(Order order) {
        // Calculate total amount
        BigDecimal total = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        // Update product stock
        for (OrderItem item : order.getItems()) {
            productClient.updateStock(item.getProductId(), item.getQuantity());
        }

        // Clear the user's basket
        basketClient.clearBasket(order.getUserId());

        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long id, String status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
}
