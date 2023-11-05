package com.example.soonsul.search.dto;

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
@ApiModel(description = "전통주 검색 결과 정보")
public class SearchDto {

    @ApiModelProperty(value = "전통주 pk", position = 1)
    private String liquorId;

    @ApiModelProperty(value = "전통주 이름", position = 2)
    private String name;

    @ApiModelProperty(value = "주종 카테고리", position = 3)
    private String liquorCategory;

    @ApiModelProperty(value = "양조장", position = 4)
    private String brewery;

    @ApiModelProperty(value = "전통주 대표 사진", position = 5)
    private String imageUrl;

    @ApiModelProperty(value = "문자가 포함된 시작 위치", position = 6)
    private Integer startIdx;
}
