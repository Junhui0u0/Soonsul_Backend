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
@ApiModel(description = "전통주 상세 정보")
public class ReviewDto {

    @ApiModelProperty(value = "리뷰 pk", position = 1)
    private Long reviewId;

    @ApiModelProperty(value = "전통주 개인 평점", position = 2)
    private Double averageRating;

    @ApiModelProperty(value = "리뷰 내용", position = 3)
    private String content;

    @ApiModelProperty(value = "리뷰 좋아요 개수", position = 4)
    private Integer goodNumber;

    @ApiModelProperty(value = "리뷰 작성일", position = 5)
    private String createdDate;

    @ApiModelProperty(value = "댓글 개수", position = 6)
    private Integer commentNumber;


    @ApiModelProperty(value = "유저 pk", position = 7)
    private String userId;

    @ApiModelProperty(value = "유저 닉네임", position = 8)
    private String nickname;

    @ApiModelProperty(value = "프로필 이미지", position = 9)
    private String profileImage;

    @ApiModelProperty(value = "유저가 남긴 리뷰 개수", position = 10)
    private Integer reviewNumber;


    @ApiModelProperty(value = "좋아요 유무", position = 11)
    private boolean flagGood;

    @ApiModelProperty(value = "총 리뷰 개수", position = 12)
    private Integer totalReviewNumber;
}
