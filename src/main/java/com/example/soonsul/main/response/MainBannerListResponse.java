package com.example.soonsul.main.response;

import com.example.soonsul.main.dto.MainBannerDto;
import com.example.soonsul.response.result.ResultCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.List;

@Getter
@ApiModel(description = "메인 배너 리스트 응답 모델")
public class MainBannerListResponse {

    @ApiModelProperty(value = "Http 상태 코드")
    private final int status;
    @ApiModelProperty(value = "Business 상태 코드")
    private final String code;
    @ApiModelProperty(value = "응답 메세지")
    private final String message;
    @ApiModelProperty(value = "응답 데이터")
    private final List<MainBannerDto> data;


    public MainBannerListResponse(ResultCode resultCode, List<MainBannerDto> data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static MainBannerListResponse of(ResultCode resultCode, List<MainBannerDto> data) {
        return new MainBannerListResponse(resultCode, data);
    }
}
