package com.michaeljava.springboot_hello.repository;

import com.michaeljava.springboot_hello.entity.user.CCCDEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CCCDRepository extends JpaRepository<CCCDEntity, Long> {

}
