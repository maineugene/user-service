package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BusinessLogicException;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.repository.specification.UserSpecification;
import com.innowise.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private static final int MAX_CARDS_PER_USER = 5;

    @Transactional
    public User createUser(User user) {
        if (user.getPaymentCards() != null) {
            if (user.getPaymentCards().size() > MAX_CARDS_PER_USER) {
                throw new BusinessLogicException("User can not have more than"
                        + MAX_CARDS_PER_USER + "cards");
            }
            user.getPaymentCards().forEach(card -> card.setUser(user));
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));

        if (user.getPaymentCards() != null) {
            user.getPaymentCards().size();
        }

        return user;
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(String name, String surname, Pageable pageable) {
        var spec = UserSpecification.filterByFirstNameAndSurname(name, surname);
        return userRepository.findAll(spec, pageable);
    }

    @CachePut(value = "users", key = "#id")
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id:" + id + " not found"));

        existingUser.setName(userDetails.getName());
        existingUser.setSurname(userDetails.getSurname());
        existingUser.setBirthDate(userDetails.getBirthDate());
        existingUser.setEmail(userDetails.getEmail());

        User updated = userRepository.save(existingUser);
        if (updated.getPaymentCards() != null) {
            updated.getPaymentCards().size();
        }
        return updated;
    }

    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public void changeActiveStatus(Long id, boolean active) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id:" + id + "not found");
        }
        userRepository.updateActiveStatus(id, active);
    }
}
