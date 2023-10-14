package com.example.soonsul.notification.response;

import com.example.soonsul.notification.dto.NotificationDto;
import com.example.soonsul.response.result.ResultCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.List;

@Getter
@ApiModel(description = "알림 내역 리스트 응답 모델")
public class NotificationListResponse {

    @ApiModelProperty(value = "Http 상태 코드")
    private final int status;
    @ApiModelProperty(value = "Business 상태 코드")
    private final String code;
    @ApiModelProperty(value = "응답 메세지")
    private final String message;
    @ApiModelProperty(value = "응답 데이터")
    private final List<NotificationDto> data;


    public NotificationListResponse(ResultCode resultCode, List<NotificationDto> data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static NotificationListResponse of(ResultCode resultCode, List<NotificationDto> data) {
        return new NotificationListResponse(resultCode, data);
    }
}
