package com.example.soonsul.main.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Sorting {
    STAR("star"),
    REVIEW("review"),
    LOWEST_COST("lowest-cost"),
    HIGHEST_COST("highest-cost"),
    DATE("date");

    private final String value;

}
