package com.example.soonsul.liquor.dto;

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
@ApiModel(description = "전통주 - 소재지 정보")
public class LocationDto {

    @ApiModelProperty(value = "양조장 이름")
    private String brewery;

    @ApiModelProperty(value = "위도")
   private Double latitude;

    @ApiModelProperty(value = "경도")
    private Double longitude;

    @ApiModelProperty(value = "양조장 위치")
    private String location;

}
