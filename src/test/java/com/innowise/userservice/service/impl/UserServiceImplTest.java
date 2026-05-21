package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BusinessLogicException;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.model.PaymentCard;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .name("Evgeniy")
                .surname("Test")
                .email("test@innowise.com")
                .birthDate(LocalDate.of(2007, 1, 4))
                .paymentCards(new ArrayList<>())
                .build();
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User created = userService.createUser(new User());

        assertNotNull(created);
        assertEquals(1L, created.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_WithValidCards_ShouldLinkCardsAndSave() {
        User userWithCards = new User();
        List<PaymentCard> cards = new ArrayList<>();
        cards.add(new PaymentCard());
        cards.add(new PaymentCard());
        userWithCards.setPaymentCards(cards);

        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User created = userService.createUser(userWithCards);

        assertNotNull(created);
        for (PaymentCard card : cards) {
            assertEquals(userWithCards, card.getUser());
        }
    }

    @Test
    void createUser_WithTooManyCards_ShouldThrowBusinessLogicException() {
        User userWithTooManyCards = new User();
        List<PaymentCard> cards = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            cards.add(new PaymentCard());
        }
        userWithTooManyCards.setPaymentCards(cards);

        assertThrows(BusinessLogicException.class, () -> userService.createUser(userWithTooManyCards));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        User found = userService.getUserById(1L);

        assertNotNull(found);
        assertEquals("Evgeniy", found.getName());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> list = List.of(sampleUser);
        Page<User> expectedPage = new PageImpl<>(list, pageable, list.size());

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(expectedPage);

        Page<User> actualPage = userService.getAllUsers("Evgeniy", "Test", pageable);

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        assertEquals("Evgeniy", actualPage.getContent().getFirst().getName());
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateFieldsAndReturnUser() {
        User userDetails = User.builder()
                .name("UpdatedName")
                .surname("UpdatedSurname")
                .email("updated@innowise.com")
                .birthDate(LocalDate.of(1995, 5, 5))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUser(1L, userDetails);

        assertNotNull(updatedUser);
        assertEquals("UpdatedName", updatedUser.getName());
        assertEquals("UpdatedSurname", updatedUser.getSurname());
        assertEquals("updated@innowise.com", updatedUser.getEmail());
        assertEquals(LocalDate.of(1995, 5, 5), updatedUser.getBirthDate());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        User userDetails = new User();

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(2L, userDetails));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changeActiveStatus_WhenUserExists_ShouldUpdateStatus() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.changeActiveStatus(1L, false);

        verify(userRepository, times(1)).updateActiveStatus(1L, false);
    }

    @Test
    void changeActiveStatus_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.changeActiveStatus(2L, false));
        verify(userRepository, never()).updateActiveStatus(anyLong(), anyBoolean());
    }
}