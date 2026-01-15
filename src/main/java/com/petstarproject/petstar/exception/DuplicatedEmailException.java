package com.petstarproject.petstar.exception;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException(@Email @NotBlank String email) {
        super("Email already exists: " + email);
    }
}
