package com.example.soonsul.main.exception;

import com.example.soonsul.response.error.ErrorCode;
import lombok.Getter;

@Getter
public class MainBannerNotExist extends RuntimeException {

    private final ErrorCode errorCode;

    public MainBannerNotExist(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
