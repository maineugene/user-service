package com.innowise.userservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentCardRequestDto {

    @NotBlank(message = "Payment card number required")
    @Pattern(regexp = "^\\d{16,19}$", message = "Payment card number must contain from 16 to 19 digits")
    private String number;

    @NotBlank(message = "Payment card holder's name required")
    private String holder;

    @NotNull(message = "Expiration date of the card required")
    @Future(message = "Expiration date of the card should be in the future")
    private LocalDate expirationDate;

    private boolean active = true;
}
