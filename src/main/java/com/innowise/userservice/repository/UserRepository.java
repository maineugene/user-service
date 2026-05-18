package com.innowise.userservice.repository;

import com.innowise.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository
        extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findById(Long id);

    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);
}
