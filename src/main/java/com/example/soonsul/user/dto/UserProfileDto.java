package com.example.soonsul.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "유저 프로필 정보 (닉네임, 프로필 사진, 알림 허용 유무)")
public class UserProfileDto {

    @ApiModelProperty(value = "유저 닉네임", position = 1)
    private String nickname;

    @ApiModelProperty(value = "유저 프로필 사진", position = 2)
    private String profileImage;

    @ApiModelProperty(value = "활동 알림 허용 유무", position = 3)
    private Boolean flagActivity;

    @ApiModelProperty(value = "광고성 알림 허용 유무", position = 4)
    private Boolean flagAdvertising;
}
