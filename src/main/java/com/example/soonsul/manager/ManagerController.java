package com.example.soonsul.manager;

import com.example.soonsul.response.result.ResultCode;
import com.example.soonsul.response.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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


    @ApiOperation(value = "모든 전통주 평가, 평가수 추가", notes = "전통주 데이터 넣은 후  해당 api 실행하기")
    @PostMapping("/liquor/init")
    public ResponseEntity<ResultResponse> postLiquorInit() {
        managerService.postLiquorInit();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_LIQUOR_INIT_SUCCESS));
    }


    @ApiOperation(value = "모든 소재지에 위도,경도값 추가", notes = "소재지 데이터 넣은 후  해당 api 실행하기")
    @PostMapping("/location/init")
    public ResponseEntity<ResultResponse> postLocationInit() {
        managerService.postLocationInit();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_LOCATION_INIT_SUCCESS));
    }


    @ApiOperation(value = "전통주 저장")
    @PostMapping("/liquors")
    public ResponseEntity<ResultResponse> postLiquor(String spreadsheetId, String range) throws IOException {
        googleSheetsService.postLiquor(spreadsheetId, range);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.MANAGE_ACTION_SUCCESS));
    }


    @ApiOperation(value = "소재지 주소 체크", notes = "소재지가 잘못 저장된 전통주를 리스트로 반환")
    @GetMapping("/location/check")
    public ResponseEntity<ResultResponse> getLocationCheck(String spreadsheetId, String range) throws IOException {
        List<String> data= googleSheetsService.getLocationCheck(spreadsheetId, range);
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

}
