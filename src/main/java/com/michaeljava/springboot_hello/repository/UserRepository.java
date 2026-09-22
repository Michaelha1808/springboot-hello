package com.michaeljava.springboot_hello.repository;

import com.michaeljava.springboot_hello.entity.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

import java.util.List;

//@RepositoryDefinition(domainClass = UserEntity.class, idClass = Long.class)
public interface UserRepository extends JpaRepository<UserEntity, Long>,JpaSpecificationExecutor<UserEntity> {

    // user pageable
    Page<UserEntity> findByUserName (String name, Pageable pageable);
    Page<UserEntity> findByUserNameContaining (String name, Pageable pageable);

    // find userName vs userEmail
    // findByUserNameAndUserEmail
    // UserNameAndUserEmail
    // userNameAnduserEmail
    // where userName = ?1 and userEmail = ?1
    UserEntity findByUserNameAndUserEmail(String userName, String userEmail);

    // userName
//    UserEntity findByUserName(String userName);

    /**
     * Where userName like %?
     */
    List<UserEntity> findByUserNameStartingWith(String userName);


    /**
     * Where userName like ?%
     */
    List<UserEntity> findByUserNameEndingWith(String userName);
    /**
     * Where id < 1
     */
    List<UserEntity> findByIdLessThan(Long id);

    // RAW JPQL
    @Query("SELECT u FROM UserEntity u WHERE u.id = (SELECT MAX(p.id) from UserEntity p)")
    UserEntity findMaxIdUser();

    @Query("SELECT u FROM UserEntity u WHERE u.userName = ?1 and u.userEmail = ?2")
    List<UserEntity> getUserEntityBy( String userName, String userEmail);

    @Query("SELECT u FROM UserEntity u WHERE u.userName = :userName and u.userEmail = :userEmail")
    List<UserEntity> getUserEntityByTwo(@Param("userName") String userName,@Param("userEmail") String userEmail);

    /**
     * UPDATE DELETE
     * */
    @Modifying
    @Query("UPDATE UserEntity u SET u.userName = :userName")
    @Transactional
    int updateUserName(@Param("userName") String userName);

    // native query
    /**
     * get count user use native query
     * */
    @Query(value = "SELECT COUNT(id) from java_user_001", nativeQuery = true)
    long getTotalUser();
}
