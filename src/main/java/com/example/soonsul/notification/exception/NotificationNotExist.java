package com.example.soonsul.notification.exception;

import com.example.soonsul.response.error.ErrorCode;
import lombok.Getter;

@Getter
public class NotificationNotExist extends RuntimeException {

    private final ErrorCode errorCode;

    public NotificationNotExist(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
