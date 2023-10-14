package com.example.soonsul.liquor.response;

import com.example.soonsul.liquor.dto.PersonalDto;
import com.example.soonsul.response.result.ResultCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(description = "유저가 남긴 평가 응답 모델")
public class PersonalResponse {

    @ApiModelProperty(value = "Http 상태 코드")
    private final int status;
    @ApiModelProperty(value = "Business 상태 코드")
    private final String code;
    @ApiModelProperty(value = "응답 메세지")
    private final String message;
    @ApiModelProperty(value = "응답 데이터")
    private final PersonalDto data;


    public PersonalResponse(ResultCode resultCode, PersonalDto data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static PersonalResponse of(ResultCode resultCode, PersonalDto data) {
        return new PersonalResponse(resultCode, data);
    }
}
