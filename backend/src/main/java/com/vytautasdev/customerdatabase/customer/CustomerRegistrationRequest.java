package com.vytautasdev.customerdatabase.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
