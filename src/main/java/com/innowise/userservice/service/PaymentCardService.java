package com.innowise.userservice.service;

import com.innowise.userservice.model.PaymentCard;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.repository.specification.PaymentCardSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PaymentCardService {

    private UserRepository userRepository;
    private PaymentCardRepository paymentCardRepository;
    private static final int MAX_CARDS_PER_USER = 5;

    public PaymentCard createCard(Long userId, PaymentCard card){
        User user = userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("User not found"));

        int currentCardCount = paymentCardRepository.countCardsByUserId(userId);
        if (currentCardCount >= MAX_CARDS_PER_USER){
            throw new IllegalArgumentException("User with id" + userId
                    + "already has the maximum number of cards");
        }

        card.setUser(user);
        return paymentCardRepository.save(card);
    }

    public PaymentCard getCardById(Long id) {
        return paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card with id " + id + " not found"));
    }

    public Page<PaymentCard> getAllCards(String holder, Pageable pageable) {
        var spec = PaymentCardSpecification.filterByHolder(holder);
        return paymentCardRepository.findAll(spec, pageable);
    }

    public List<PaymentCard> getCardsByUserId(Long userId) {
        return paymentCardRepository.findPaymentCardByUserId(userId);
    }

    public PaymentCard updateCard(Long id, PaymentCard cardDetails) {
        PaymentCard existingCard = getCardById(id);
        existingCard.setNumber(cardDetails.getNumber());
        existingCard.setHolder(cardDetails.getHolder());
        existingCard.setExpirationDate(cardDetails.getExpirationDate());
        return paymentCardRepository.save(existingCard);
    }

    public void changeActiveStatus(Long id, boolean active) {
        if (!paymentCardRepository.existsById(id)) {
            throw new RuntimeException("Payment card not found");
        }
        paymentCardRepository.updateActiveStatus(id, active);
    }
}
