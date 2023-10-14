package com.example.soonsul.response.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    // user
    NEW_USER_LOGIN_SUCCESS(200, "A001", "신규 유저 로그인 완료했습니다."),
    ORIGINAL_USER_LOGIN_SUCCESS(200, "A002", "기존 유저 로그인 완료했습니다."),
    SIGNUP_SUCCESS(201,"A003","회원가입 완료했습니다."),
    GENERATE_ACCESS_TOKEN_SUCCESS(200,"A004","새로운 Access Token을 발급했습니다."),
    TOKEN_VALID_CHECK_SUCCESS(200,"A005","Access Token 유효성 검사 완료했습니다."),
    DELETE_USER_SUCCESS(200,"A006","회원탈퇴 완료했습니다."),
    POST_FOLLOWING_SUCCESS(201,"A007","팔로잉을 추가했습니다."),
    DELETE_FOLLOWING_SUCCESS(200,"A008","팔로잉을 삭제했습니다."),
    GET_FOLLOWING_SUCCESS(200,"A009","팔로잉 리스트를 조회했습니다."),
    GET_FOLLOWER_SUCCESS(200,"A010","팔로워 리스트를 조회했습니다."),
    POST_DEVICE_TOKEN_SUCCESS(200,"A011","디바이스 토큰을 등록했습니다."),

    //scan
    GET_SCANNED_LIQUOR_NAME_SUCCESS(200,"SC01","스캔한 제품의 주류명을 조회했습니다."),
    POST_SCAN_SUCCESS(201,"SC02","스캔한 제품을 사진 히스토리에 저장했습니다."),
    DELETE_SCAN_SUCCESS(200,"SC03","해당 히스토리를 삭제했습니다."),

    //liquor
    GET_LIQUOR_INFO_SUCCESS(200,"L001","전통주 정보를 조회했습니다."),
    GET_EVALUATION_CHECK_SUCCESS(200,"L002","전통주 평가 여부를 조회했습니다."),
    GET_FLAVOR_AVERAGE_SUCCESS(200,"L003","전통주 전체 맛평가를 조회했습니다."),
    GET_FLAVOR_PERSON_SUCCESS(200,"L004","전통주 개인 맛평가를 조회했습니다."),
    GET_FLAVOR_CHECK_SUCCESS(200,"L005","전통주 맛평가 여부를 조회했습니다."),
    GET_REVIEW_LIST_SUCCESS(200,"L006","전통주 전체 리뷰를 조회했습니다."),
    GET_REVIEW_SUCCESS(200,"L007","전통주 리뷰 한개를 조회했습니다."),
    GET_COMMENT_LIST_SUCCESS(200,"L008","해당하는 리뷰의 전체 댓글을 조회했습니다."),
    POST_EVALUATION_SUCCESS(201,"L009","전통주에 대한 평가를 등록했습니다."),
    PUT_EVALUATION_SUCCESS(200,"L010","전통주에 대한 평가를 수정했습니다."),
    POST_COMMENT_SUCCESS(201,"L011","댓글을 등록했습니다."),
    PUT_COMMENT_SUCCESS(200,"L012","댓글을 수정했습니다."),
    DELETE_COMMENT_SUCCESS(200,"L013","댓글을 삭제했습니다."),
    POST_RECOMMENT_SUCCESS(201,"L014","대댓글을 등록했습니다."),
    PUT_RECOMMENT_SUCCESS(200,"L015","대댓글을 수정했습니다."),
    DELETE_RECOMMENT_SUCCESS(200,"L016","대댓글을 삭제했습니다."),
    POST_REVIEW_LIKE_SUCCESS(200,"L017","해당 리뷰의 좋아요를 추가했습니다."),
    DELETE_REVIEW_LIKE_SUCCESS(200,"L018","해당 리뷰의 좋아요를 삭제했습니다."),
    GET_REVIEW_LIKE_SUCCESS(200,"L019","해당 리뷰의 좋아요를 조회했습니다."),
    POST_COMMENT_LIKE_SUCCESS(200,"L020","해당 댓글의 좋아요를 추가했습니다."),
    DELETE_COMMENT_LIKE_SUCCESS(200,"L021","해당 댓글의 좋아요를 삭제했습니다."),
    GET_COMMENT_LIKE_SUCCESS(200,"L022","해당 댓글의 좋아요를 조회했습니다."),
    GET_WEEK_LIQUOR_SUCCESS(200,"L023","이번주 가장 사랑받는 전통주를 조회했습니다."),
    GET_LIQUOR_PRIZE_SUCCESS(200,"L024","전통주 수상내역 정보를 조회했습니다."),
    GET_LIQUOR_LOCATION_SUCCESS(200,"L025","전통주 소재지 정보를 조회했습니다."),
    GET_LIQUOR_SALE_PLACE_SUCCESS(200,"L026","전통주 판매처 정보를 조회했습니다."),
    GET_LIQUOR_LIST_NAME_SUCCESS(200,"L027","모든 전통주 이름을 조회했습니다"),
    UPDATE_LIQUOR_FILTERING_SUCCESS(200,"L028","전통주 필터링을 업데이트했습니다."),
    GET_PERSONAL_RATING_SUCCESS(200,"L029","전통주 개인 평점을 조회했습니다."),
    POST_SCRAP_SUCCESS(201,"L030","스크랩을 등록했습니다."),
    DELETE_SCRAP_SUCCESS(200,"L031","스크랩을 삭제했습니다."),
    GET_RECOMMENT_LIST_SUCCESS(200,"L032","대댓글을 모두 조회했습니다."),
    GET_EVALUATION_SUCCESS(200,"L033","유저가 남긴 평가 내용을 모두 조회했습니다."),
    GET_LIQUOR_LIST_ID_SUCCESS(200,"L034","모든 전통주 ID를 조회했습니다"),

    //user
    PUT_PROFILE_SUCCESS(200,"U001","유저 프로필을 수정했습니다."),
    GET_NICKNAME_CHECK_SUCCESS(200,"U002","유저 닉네임 사용유무를 조회했습니다."),
    GET_USER_ZZIM_SUCCESS(200,"U003","유저 찜 리스트를 조회했습니다."),
    GET_USER_HISTORY_SUCCESS(200,"U004","사진 히스토리를 조회했습니다."),
    GET_USER_SCRAP_SUCCESS(200,"U005","유저가 스크랩한 전통주 리스트를 조회했습니다."),
    GET_USER_EVALUATION_SUCCESS(200,"U006","유저가 남긴 평가리스트를 조회했습니다."),
    DELETE_USER_EVALUATION_SUCCESS(200,"U007","유저가 남긴 평가를 삭제했습니다."),
    PUT_PROFILE_NICKNAME_SUCCESS(200,"U008","유저 닉네임을 수정했습니다."),
    PUT_PROFILE_IMAGE_SUCCESS(200,"U009","유저 프로필 이미지를 수정했습니다."),
    GET_USER_PROFILE_SUCCESS(200, "U010", "유저 프로필 정보를 조회했습니다."),
    POST_INQUIRY_SUCCESS(201, "U011", "문의글을 작성했습니다."),
    GET_FLAG_NOTIFICATION_SUCCESS(200, "U012", "알림 허용 유무를 조회했습니다."),
    PUT_FLAG_NOTIFICATION_SUCCESS(200, "U013", "알림 허용 유무를 수정했습니다."),

    //click
    POST_CLICK_SUCCESS(201,"C001","전통주 클릭을 등록했습니다."),

    //search
    GET_SEARCH_SUCCESS(200,"SE01","해당 전통주를 검색했습니다."),

    //promotion
    GET_PROMOTION_LIST_SUCCESS(200,"P001","모든 프로모션을 조회했습니다."),
    GET_PROMOTION_SUCCESS(200,"P002","해당 프로모션을 조회했습니다."),
    POST_ZZIM_SUCCESS(201,"P003","찜을 등록했습니다."),
    DELETE_ZZIM_SUCCESS(200,"P004","해당 찜을 삭제했습니다."),
    GET_MAIN_BANNER_LIST_SUCCESS(200,"P005","메인 배너 리스트를 조회했습니다."),
    GET_MAIN_BANNER_SUCCESS(200,"P006","해당 메인 배너를 조회했습니다."),
    POST_BANNER_ZZIM_SUCCESS(201,"P007","메인 배너 찜을 등록했습니다."),
    DELETE_BANNER_ZZIM_SUCCESS(200,"P008","메인 배너 찜을 삭제했습니다."),
    
    //notification
    GET_NOTIFICATION_LIST_SUCCESS(200,"N001","모든 알림 내역을 조회했습니다."),
    GET_NEW_NOTIFICATION_SUCCESS(200,"N002","새로운 알림 유무를 확인했습니다."),
    PUT_NEW_NOTIFICATION_SUCCESS(200,"N003","알림 상태를 읽음으로 수정했습니다."),

    //manager
    POST_MAIN_PHOTO(200,"M001","S3에 모든 전통주 메인사진을 등록했습니다."),
    POST_LIQUOR_INIT_SUCCESS(201,"M002","모든 전통주 평가, 평가수를 등록했습니다."),
    POST_LOCATION_INIT_SUCCESS(200,"M003","모든 소재지의 위도, 경도값을 등록했습니다."),
    MANAGE_ACTION_SUCCESS(200,"M004","성공했습니다.")
    ;


    private final int status;
    private final String code;
    private final String message;
}