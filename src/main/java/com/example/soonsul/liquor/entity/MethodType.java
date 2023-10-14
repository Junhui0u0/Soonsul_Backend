package com.example.soonsul.liquor.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MethodType {
    POST("register"),
    PUT("modify"),
    DELETE("erase");

    private final String value;
}
