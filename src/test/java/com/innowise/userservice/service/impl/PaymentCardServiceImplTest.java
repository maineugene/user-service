package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BusinessLogicException;
import com.innowise.userservice.exception.ResourceNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    private User sampleUser;
    private PaymentCard sampleCard;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);

        sampleCard = new PaymentCard();
        sampleCard.setId(10L);
        sampleCard.setNumber("1234567812345678");
        sampleCard.setHolder("EVGENIY TEST");
        sampleCard.setExpirationDate(LocalDate.of(2030, 1, 1));
        sampleCard.setUser(sampleUser);
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

    @Test
    void createCard_WithValidData_ShouldSaveAndReturnCard() {
        PaymentCard newCard = new PaymentCard();
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(paymentCardRepository.countCardsByUserId(1L)).thenReturn(3); // Меньше 5
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(sampleCard);

        PaymentCard created = paymentCardService.createCard(1L, newCard);

        assertNotNull(created);
        assertEquals(sampleUser, newCard.getUser());
        verify(paymentCardRepository, times(1)).save(newCard);
    }

    @Test
    void createCard_WhenUserDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        PaymentCard card = new PaymentCard();

        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.createCard(1L, card));

        verify(paymentCardRepository, never()).countCardsByUserId(anyLong());
        verify(paymentCardRepository, never()).save(any(PaymentCard.class));
    }

    @Test
    void getCardById_WhenCardExists_ShouldReturnCard() {
        when(paymentCardRepository.findById(10L)).thenReturn(Optional.of(sampleCard));

        PaymentCard found = paymentCardService.getCardById(10L);

        assertNotNull(found);
        assertEquals("1234567812345678", found.getNumber());
    }

    @Test
    void getCardById_WhenCardDoesNotExist_ShouldThrowResourceNotFoundException() {
        when(paymentCardRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.getCardById(20L));
    }

    @Test
    void getAllCards_ShouldReturnPageOfCards() {
        Pageable pageable = PageRequest.of(0, 10);
        List<PaymentCard> list = List.of(sampleCard);
        Page<PaymentCard> expectedPage = new PageImpl<>(list, pageable, list.size());

        when(paymentCardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(expectedPage);

        Page<PaymentCard> actualPage = paymentCardService.getAllCards("EVGENIY TEST", pageable);

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        assertEquals("EVGENIY TEST", actualPage.getContent().get(0).getHolder());
    }

    @Test
    void getCardsByUserId_ShouldReturnListOfCards() {
        when(paymentCardRepository.findPaymentCardByUserId(1L)).thenReturn(List.of(sampleCard));

        List<PaymentCard> cards = paymentCardService.getCardsByUserId(1L);

        assertNotNull(cards);
        assertEquals(1, cards.size());
        assertEquals(10L, cards.get(0).getId());
    }

    @Test
    void updateCard_WhenCardExists_ShouldUpdateFieldsAndReturnCard() {
        PaymentCard cardDetails = new PaymentCard();
        cardDetails.setNumber("8765432187654321");
        cardDetails.setHolder("NEW HOLDER");
        cardDetails.setExpirationDate(LocalDate.of(2035, 12, 31));

        when(paymentCardRepository.findById(10L)).thenReturn(Optional.of(sampleCard));
        when(paymentCardRepository.save(any(PaymentCard.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentCard updatedCard = paymentCardService.updateCard(10L, cardDetails);

        assertNotNull(updatedCard);
        assertEquals("8765432187654321", updatedCard.getNumber());
        assertEquals("NEW HOLDER", updatedCard.getHolder());
        assertEquals(LocalDate.of(2035, 12, 31), updatedCard.getExpirationDate());
        verify(paymentCardRepository, times(1)).save(sampleCard);
    }

    @Test
    void evictUserCache_ShouldExecuteWithoutExceptions() {
        assertDoesNotThrow(() -> paymentCardService.evictUserCache(1L));
    }
}