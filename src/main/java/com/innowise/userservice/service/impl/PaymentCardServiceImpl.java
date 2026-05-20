package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BusinessLogicException;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.model.PaymentCard;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.repository.specification.PaymentCardSpecification;
import com.innowise.userservice.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private final UserRepository userRepository;
    private final PaymentCardRepository paymentCardRepository;
    private static final int MAX_CARDS_PER_USER = 5;

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public PaymentCard createCard(Long userId, PaymentCard card) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int currentCardCount = paymentCardRepository.countCardsByUserId(userId);
        if (currentCardCount >= MAX_CARDS_PER_USER) {
            throw new BusinessLogicException("User with id" + userId
                    + "already has the maximum number of cards");
        }

        card.setUser(user);
        return paymentCardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public PaymentCard getCardById(Long id) {
        return paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Page<PaymentCard> getAllCards(String holder, Pageable pageable) {
        var spec = PaymentCardSpecification.filterByHolder(holder);
        return paymentCardRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public List<PaymentCard> getCardsByUserId(Long userId) {
        return paymentCardRepository.findPaymentCardByUserId(userId);
    }

    @CacheEvict(value = "users", key = "#result.user.id")
    @Transactional
    public PaymentCard updateCard(Long id, PaymentCard cardDetails) {
        PaymentCard existingCard = getCardById(id);
        existingCard.setNumber(cardDetails.getNumber());
        existingCard.setHolder(cardDetails.getHolder());
        existingCard.setExpirationDate(cardDetails.getExpirationDate());

        return paymentCardRepository.save(existingCard);
    }

    @Transactional
    public void changeActiveStatus(Long id, boolean active) {
        PaymentCard existingCard = getCardById(id);
        paymentCardRepository.updateActiveStatus(id, active);

        evictUserCache(existingCard.getUser().getId());
    }

    @CacheEvict(value = "users", key = "#userId")
    public void evictUserCache(Long userId) {
    }
}
