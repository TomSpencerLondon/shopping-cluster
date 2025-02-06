package com.shopping.orderservice.service;

import com.shopping.orderservice.model.Order;
import com.shopping.orderservice.model.OrderItem;
import com.shopping.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        
        // Calculate subtotals for each item
        order.getItems().forEach(item -> {
            item.setSubtotal(item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())));
        });
        
        return orderRepository.save(order);
    }

    public Optional<Order> getOrder(String id) {
        return orderRepository.findById(id);
    }

    public List<Order> getUserOrders(String userId) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getUserId().equals(userId))
                .toList();
    }

    public Order updateOrder(String id, Order orderDetails) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            Order existingOrder = order.get();
            existingOrder.setStatus(orderDetails.getStatus());
            existingOrder.setShippingAddress(orderDetails.getShippingAddress());
            existingOrder.setItems(orderDetails.getItems());
            return orderRepository.save(existingOrder);
        }
        return null;
    }

    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getStatus().equals(status))
                .toList();
    }
}
