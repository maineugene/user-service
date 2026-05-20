package com.innowise.userservice.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class UserResponseDto {

    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
