package com.example.soonsul.promotion;

import com.example.soonsul.promotion.dto.PromotionDto;
import com.example.soonsul.promotion.response.PromotionListResponse;
import com.example.soonsul.promotion.response.PromotionResponse;
import com.example.soonsul.response.result.ResultCode;
import com.example.soonsul.response.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags="Promotion")
@RestController
@RequiredArgsConstructor
@RequestMapping("/promotion")
public class PromotionController {
    private final PromotionService promotionService;

    @ApiOperation(value = "모든 프로모션 조회")
    @GetMapping()
    public ResponseEntity<PromotionListResponse> getPromotionList() {
        final List<PromotionDto> data= promotionService.getPromotionList("promotion");
        return ResponseEntity.ok(PromotionListResponse.of(ResultCode.GET_PROMOTION_LIST_SUCCESS, data));
    }


    @ApiOperation(value = "특정 프로모션 조회")
    @GetMapping("/{promotionId}")
    public ResponseEntity<PromotionResponse> getPromotion(@PathVariable("promotionId") Long promotionId) {
        final PromotionDto data= promotionService.getPromotion(promotionId);
        return ResponseEntity.ok(PromotionResponse.of(ResultCode.GET_PROMOTION_SUCCESS, data));
    }


    @ApiOperation(value = "찜 등록")
    @PostMapping("/{promotionId}/zzim")
    public ResponseEntity<ResultResponse> postZzim(@PathVariable("promotionId") Long promotionId) {
        promotionService.postZzim(promotionId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_ZZIM_SUCCESS));
    }


    @ApiOperation(value = "찜 삭제")
    @DeleteMapping("/{promotionId}/zzim")
    public ResponseEntity<ResultResponse> deleteZzim(@PathVariable("promotionId") Long promotionId) {
        promotionService.deleteZzim(promotionId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_ZZIM_SUCCESS));
    }

}
