package com.example.soonsul.user.oauth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@ApiModel(description = "회원가입 정보 요청")
public class SignupDto {
    private String oauthId;
    private String oauthProvider;
    private String gender;
    private boolean flagAge;
    private boolean flagTerms;
    private boolean flagPrivacy;
    private LocalDate birthday;
    @ApiModelProperty(value = "주로 전통주 마시는 시기", position = 8)
    private String period;
    @ApiModelProperty(value = "선호하는 전통주", position = 9)
    private String liquor;
    @ApiModelProperty(value = "주로 전통주 마시는 장소", position = 10)
    private String place;
    @ApiModelProperty(value = "활동 알림 허용 유무 (허용: true, 허용안함: false)", position = 11)
    private boolean flagActivity;
    @ApiModelProperty(value = "광고성 알림 허용 유무 (허용: true, 허용안함: false)", position = 12)
    private boolean flagAdvertising;
}
