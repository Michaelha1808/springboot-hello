package com.michaeljava.springboot_hello.repository;

import com.michaeljava.springboot_hello.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
