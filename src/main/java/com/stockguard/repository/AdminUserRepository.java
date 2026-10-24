package com.stockguard.repository;

import com.stockguard.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepository
        extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByEmail(String email);

    boolean existsByEmail(String email);
}

