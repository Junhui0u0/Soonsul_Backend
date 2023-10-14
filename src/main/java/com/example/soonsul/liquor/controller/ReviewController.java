package com.example.soonsul.liquor.controller;

import com.example.soonsul.liquor.dto.CommentDto;
import com.example.soonsul.liquor.dto.ReviewDto;
import com.example.soonsul.liquor.response.CommentListResponse;
import com.example.soonsul.liquor.response.ReviewListResponse;
import com.example.soonsul.liquor.response.ReviewResponse;
import com.example.soonsul.liquor.service.CommentService;
import com.example.soonsul.liquor.service.ReviewService;
import com.example.soonsul.notification.NotificationService;
import com.example.soonsul.notification.dto.PushNotification;
import com.example.soonsul.notification.entity.NotificationType;
import com.example.soonsul.response.result.ResultCode;
import com.example.soonsul.response.result.ResultResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags="Review")
@RestController
@RequiredArgsConstructor
@RequestMapping("/liquors")
public class ReviewController {
    private final ReviewService reviewService;
    private final CommentService commentService;
    private final NotificationService notificationService;


    @ApiOperation(value = "전통주 리뷰 전체 조회 - 최신순")
    @GetMapping("/{liquorId}/reviews/latest")
    public ResponseEntity<ReviewListResponse> getReviewListByLatest(@PageableDefault(size=10, sort = "review_id", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable("liquorId") String liquorId) {
        final List<ReviewDto> data= reviewService.getReviewListByLatest(pageable, liquorId);
        return ResponseEntity.ok(ReviewListResponse.of(ResultCode.GET_REVIEW_LIST_SUCCESS, data));
    }


    @ApiOperation(value = "전통주 리뷰 전체 조회 - 평점순")
    @GetMapping("/{liquorId}/reviews/rating")
    public ResponseEntity<ReviewListResponse> getReviewListByRating(@PageableDefault(size=10, sort = "review_id", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable("liquorId") String liquorId) {
        final List<ReviewDto> data= reviewService.getReviewListByRating(pageable, liquorId);
        return ResponseEntity.ok(ReviewListResponse.of(ResultCode.GET_REVIEW_LIST_SUCCESS, data));
    }


    @ApiOperation(value = "전통주 리뷰 한개 조회")
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable("reviewId") Long reviewId) {
        final ReviewDto data= reviewService.getReview(reviewId);
        return ResponseEntity.ok(ReviewResponse.of(ResultCode.GET_REVIEW_SUCCESS, data));
    }


    @ApiOperation(value = "해당하는 리뷰의 댓글 전체 조회 - 최신순")
    @GetMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<CommentListResponse> getCommentList(@PageableDefault(size=10, sort = "comment_id", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable("reviewId") Long reviewId) {
        final List<CommentDto> data= commentService.getCommentList(pageable, reviewId);
        return ResponseEntity.ok(CommentListResponse.of(ResultCode.GET_COMMENT_LIST_SUCCESS, data));
    }


    @ApiOperation(value = "리뷰 좋아요 추가")
    @PostMapping("/reviews/{reviewId}/like")
    public ResponseEntity<ResultResponse> postReviewLike(@PathVariable("reviewId") Long reviewId) throws FirebaseMessagingException {
        final PushNotification pushNotification= reviewService.postReviewLike(reviewId);
        notificationService.sendNotification(NotificationType.REVIEW_GOOD, pushNotification);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_REVIEW_LIKE_SUCCESS));
    }


    @ApiOperation(value = "리뷰 좋아요 삭제")
    @DeleteMapping("/reviews/{reviewId}/like")
    public ResponseEntity<ResultResponse> deleteReviewLike(@PathVariable("reviewId") Long reviewId) {
        final Long objectId= reviewService.deleteReviewLike(reviewId);
        notificationService.deleteNotification(NotificationType.REVIEW_GOOD, objectId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_REVIEW_LIKE_SUCCESS));
    }


    @ApiOperation(value = "해당 리뷰의 좋아요 개수 조회")
    @GetMapping("/reviews/{reviewId}/like")
    public ResponseEntity<ResultResponse> getReviewLike(@PathVariable("reviewId") Long reviewId) {
        final Integer data= reviewService.getReviewLike(reviewId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_REVIEW_LIKE_SUCCESS, data));
    }
}
