package com.innowise.userservice.service;

import com.innowise.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User createUser(User user);

    User getUserById(Long id);

    Page<User> getAllUsers(String name, String surname, Pageable pageable);

    User updateUser(Long id, User userDetails);

    void changeActiveStatus(Long id, boolean active);
}
