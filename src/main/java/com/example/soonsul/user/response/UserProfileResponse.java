package com.example.soonsul.user.response;

import com.example.soonsul.liquor.response.CommentListResponse;
import com.example.soonsul.response.result.ResultCode;
import com.example.soonsul.user.dto.UserProfileDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;


@Getter
@ApiModel(description = "유저 프로필 응답 모델")
public class UserProfileResponse {

    @ApiModelProperty(value = "Http 상태 코드")
    private final int status;
    @ApiModelProperty(value = "Business 상태 코드")
    private final String code;
    @ApiModelProperty(value = "응답 메세지")
    private final String message;
    @ApiModelProperty(value = "응답 데이터")
    private final UserProfileDto data;


    public UserProfileResponse(ResultCode resultCode, UserProfileDto data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static UserProfileResponse of(ResultCode resultCode, UserProfileDto data) {
        return new UserProfileResponse(resultCode, data);
    }
}
