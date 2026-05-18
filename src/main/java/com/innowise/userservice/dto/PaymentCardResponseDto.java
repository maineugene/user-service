package com.innowise.userservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentCardResponseDto {

    private Long id;
    private Long userId;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
