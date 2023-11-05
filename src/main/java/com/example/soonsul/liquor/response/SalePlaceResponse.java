package com.example.soonsul.liquor.response;

import com.example.soonsul.liquor.dto.SalePlaceDto;
import com.example.soonsul.response.result.ResultCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(description = "전통주 판매처 응답 모델")
public class SalePlaceResponse {

    @ApiModelProperty(value = "Http 상태 코드")
    private final int status;
    @ApiModelProperty(value = "Business 상태 코드")
    private final String code;
    @ApiModelProperty(value = "응답 메세지")
    private final String message;
    @ApiModelProperty(value = "응답 데이터")
    private final SalePlaceDto data;


    public SalePlaceResponse(ResultCode resultCode, SalePlaceDto data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static SalePlaceResponse of(ResultCode resultCode, SalePlaceDto data) {
        return new SalePlaceResponse(resultCode, data);
    }
}
