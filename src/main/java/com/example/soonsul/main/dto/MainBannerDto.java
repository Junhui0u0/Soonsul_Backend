package com.example.soonsul.main.dto;

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
@ApiModel(description = "메인 배너 정보")
public class MainBannerDto {

    @ApiModelProperty(value = "메인배너 pk", position = 1)
    private Long mainBannerId;

    @ApiModelProperty(value = "썸네일 사진 url", position = 2)
    private String thumbnail;

    @ApiModelProperty(value = "내용 사진 url", position = 3)
    private String content;

    @ApiModelProperty(value = "메인배너 찜 유무", position = 4)
    private Boolean flagZzim;

    @ApiModelProperty(value = "메인배너 이름", position = 5)
    private String title;
}
