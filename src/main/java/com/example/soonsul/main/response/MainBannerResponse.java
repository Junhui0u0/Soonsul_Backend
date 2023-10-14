package com.example.soonsul.main.response;

import com.example.soonsul.main.dto.MainBannerDto;
import com.example.soonsul.response.result.ResultCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(description = "메인 배너 응답 모델")
public class MainBannerResponse {

    @ApiModelProperty(value = "Http 상태 코드")
    private final int status;
    @ApiModelProperty(value = "Business 상태 코드")
    private final String code;
    @ApiModelProperty(value = "응답 메세지")
    private final String message;
    @ApiModelProperty(value = "응답 데이터")
    private final MainBannerDto data;


    public MainBannerResponse(ResultCode resultCode, MainBannerDto data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static MainBannerResponse of(ResultCode resultCode, MainBannerDto data) {
        return new MainBannerResponse(resultCode, data);
    }
}
