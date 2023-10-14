package com.example.soonsul.liquor.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "댓글 정보")
public class CommentDto {

    @ApiModelProperty(value = "작성자 pk", position = 1)
    private String userId;

    @ApiModelProperty(value = "작성자 닉네임", position = 2)
    private String nickname;

    @ApiModelProperty(value = "작성자 프로필 사진", position = 3)
    private String profileImage;


    @ApiModelProperty(value = "리뷰 pk", position = 4)
    private Long reviewId;

    @ApiModelProperty(value = "댓글 pk", position = 5)
    private Long commentId;

    @ApiModelProperty(value = "댓글 내용", position = 6)
    private String content;

    @ApiModelProperty(value = "댓글 작성일", position = 7)
    private String createdDate;

    @ApiModelProperty(value = "댓글 좋아요 개수", position = 8)
    private Integer good;


    @ApiModelProperty(value = "대댓글 정보 (최대2개)", position = 9)
    private List<ReCommentDto> reCommentList;

    @ApiModelProperty(value = "대댓글 개수", position = 10)
    private Integer reCommentNumber;

    @ApiModelProperty(value = "댓글 작성자가 본인인지에 대한 여부", position = 11)
    private boolean flagMySelf;


    @ApiModelProperty(value = "좋아요 유무", position = 12)
    private boolean flagGood;

}
