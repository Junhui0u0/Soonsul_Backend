package com.example.soonsul.user.response;

import com.example.soonsul.response.result.ResultCode;
import com.example.soonsul.user.dto.FollowDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.List;

@Getter
@ApiModel(description = "팔로우 응답 모델")
public class FollowResponse {

    @ApiModelProperty(value = "Http 상태 코드")
    private final int status;
    @ApiModelProperty(value = "Business 상태 코드")
    private final String code;
    @ApiModelProperty(value = "응답 메세지")
    private final String message;
    @ApiModelProperty(value = "응답 데이터")
    private final List<FollowDto> data;


    public FollowResponse(ResultCode resultCode, List<FollowDto> data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static FollowResponse of(ResultCode resultCode, List<FollowDto> data) {
        return new FollowResponse(resultCode, data);
    }
}
