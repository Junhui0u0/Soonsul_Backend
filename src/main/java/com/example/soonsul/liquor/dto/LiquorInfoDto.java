package com.example.soonsul.liquor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "전통주 상세 정보")
public class LiquorInfoDto {

    @ApiModelProperty(value = "전통주 pk", position = 1)
    private String liquorId;

    @ApiModelProperty(value = "전통주 이름", position = 2)
    private String name;

    @ApiModelProperty(value = "성분", position = 3)
    private String ingredient;

    @ApiModelProperty(value = "평균 평점", position = 4)
    private Double averageRating;

    @ApiModelProperty(value = "최저가", position = 5)
    private Long lowestPrice;

    @ApiModelProperty(value = "도수", position = 6)
    private Double alcohol;

    @ApiModelProperty(value = "용량", position = 7)
    private Integer capacity;

    @ApiModelProperty(value = "지역 이름", position = 8)
    private String region;

    @ApiModelProperty(value = "전통주 대표사진", position = 9)
    private String imageUrl;

    @ApiModelProperty(value = "주종 카테고리", position = 10)
    private String liquorCategory;

    @ApiModelProperty(value = "전통주 개인평점", position = 11)
    private Double liquorPersonalRating;

    @ApiModelProperty(value = "리뷰 평가수", position = 12)
    private Integer ratingNumber;

    @ApiModelProperty(value = "전통주 필터링 정보 ex)20대 여성이 가장 좋아하는 술", position = 13)
    private List<LiquorFilteringDto> filtering;

    @ApiModelProperty(value = "스크랩 유무", position = 14)
    private boolean flagScrap;

    @ApiModelProperty(value = "스크랩 날짜", position = 15)
    private LocalDate scrapDate;


    @ApiModelProperty(value = "스크랩 총 개수", position = 16)
    private Integer totalScrapNumber;

}
