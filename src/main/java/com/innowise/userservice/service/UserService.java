package com.innowise.userservice.service;

import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.repository.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private static final int MAX_CARDS_PER_USER = 5;

    @Transactional
    public User createUser(User user) {
        if (user.getPaymentCards() != null) {
            if (user.getPaymentCards().size() > MAX_CARDS_PER_USER) {
                throw new IllegalArgumentException("User can not have more than"
                        + MAX_CARDS_PER_USER + "cards");
            }
            user.getPaymentCards().forEach(card -> card.setUser(user));
        }

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id: " + id + " not found"));
    }

    public Page<User> getAllUsers(String name, String surname, Pageable pageable) {
        var spec = UserSpecification.filterByFirstNameAndSurname(name, surname);
        return userRepository.findAll(spec, pageable);
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);
        existingUser.setName(userDetails.getName());
        existingUser.setSurname(userDetails.getSurname());
        existingUser.setBirthDate(userDetails.getBirthDate());
        existingUser.setEmail(userDetails.getEmail());

        return userRepository.save(existingUser);
    }

    @Transactional
    public void changeActiveStatus(Long id, boolean active) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User with id:" + id + "not found");
        }
        userRepository.updateActiveStatus(id, active);
    }

}
