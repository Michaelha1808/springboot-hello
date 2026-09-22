package com.michaeljava.springboot_hello.entity.user;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "java_cccd_001")
public class CCCDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numberCCCD;

//    @OneToOne(cascade =  CascadeType.ALL)
//    @JoinColumn(name = "user_id")
//    private UserEntity user;
}
