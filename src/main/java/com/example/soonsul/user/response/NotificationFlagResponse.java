package com.example.soonsul.user.response;

import com.example.soonsul.response.result.ResultCode;
import com.example.soonsul.user.dto.NotificationFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(description = "알림 유무 응답 모델")
public class NotificationFlagResponse {

    @ApiModelProperty(value = "Http 상태 코드")
    private final int status;
    @ApiModelProperty(value = "Business 상태 코드")
    private final String code;
    @ApiModelProperty(value = "응답 메세지")
    private final String message;
    @ApiModelProperty(value = "응답 데이터")
    private final NotificationFlag data;


    public NotificationFlagResponse(ResultCode resultCode, NotificationFlag data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static NotificationFlagResponse of(ResultCode resultCode, NotificationFlag data) {
        return new NotificationFlagResponse(resultCode, data);
    }
}
