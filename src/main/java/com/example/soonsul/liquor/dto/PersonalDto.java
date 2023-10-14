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
@ApiModel(description = "내가 남긴 평가 상세 정보")
public class PersonalDto {

    @ApiModelProperty(value = "전통주 pk", position = 1)
    private String liquorId;

    @ApiModelProperty(value = "전통주 이름", position = 2)
    private String name;

    @ApiModelProperty(value = "평균 평점", position = 3)
    private Double averageRating;

    @ApiModelProperty(value = "전통주 대표사진", position = 4)
    private String imageUrl;

    @ApiModelProperty(value = "주종 카테고리", position = 5)
    private String liquorCategory;

    @ApiModelProperty(value = "양조장 리스트", position = 6)
    private List<String> locationList;

    @ApiModelProperty(value = "평가한 날짜", position = 7)
    private LocalDate evaluationDate;

    @ApiModelProperty(value = "개인 평가 pk", position = 8)
    private Long personalEvaluationId;

    @ApiModelProperty(value = "개인 평점", position = 9)
    private Double personalRating;

    @ApiModelProperty(value = "리뷰 pk", position = 10)
    private Long reviewId;

    @ApiModelProperty(value = "리뷰 글", position = 11)
    private String reviewContent;

    @ApiModelProperty(value = "단맛", position = 12)
    private Integer sweetness;

    @ApiModelProperty(value = "산미", position = 13)
    private Integer acidity;

    @ApiModelProperty(value = "탄산", position = 14)
    private Integer carbonicAcid;

    @ApiModelProperty(value = "묵직함", position = 15)
    private Integer heavy;

    @ApiModelProperty(value = "향", position = 16)
    private Integer scent;

    @ApiModelProperty(value = "꾸덕함", position = 17)
    private Integer density;


    @ApiModelProperty(value = "리뷰 개수", position = 18)
    private Integer totalReviewNumber;

    @ApiModelProperty(value = "좋아요 개수", position = 19)
    private Integer goodNumber;

    @ApiModelProperty(value = "댓글 개수", position = 20)
    private Integer commentNumber;

    @ApiModelProperty(value = "판매처", position = 21)
    private List<String> salePlaceList;

    @ApiModelProperty(value = "좋아요 유무", position = 22)
    private boolean flagGood;
}
