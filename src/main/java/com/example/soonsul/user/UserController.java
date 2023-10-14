package com.example.soonsul.user;

import com.example.soonsul.liquor.dto.LiquorInfoDto;
import com.example.soonsul.liquor.dto.PersonalDto;
import com.example.soonsul.liquor.response.LiquorInfoListResponse;
import com.example.soonsul.liquor.response.PersonalListResponse;
import com.example.soonsul.liquor.service.EvaluationService;
import com.example.soonsul.liquor.service.LiquorService;
import com.example.soonsul.liquor.service.PersonalService;
import com.example.soonsul.main.entity.Sorting;
import com.example.soonsul.notification.NotificationService;
import com.example.soonsul.notification.dto.PushNotification;
import com.example.soonsul.notification.entity.NotificationType;
import com.example.soonsul.promotion.PromotionService;
import com.example.soonsul.promotion.dto.PromotionDto;
import com.example.soonsul.promotion.response.PromotionListResponse;
import com.example.soonsul.response.result.ResultCode;
import com.example.soonsul.response.result.ResultResponse;
import com.example.soonsul.scan.ScanService;
import com.example.soonsul.scan.dto.ScanDto;
import com.example.soonsul.scan.response.ScanResponse;
import com.example.soonsul.user.dto.FollowDto;
import com.example.soonsul.user.dto.NotificationFlag;
import com.example.soonsul.user.dto.UserProfileDto;
import com.example.soonsul.user.response.FollowResponse;
import com.example.soonsul.user.response.NotificationFlagResponse;
import com.example.soonsul.user.response.UserProfileResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags="User")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final PromotionService promotionService;
    private final ScanService scanService;
    private final LiquorService liquorService;
    private final PersonalService personalService;
    private final EvaluationService evaluationService;
    private final NotificationService notificationService;


    @ApiOperation(value = "유저 닉네임 변경")
    @PutMapping(value = "/profile/nickname")
    public ResponseEntity<ResultResponse> putNickname(@RequestParam("nickname") String nickname) {
        userService.putNickname(nickname);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PUT_PROFILE_NICKNAME_SUCCESS));
    }


    @ApiOperation(value = "유저 프로필 이미지 변경")
    @PutMapping(value = "/profile/image", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResultResponse> putProfileImage(@RequestPart(value = "image", required = false) MultipartFile image) {
        userService.putProfileImage(image);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PUT_PROFILE_IMAGE_SUCCESS));
    }


    @ApiOperation(value = "유저 닉네임 사용여부(중복확인)", notes = "true: 닉네임 사용할 수 있음, false: 사용할 수 없음")
    @GetMapping("/nickname/check")
    public ResponseEntity<ResultResponse> getNicknameCheck(@RequestParam("nickname") String nickname) {
        final boolean data= userService.getNicknameCheck(nickname);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_NICKNAME_CHECK_SUCCESS, data));
    }


    @ApiOperation(value = "유저 찜 리스트 조회")
    @GetMapping("/zzims")
    public ResponseEntity<PromotionListResponse> getUserZzim() {
        final List<PromotionDto> result= promotionService.getPromotionList("all");
        final List<PromotionDto> data= result.stream().filter(PromotionDto::isFlagZzim).collect(Collectors.toList());
        return ResponseEntity.ok(PromotionListResponse.of(ResultCode.GET_USER_ZZIM_SUCCESS, data));
    }


    @ApiOperation(value = "사진 히스토리 조회")
    @GetMapping("/histories")
    public ResponseEntity<ScanResponse> getUserHistory(@PageableDefault(size=10, sort = "scan_id", direction = Sort.Direction.DESC) Pageable pageable) {
        final List<ScanDto> data= scanService.getScanList(pageable);
        return ResponseEntity.ok(ScanResponse.of(ResultCode.GET_USER_HISTORY_SUCCESS, data));
    }


    @ApiOperation(value = "스크랩한 전통주 조회", notes = "sorting: [최신순: DATE] [별점순: STAR] [가격 낮은순: LOWEST_COST] " +
            "[가격 높은순: HIGHEST_COST]")
    @GetMapping("/scraps")
    public ResponseEntity<LiquorInfoListResponse> getUserScrap(@PageableDefault(size=10) Pageable pageable, String sorting) {
        final List<LiquorInfoDto> data= liquorService.getScrapList(pageable, Sorting.valueOf(sorting));
        return ResponseEntity.ok(LiquorInfoListResponse.of(ResultCode.GET_USER_SCRAP_SUCCESS, data));
    }


    @ApiOperation(value = "내가 남긴 평가리스트 조회")
    @GetMapping("/evaluations")
    public ResponseEntity<PersonalListResponse> getUserEvaluation(@PageableDefault(size=10, sort = "personal_evaluation_id", direction = Sort.Direction.DESC) Pageable pageable) {
        final List<PersonalDto> data= personalService.getPersonalEvaluationList(null, pageable);
        return ResponseEntity.ok(PersonalListResponse.of(ResultCode.GET_USER_EVALUATION_SUCCESS, data));
    }


    @ApiOperation(value = "내가 남긴 평가 삭제 (전통주 평점, 맛평가, 리뷰 삭제)")
    @DeleteMapping("/evaluation/{liquorId}")
    public ResponseEntity<ResultResponse> deleteUserEvaluation(@PathVariable("liquorId") String liquorId) {
        evaluationService.deletePersonalEvaluation(liquorId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_USER_EVALUATION_SUCCESS));
    }


    @ApiOperation(value = "다른 유저의 프로필 정보 (닉네임, 프로필 사진)")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getOtherUserProfile(@PathVariable("userId") String userId) {
        final UserProfileDto data= userService.getUserProfile(userId);
        return ResponseEntity.ok(UserProfileResponse.of(ResultCode.GET_USER_PROFILE_SUCCESS, data));
    }


    @ApiOperation(value = "유저의 프로필 정보 (닉네임, 프로필 사진, 알림 허용 유무)")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        final UserProfileDto data= userService.getUserProfile(null);
        return ResponseEntity.ok(UserProfileResponse.of(ResultCode.GET_USER_PROFILE_SUCCESS, data));
    }


    @ApiOperation(value = "다른 유저의 리뷰 조회")
    @GetMapping("/{userId}/reviews")
    public ResponseEntity<PersonalListResponse> getUserReviewList(@PathVariable("userId") String userId, @PageableDefault(size=10, sort = "personal_evaluation_id", direction = Sort.Direction.DESC) Pageable pageable) {
        final List<PersonalDto> data= personalService.getPersonalEvaluationList(userId, pageable);
        return ResponseEntity.ok(PersonalListResponse.of(ResultCode.GET_USER_EVALUATION_SUCCESS, data));
    }


    @ApiOperation(value = "팔로잉 추가", notes = "userId : 팔로잉하는 유저 ID (상대방ID)")
    @PostMapping("/{userId}/following")
    public ResponseEntity<ResultResponse> postFollowing(@PathVariable("userId") String userId) throws FirebaseMessagingException {
        final PushNotification pushNotification= userService.postFollowing(userId);
        notificationService.sendNotification(NotificationType.FOLLOW, pushNotification);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_FOLLOWING_SUCCESS));
    }


    @ApiOperation(value = "팔로잉 취소", notes = "userId : 팔로잉하는 유저 ID (상대방ID)")
    @DeleteMapping("/{userId}/following")
    public ResponseEntity<ResultResponse> deleteFollowing(@PathVariable("userId") String userId) {
        final Long objectId= userService.deleteFollowing(userId);
        notificationService.deleteNotification(NotificationType.FOLLOW, objectId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_FOLLOWING_SUCCESS));
    }


    @ApiOperation(value = "팔로잉 조회")
    @GetMapping("/{userId}/followings")
    public ResponseEntity<FollowResponse> getFollowingList(@PathVariable("userId") String userId) {
        final List<FollowDto> data= userService.getFollowingList(userId);
        return ResponseEntity.ok(FollowResponse.of(ResultCode.GET_FOLLOWING_SUCCESS, data));
    }


    @ApiOperation(value = "팔로워 조회")
    @GetMapping("/{userId}/followers")
    public ResponseEntity<FollowResponse> getFollowerList(@PathVariable("userId") String userId) {
        final List<FollowDto> data= userService.getFollowerList(userId);
        return ResponseEntity.ok(FollowResponse.of(ResultCode.GET_FOLLOWER_SUCCESS, data));
    }


    @ApiOperation(value = "알림 허용 유무 조회")
    @GetMapping("/flag-notification")
    public ResponseEntity<NotificationFlagResponse> getFlagNotification() {
        final NotificationFlag data= userService.getFlagNotification();
        return ResponseEntity.ok(NotificationFlagResponse.of(ResultCode.GET_FLAG_NOTIFICATION_SUCCESS, data));
    }


    @ApiOperation(value = "알림 허용 유무 수정")
    @PutMapping("/flag-notification")
    public ResponseEntity<ResultResponse> putFlagNotification(@RequestBody NotificationFlag flag) {
        userService.putFlagNotification(flag);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PUT_FLAG_NOTIFICATION_SUCCESS));
    }
}
