package com.example.soonsul.liquor.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CalculationType {
    ADD("add"),
    SUB("sub");

    private final String value;
}
