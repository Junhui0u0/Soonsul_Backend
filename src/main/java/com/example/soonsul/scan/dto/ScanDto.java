package com.example.soonsul.scan.dto;

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
@ApiModel(description = "스캔한 전통주 정보")
public class ScanDto {

    @ApiModelProperty(value = "스캔 pk", position = 1)
    private Long scanId;

    @ApiModelProperty(value = "스캔 날짜", position = 2)
    private LocalDate scanDate;

    @ApiModelProperty(value = "스캔한 사진", position = 3)
    private String imageUrl;

    @ApiModelProperty(value = "전통주 pk", position = 4)
    private String liquorId;

    @ApiModelProperty(value = "전통주 이름", position = 5)
    private String name;

    @ApiModelProperty(value = "주종 카테고리", position = 6)
    private String liquorCategory;

    @ApiModelProperty(value = "양조장", position = 7)
    private String brewery;

    @ApiModelProperty(value = "전통주 평점", position = 8)
    private Double averageRating;

    @ApiModelProperty(value = "전통주 평가 여부", position = 9)
    private boolean flagEvaluation;

    @ApiModelProperty(value = "전통주 개인 평점", position = 10)
    private Double personalRating;

}
