
package com.stockguard.controller;

import com.stockguard.entity.Order;
import com.stockguard.service.OrderService;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order placeOrder(
            @RequestParam Long productId,
            @RequestParam @Min(value = 1, message = "Quantity must be at least 1")
            int quantity) {
        return orderService.placeOrder(productId, quantity);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }
}

