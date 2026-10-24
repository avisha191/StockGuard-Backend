
package com.stockguard.repository;

import com.stockguard.entity.Order;
import com.stockguard.entity.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.stock = p.stock - :quantity
        WHERE p.id = :productId
        AND p.stock >= :quantity
    """)
    int deductStock(
            @Param("productId") Long productId,
            @Param("quantity") int quantity
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);
}

