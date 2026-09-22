package com.michaeljava.springboot_hello;

import com.michaeljava.springboot_hello.entity.feed.FeedEntity;
import com.michaeljava.springboot_hello.entity.user.UserEntity;
import com.michaeljava.springboot_hello.repository.FeedRepository;
import com.michaeljava.springboot_hello.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@SpringBootTest
public class UserFeedTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Test
    @Transactional
    @Rollback(value = false)
    void onToManyTest(){
        // 1. New User
        UserEntity user = new UserEntity();
        FeedEntity feed = new FeedEntity();

        user.setUserName("Michael ha");
        user.setUserEmail("haminhchi1808@gmail.com");

        feed.setTitle("feed 01");
        feed.setDescription("feed 01 description");
        user.setFeedList(List.of(feed));
        feed.setUser(user);

        userRepository.save(user);
        feedRepository.save(feed);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    void onToManyTestTwo(){
        // 1. New User
        UserEntity user = new UserEntity();
        FeedEntity feed = new FeedEntity();

        user.setUserName("Minh chi");
        user.setUserEmail("itpro@gmail.com");

        feed.setTitle("feed 02");
        feed.setDescription("feed 02 description");
        user.setFeedList(List.of(feed));
        feed.setUser(user);

        userRepository.save(user);
//        feedRepository.save(feed);
    }

    @Test
    @Transactional
    void  selectOntToManyTest(){
        UserEntity user = userRepository.findById(10L).orElseThrow();
        System.out.println(user);
        System.out.println(user.getFeedList());

    }
}
