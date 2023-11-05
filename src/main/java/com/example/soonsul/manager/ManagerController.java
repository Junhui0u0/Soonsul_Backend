package com.example.soonsul.manager;

import com.example.soonsul.liquor.dto.ReviewDto;
import com.example.soonsul.liquor.response.ReviewListResponse;
import com.example.soonsul.response.result.ResultCode;
import com.example.soonsul.response.result.ResultResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Api(tags="관리자")
@RestController
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {
    private final ManagerService managerService;
    private final GoogleSheetsService googleSheetsService;


    @ApiOperation(value = "모든 전통주 메인사진 s3에 등록")
    @PostMapping(value = "/liquor/main-photo", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResultResponse> postMainPhoto(@RequestPart("images") List<MultipartFile> images) {
        managerService.postMainPhoto(images);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_MAIN_PHOTO));
    }

    @ApiOperation(value = "모든 전통주 기본사진 s3에 등록")
    @PostMapping(value = "/liquor/default-photo")
    public ResponseEntity<ResultResponse> postDefaultPhoto() {
        managerService.postDefaultPhoto();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_MAIN_PHOTO));
    }


    @ApiOperation(value = "모든 전통주 평가, 평가수 추가", notes = "전통주 데이터 넣은 후  해당 api 실행하기")
    @PostMapping("/liquor/init")
    public ResponseEntity<ResultResponse> postLiquorInit() {
        managerService.postLiquorInit();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_LIQUOR_INIT_SUCCESS));
    }


    @ApiOperation(value = "전통주 저장")
    @PostMapping("/liquors")
    public ResponseEntity<ResultResponse> postLiquor(String spreadsheetId, String range) throws IOException {
        googleSheetsService.postLiquor(spreadsheetId, range);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.MANAGE_ACTION_SUCCESS));
    }


    @ApiOperation(value = "전통주 저장하기 전에 데이터가 올바른 형식인지 체크", notes = "양조장 주소나 행정구역 이름이 잘못 저장되어 있는 전통주를 리스트로 반환")
    @GetMapping("/data-check")
    public ResponseEntity<ResultResponse> checkDataFormat(String spreadsheetId, @RequestParam List<String> range) throws IOException {
        List<Pair<String,String>> data= googleSheetsService.checkDataFormat(spreadsheetId, range);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.MANAGE_ACTION_SUCCESS, data));
    }

    @ApiOperation(value = "지역코드 체크", notes = "지역코드가 잘못 저장된 전통주를 리스트로 반환")
    @GetMapping("/region-code/check")
    public ResponseEntity<ResultResponse> getRegionCodeCheck(String spreadsheetId, String range) throws IOException {
        List<String> data= googleSheetsService.getRegionCodeCheck(spreadsheetId, range);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.MANAGE_ACTION_SUCCESS, data));
    }

    @ApiOperation(value = "액세스토큰 조회")
    @GetMapping("/token/{userId}")
    public ResponseEntity<ResultResponse> getToken(@PathVariable("userId") String userId) throws IOException {
        String data= googleSheetsService.getToken(userId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.MANAGE_ACTION_SUCCESS, data));
    }

    @ApiOperation(value = "프로모션 저장")
    @PostMapping(value = "/promotion", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> postPromotion(@RequestPart(value = "thumbnail", required = false) MultipartFile image, @RequestPart(value = "content", required = false) MultipartFile content,
                                                        @RequestPart(value = "title", required = false) String title, @RequestPart(value = "location", required = false) String location,
                                                        @RequestParam(value = "beginDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beginDate, @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws FirebaseMessagingException {
        managerService.postPromotion(image, content, title, location, beginDate, endDate);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.MANAGE_ACTION_SUCCESS));
    }

    @ApiOperation(value = "프로모션 삭제")
    @DeleteMapping(value = "/promotion/{promotionId}")
    public ResponseEntity<ResultResponse> deletePromotion(@PathVariable("promotionId") Long promotionId) {
        managerService.deletePromotion(promotionId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.MANAGE_ACTION_SUCCESS));
    }


    @ApiOperation(value = "모든 리뷰 조회")
    @GetMapping("/reviews")
    public ResponseEntity<ReviewListResponse> getAllReview() {
        final List<ReviewDto> data= managerService.getAllReview();
        return ResponseEntity.ok(ReviewListResponse.of(ResultCode.MANAGE_ACTION_SUCCESS, data));
    }
}
