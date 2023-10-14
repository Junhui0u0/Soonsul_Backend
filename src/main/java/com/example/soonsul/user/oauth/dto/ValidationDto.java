package com.example.soonsul.user.oauth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "토큰 유효성 검증 정보")
public class ValidationDto {

    @ApiModelProperty(value = "토큰 유효성 여부", position = 1)
    private boolean flagValidation;

    @ApiModelProperty(value = "유저 닉네임", position = 2)
    private String nickname;

    @ApiModelProperty(value = "유저 프로필 사진 url", position = 3)
    private String profileImage;

}
