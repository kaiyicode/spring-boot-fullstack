package com.kaiyicode.customer;

public record CustomerUpdateRequest (
        String name,
        String email,
        Integer age
) {
}
