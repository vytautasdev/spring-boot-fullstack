package com.vytautasdev.customerdatabase.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
