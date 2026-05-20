package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BusinessLogicException;
import com.innowise.userservice.model.PaymentCard;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createCard_WhenLimitExceeded_ShouldThrowBusinessException() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countCardsByUserId(1L)).thenReturn(5);

        assertThrows(BusinessLogicException.class, () -> paymentCardService.createCard(1L, new PaymentCard()));
        verify(paymentCardRepository, never()).save(any(PaymentCard.class));
    }
}