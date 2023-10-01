package com.example.soonsul.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationType {
    FOLLOW("follow"),
    REVIEW_GOOD("review"),
    COMMENT("comment"),
    RECOMMENT("recomment");

    private final String value;
}
