package com.innowise.userservice.service;

import com.innowise.userservice.model.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentCardService {

    PaymentCard createCard(Long userId, PaymentCard card);

    PaymentCard getCardById(Long id);

    Page<PaymentCard> getAllCards(String holder, Pageable pageable);

    List<PaymentCard> getCardsByUserId(Long userId);

    PaymentCard updateCard(Long id, PaymentCard cardDetails);

    void changeActiveStatus(Long id, boolean active);
}
