package com.kaiyicode.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        int age,
        Gender gender
) {
}
