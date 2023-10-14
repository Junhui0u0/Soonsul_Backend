package com.example.soonsul.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "팔로우 정보")
public class FollowDto {

    @ApiModelProperty(value = "유저 id", position = 1)
    private String userId;

    @ApiModelProperty(value = "유저 닉네임", position = 2)
    private String nickname;

    @ApiModelProperty(value = "프로필 사진 url", position = 3)
    private String profileImage;
}
