
package com.stockguard.service;

import com.stockguard.entity.Order;
import com.stockguard.entity.OrderStatus;
import com.stockguard.entity.Product;
import com.stockguard.repository.OrderRepository;
import com.stockguard.repository.ProductRepository;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private OrderService orderService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(
                "Face Serum",
                new BigDecimal("499.00"),
                20
        );
    }

    @Test
    void placeOrderSuccessfully() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.deductStock(1L, 2))
                .thenReturn(1);

        doNothing().when(entityManager).refresh(product);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.placeOrder(1L, 2);

        assertEquals(2, result.getQuantity());
        assertEquals(new BigDecimal("998.00"), result.getTotalPrice());
        assertEquals(OrderStatus.PLACED, result.getStatus());

        verify(orderRepository).deductStock(1L, 2);
        verify(orderRepository).save(any(Order.class));
        verify(entityManager).refresh(product);
    }

    @Test
    void rejectOrderWhenStockIsInsufficient() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.deductStock(1L, 25))
                .thenReturn(0);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(1L, 25)
        );

        verify(orderRepository, never()).save(any(Order.class));
        verify(entityManager, never()).refresh(any());
    }

    @Test
    void rejectOrderWhenQuantityIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(1L, 0)
        );

        verify(productRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrderRestoresStock() {
        Order order = new Order(
                product,
                2,
                new BigDecimal("998.00")
        );

        when(orderRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertEquals(22, product.getStock());

        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }

    @Test
    void rejectCancellationWhenAlreadyCancelled() {
        Order order = new Order(
                product,
                2,
                new BigDecimal("998.00")
        );
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.cancelOrder(1L)
        );

        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
}


