package com.innowise.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequestDto {

    @NotBlank(message = "Name required")
    private String name;

    @NotBlank(message = "Surname required")
    private String surname;

    @NotNull(message = "Date of birth required")
    @Past(message = "The date of birth must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Email required")
    @Email(message = "Incorrect format of email")
    private String email;

    private boolean active = true;
}
