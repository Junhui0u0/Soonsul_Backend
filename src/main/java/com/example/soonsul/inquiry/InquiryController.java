package com.example.soonsul.inquiry;

import com.example.soonsul.inquiry.dto.InquiryRequest;
import com.example.soonsul.response.result.ResultCode;
import com.example.soonsul.response.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags="Inquiry (문의)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryController {
    private final InquiryService inquiryService;


    @ApiOperation(value = "문의하기", notes = "category: [회원정보 문의: USER_INFORMATION] [서비스 개선 제안: SERVICE]"
                    + " [시스템 오류 제보: SYSTEM] [전통주 정보 등록 요청: LIQUOR_REGISTER] [전통주 정보 정정 제보: LIQUOR_UPDATE]"
                    + " [불편신고: INCONVENIENCE] [기타문의: ETC]")
    @PostMapping()
    public ResponseEntity<ResultResponse> postInquiry(InquiryRequest request) {
        inquiryService.postInquiry(request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_INQUIRY_SUCCESS));
    }

}
