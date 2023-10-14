package com.example.soonsul.inquiry.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InquiryType {
    USER_INFORMATION("user_information"),
    SERVICE("service"),
    SYSTEM("system"),
    LIQUOR_REGISTER("liquor_register"),
    LIQUOR_UPDATE("liquor_update"),
    INCONVENIENCE("inconvenience"),
    ETC("etc");

    private final String value;
}
