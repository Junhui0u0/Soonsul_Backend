package com.example.soonsul.liquor.controller;

import com.example.soonsul.liquor.dto.CommentDto;
import com.example.soonsul.liquor.dto.CommentRequest;
import com.example.soonsul.liquor.dto.ReCommentDto;
import com.example.soonsul.liquor.response.ReCommentListResponse;
import com.example.soonsul.liquor.service.CommentService;
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

@Api(tags="Comment")
@RestController
@RequiredArgsConstructor
@RequestMapping("/liquors")
public class CommentController {
    private final CommentService commentService;
    private final NotificationService notificationService;


    @ApiOperation(value = "댓글 작성")
    @PostMapping("/reviews/{reviewId}/comment")
    public ResponseEntity<ResultResponse> postComment(@PathVariable("reviewId") Long reviewId, @RequestBody CommentRequest request) throws FirebaseMessagingException {
        final PushNotification pushNotification= commentService.postComment(reviewId, request);
        notificationService.sendNotification(NotificationType.COMMENT, pushNotification);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_COMMENT_SUCCESS));
    }


    @ApiOperation(value = "댓글 수정")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ResultResponse> putComment(@PathVariable("commentId") Long commentId, @RequestBody CommentRequest request) {
        commentService.putComment(commentId, request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PUT_COMMENT_SUCCESS));
    }


    @ApiOperation(value = "댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ResultResponse> deleteComment(@PathVariable("commentId") Long commentId) {
        notificationService.deleteNotification(NotificationType.COMMENT, commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_COMMENT_SUCCESS));
    }


    @ApiOperation(value = "대댓글 작성")
    @PostMapping("/re-comments/{upperCommentId}")
    public ResponseEntity<ResultResponse> postReComment(@PathVariable("upperCommentId") Long upperCommentId, @RequestBody CommentRequest request) throws FirebaseMessagingException {
        final PushNotification pushNotification= commentService.postReComment(upperCommentId, request);
        notificationService.sendNotification(NotificationType.RECOMMENT, pushNotification);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_RECOMMENT_SUCCESS));
    }


    @ApiOperation(value = "대댓글 수정")
    @PutMapping("/re-comments/{commentId}")
    public ResponseEntity<ResultResponse> putReComment(@PathVariable("commentId") Long commentId, @RequestBody CommentRequest request) {
        commentService.putComment(commentId, request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PUT_RECOMMENT_SUCCESS));
    }


    @ApiOperation(value = "대댓글 삭제")
    @DeleteMapping("/re-comments/{commentId}")
    public ResponseEntity<ResultResponse> deleteReComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteReComment(commentId);
        notificationService.deleteNotification(NotificationType.RECOMMENT, commentId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_RECOMMENT_SUCCESS));
    }


    @ApiOperation(value = "댓글 좋아요 추가")
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<ResultResponse> postCommentLike(@PathVariable("commentId") Long commentId) {
        commentService.postCommentLike(commentId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.POST_COMMENT_LIKE_SUCCESS));
    }


    @ApiOperation(value = "댓글 좋아요 삭제")
    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<ResultResponse> deleteCommentLike(@PathVariable("commentId") Long commentId) {
        commentService.deleteCommentLike(commentId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_COMMENT_LIKE_SUCCESS));
    }


    @ApiOperation(value = "해당 댓글의 좋아요 개수 조회")
    @GetMapping("/comments/{commentId}/like")
    public ResponseEntity<ResultResponse> getCommentLike(@PathVariable("commentId") Long commentId) {
        final Integer data= commentService.getCommentLike(commentId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_COMMENT_LIKE_SUCCESS, data));
    }


    @ApiOperation(value = "대댓글 전체 조회 - 최신순")
    @GetMapping("/comments/{commentId}/re-comments")
    public ResponseEntity<ReCommentListResponse> getReCommentList(@PageableDefault(size=10, sort = "comment_id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                  @PathVariable("commentId") Long commentId) {
        final List<ReCommentDto> data= commentService.getReCommentList(pageable, commentId);
        return ResponseEntity.ok(ReCommentListResponse.of(ResultCode.GET_RECOMMENT_LIST_SUCCESS, data));
    }

}
