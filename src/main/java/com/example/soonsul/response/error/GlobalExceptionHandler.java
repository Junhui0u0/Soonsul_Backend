package com.example.soonsul.response.error;

import com.example.soonsul.liquor.exception.*;
import com.example.soonsul.main.exception.MainBannerNotExist;
import com.example.soonsul.notification.exception.NotificationNotExist;
import com.example.soonsul.user.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex){
        log.error("handleException",ex);
        final ErrorResponse response = new ErrorResponse(ErrorCode.INTER_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotExist.class)
    public ResponseEntity<ErrorResponse> handleUserNotExist(UserNotExist ex){
        log.error("handleUserNotExist",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(RefreshTokenExpired.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenExpired(RefreshTokenExpired ex){
        log.error("handleRefreshTokenExpired",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(TokenExpiredJwt.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredJwt(TokenExpiredJwt ex){
        log.error("handleTokenExpiredJwt",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(OAuthLoginException.class)
    public ResponseEntity<ErrorResponse> handleOAuthLogin(OAuthLoginException ex){
        log.error("handleOAuthLogin",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(LiquorNotExist.class)
    public ResponseEntity<ErrorResponse> handleLiquorNoExist(LiquorNotExist ex){
        log.error("handleLiquorNoExist",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(ReviewNotExist.class)
    public ResponseEntity<ErrorResponse> handleReviewNotExist(ReviewNotExist ex){
        log.error("handleReviewNotExist",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(CommentNotExist.class)
    public ResponseEntity<ErrorResponse> handleCommentNotExist(CommentNotExist ex){
        log.error("handleCommentNotExist",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(PersonalEvaluationNotExist.class)
    public ResponseEntity<ErrorResponse> handlePersonalEvaluationNotExist(PersonalEvaluationNotExist ex){
        log.error("handlePersonalEvaluationNotExist",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(EvaluationNotExist.class)
    public ResponseEntity<ErrorResponse> handleEvaluationNotExist(EvaluationNotExist ex){
        log.error("handleEvaluationNotExist",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(CodeNotExist.class)
    public ResponseEntity<ErrorResponse> handleCodeNotExist(CodeNotExist ex){
        log.error("handleCodeNotExist",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(AccessTokenExpired.class)
    public ResponseEntity<ErrorResponse> handleAccessTokenExpired(AccessTokenExpired ex){
        log.error("handleAccessTokenExpired",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(AccessTokenNotSame.class)
    public ResponseEntity<ErrorResponse> handleAccessTokenNotSame(AccessTokenNotSame ex){
        log.error("handleAccessTokenNotSame",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(SalePlaceNotExist.class)
    public ResponseEntity<ErrorResponse> handleSalePlaceNotExist(SalePlaceNotExist ex){
        log.error("handleSalePlaceNotExist",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(PersonalRatingNull.class)
    public ResponseEntity<ErrorResponse> handlePersonalRatingNull(PersonalRatingNull ex){
        log.error("handlePersonalRatingNull",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(WithdrawalUser.class)
    public ResponseEntity<ErrorResponse> handleWithdrawalUser(WithdrawalUser ex){
        log.error("handleWithdrawalUser",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(MainBannerNotExist.class)
    public ResponseEntity<ErrorResponse> handleMainBannerNotExist(MainBannerNotExist ex){
        log.error("handleMainBannerNotExist",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(NotificationNotExist.class)
    public ResponseEntity<ErrorResponse> handleNotificationNotExist(NotificationNotExist ex){
        log.error("handleNotificationNotExist",ex);
        final ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }
}
