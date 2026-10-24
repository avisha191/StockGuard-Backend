
package com.stockguard.service;

import com.stockguard.entity.Order;
import com.stockguard.entity.OrderStatus;
import com.stockguard.entity.Product;
import com.stockguard.repository.OrderRepository;
import com.stockguard.repository.ProductRepository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            EntityManager entityManager) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public Order placeOrder(Long productId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + productId));

        int updatedRows = orderRepository.deductStock(productId, quantity);

        if (updatedRows == 0) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product: " + productId);
        }

        // Refresh the product with the latest database stock.
        entityManager.refresh(product);

        BigDecimal totalPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));

        Order order = new Order(product, quantity, totalPrice);

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id));
    }

    @Transactional
    public Order cancelOrder(Long id) {

        Order order = orderRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Order is already cancelled");
        }

        Product product = order.getProduct();

        product.setStock(product.getStock() + order.getQuantity());
        productRepository.save(product);

        order.setStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);
    }
}


