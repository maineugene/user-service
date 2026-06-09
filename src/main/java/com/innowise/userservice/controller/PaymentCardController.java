package com.innowise.userservice.controller;

import com.innowise.userservice.dto.PaymentCardRequestDto;
import com.innowise.userservice.dto.PaymentCardResponseDto;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.model.PaymentCard;
import com.innowise.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentCardController {

    private final PaymentCardService cardService;
    private final PaymentCardMapper cardMapper;

    @PostMapping("/users/{userId}/cards")
    public ResponseEntity<PaymentCardResponseDto> createCard(
            @PathVariable Long userId,
            @Valid @RequestBody PaymentCardRequestDto requestDto) {
        PaymentCard card = cardMapper.toEntity(requestDto);
        PaymentCard savedCard = cardService.createCard(userId, card);
        return new ResponseEntity<>(cardMapper.toResponseDto(savedCard), HttpStatus.CREATED);
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<PaymentCardResponseDto> getCardById(@PathVariable Long id) {
        PaymentCard card = cardService.getCardById(id);
        return ResponseEntity.ok(cardMapper.toResponseDto(card));
    }

    @GetMapping("/users/{userId}/cards")
    public ResponseEntity<List<PaymentCardResponseDto>> getCardsByUserId(@PathVariable Long userId) {
        List<PaymentCard> cards = cardService.getCardsByUserId(userId);
        List<PaymentCardResponseDto> dtos = cards.stream()
                .map(cardMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/cards/{id}")
    public ResponseEntity<PaymentCardResponseDto> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody PaymentCardRequestDto requestDto) {

        PaymentCard cardDetails = cardMapper.toEntity(requestDto);
        PaymentCard updatedCard = cardService.updateCard(id, cardDetails);
        return ResponseEntity.ok(cardMapper.toResponseDto(updatedCard));
    }

    @PatchMapping("/cards/{id}/active")
    public ResponseEntity<Void> changeActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        cardService.changeActiveStatus(id, active);
        return ResponseEntity.ok().build();
    }
}
