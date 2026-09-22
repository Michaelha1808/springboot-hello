package com.michaeljava.springboot_hello.service;

import com.michaeljava.springboot_hello.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserEntity createUser(UserEntity user);

    UserEntity findByUserNameAndUserEmail(String userName, String userEmail);

    // get all by limit offset
    Page<UserEntity> findAllUsers(Pageable pageable);

    // get search by limit offset
    Page<UserEntity> findByUserName(String userName, Pageable pageable);
}
