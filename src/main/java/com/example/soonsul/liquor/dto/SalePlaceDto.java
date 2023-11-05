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
@ApiModel(description = "전통주 - 판매처 정보")
public class SalePlaceDto {

    @ApiModelProperty(value = "판매처 이름")
    private String salePlace;

    @ApiModelProperty(value = "전화번호")
    private String phoneNumber;

    @ApiModelProperty(value = "사이트 url")
    private String siteUrl;

}
