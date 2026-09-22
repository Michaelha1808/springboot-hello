package com.michaeljava.springboot_hello;

import com.michaeljava.springboot_hello.entity.user.CCCDEntity;
import com.michaeljava.springboot_hello.entity.user.UserEntity;
import com.michaeljava.springboot_hello.repository.CCCDRepository;
import com.michaeljava.springboot_hello.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
public class UserCCCDTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CCCDRepository cccdRepository;

    @Test
    @Transactional
    @Rollback(value = false)
    void oneToOneTest(){
        UserEntity user =  new UserEntity();
        CCCDEntity cccd = new CCCDEntity();

        user.setUserName("Michael id card");
        user.setUserEmail("cccd@gmail.com");

        cccd.setNumberCCCD("123456789");
        user.setCccd(cccd);

        userRepository.save(user);


    }
}
