package com.michaeljava.springboot_hello.repository;

import com.michaeljava.springboot_hello.entity.feed.FeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<FeedEntity, Long> {

}
