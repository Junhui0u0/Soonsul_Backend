package com.example.soonsul.main;

import com.example.soonsul.main.dto.MainBannerDto;
import com.example.soonsul.main.dto.RegionLiquorDto;
import com.example.soonsul.main.dto.WeekLiquorDto;
import com.example.soonsul.main.entity.Sorting;
import com.example.soonsul.main.response.MainBannerListResponse;
import com.example.soonsul.main.response.MainBannerResponse;
import com.example.soonsul.main.response.RegionLiquorResponse;
import com.example.soonsul.main.response.WeekLiquorResponse;
import com.example.soonsul.response.result.ResultCode;
import com.example.soonsul.response.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags="Main")
@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainController {
    private final MainService mainService;
    private final BannerService bannerService;

    @ApiOperation(value = "이번주 가장 사랑받는 전통주")
    @GetMapping("/week-liquor")
    public ResponseEntity<WeekLiquorResponse> getWeekLiquor() {
        final List<WeekLiquorDto> data= mainService.getWeekLiquor();
        return ResponseEntity.ok(WeekLiquorResponse.of(ResultCode.GET_WEEK_LIQUOR_SUCCESS, data));
    }


    @ApiOperation(value = "지금 사랑받는 지역술", notes = "region: [내위치: Around-me] [수도권: Metropolitan-area]" +
            "[강원도: Gangwon-do] [충청도: Chungcheong-do] [경상도: Gyeongsang-do] [전라도: Jeolla-do] [제주도: Jeju-do] " +
            ", sorting: [별점순: STAR] [리뷰순: REVIEW] [가격 낮은순: LOWEST_COST] [가격 높은순: HIGHEST_COST]")
    @GetMapping("/region-liquor")
    public ResponseEntity<RegionLiquorResponse> getRegionLiquor(@RequestParam("region") String region, @RequestParam("sorting") String sorting,
                                                                @RequestParam(value = "latitude", required = false) Double latitude,
                                                                @RequestParam(value = "longitude", required = false) Double longitude) {
        final List<RegionLiquorDto> data= mainService.getRegionLiquor(region, Sorting.valueOf(sorting), latitude, longitude);
        return ResponseEntity.ok(RegionLiquorResponse.of(ResultCode.GET_WEEK_LIQUOR_SUCCESS, data));
    }


    @ApiOperation(value = "메인 배너 리스트 조회")
    @GetMapping("/banners")
    public ResponseEntity<MainBannerListResponse> getMainBannerList() {
        final List<MainBannerDto> data= bannerService.getMainBannerList();
        return ResponseEntity.ok(MainBannerListResponse.of(ResultCode.GET_MAIN_BANNER_LIST_SUCCESS, data));
    }


    @ApiOperation(value = "특정 메인 배너 조회")
    @GetMapping("/banners/{mainBannerId}")
    public ResponseEntity<MainBannerResponse> getMainBanner(@PathVariable("mainBannerId") Long mainBannerId) {
        final MainBannerDto data= bannerService.getMainBanner(mainBannerId);
        return ResponseEntity.ok(MainBannerResponse.of(ResultCode.GET_MAIN_BANNER_SUCCESS, data));
    }


    @ApiOperation(value = "메인 배너 찜 등록")
    @PostMapping("/banners/{mainBannerId}")
    public ResponseEntity<ResultResponse> postBannerZzim(@PathVariable("mainBannerId") Long mainBannerId) {
        bannerService.postBannerZzim(mainBannerId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_BANNER_ZZIM_SUCCESS));
    }

    @ApiOperation(value = "메인 배너 찜 삭제")
    @DeleteMapping("/banners/{mainBannerId}")
    public ResponseEntity<ResultResponse> deleteBannerZzim(@PathVariable("mainBannerId") Long mainBannerId) {
        bannerService.deleteBannerZzim(mainBannerId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_BANNER_ZZIM_SUCCESS));
    }
}
