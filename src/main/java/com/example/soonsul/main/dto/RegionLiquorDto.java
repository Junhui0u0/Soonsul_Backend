package com.example.soonsul.main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "지역술 정보")
public class RegionLiquorDto {

    @ApiModelProperty(value = "전통주 pk", position = 1)
    private String liquorId;

    @ApiModelProperty(value = "전통주 이름", position = 2)
    private String name;

    @ApiModelProperty(value = "평점", position = 3)
    private Double averageRating;

    @ApiModelProperty(value = "도수", position = 4)
    private Double alcohol;

    @ApiModelProperty(value = "용량", position = 5)
    private Integer capacity;

    @ApiModelProperty(value = "주종 카테고리", position = 6)
    private String liquorCategory;

    @ApiModelProperty(value = "양조장", position = 7)
    private String brewery;

    @ApiModelProperty(value = "최저가", position = 8)
    private Long lowestPrice;

    @ApiModelProperty(value = "스크랩 여부", position = 9)
    private boolean flagScrap;

    @ApiModelProperty(value = "리뷰 평가수", position = 10)
    private Integer ratingNumber;

    @ApiModelProperty(value = "전통주 대표 사진", position = 11)
    private String imageUrl;

    @ApiModelProperty(value = "클릭수", position = 12)
    private Integer clickNumber;

    @ApiModelProperty(value = "거리차 -> 내주변 카테고리일때만 값 리턴", position = 13)
    private Double distance;
}
