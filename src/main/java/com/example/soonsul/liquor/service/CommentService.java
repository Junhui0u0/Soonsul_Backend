package com.example.soonsul.liquor.service;

import com.example.soonsul.liquor.dto.CommentDto;
import com.example.soonsul.liquor.dto.CommentRequest;
import com.example.soonsul.liquor.dto.ReCommentDto;
import com.example.soonsul.liquor.entity.Comment;
import com.example.soonsul.liquor.entity.CommentGood;
import com.example.soonsul.liquor.entity.Review;
import com.example.soonsul.liquor.repository.*;
import com.example.soonsul.notification.dto.PushNotification;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.util.LiquorUtil;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserUtil userUtil;
    private final LiquorUtil liquorUtil;
    private final CommentRepository commentRepository;
    private final CommentGoodRepository commentGoodRepository;


    @Transactional(readOnly = true)
    public List<CommentDto> getCommentList(Pageable pageable, Long reviewId){
        final User user= userUtil.getUserByAuthentication();
        final List<Comment> commentList= commentRepository.findAllByLatest(pageable, reviewId).toList();
        final List<CommentDto> result= new ArrayList<>();

        for(Comment c: commentList){
            final List<Comment> reComments= commentRepository.findAllByUpperComment(c.getCommentId());
            final List<ReCommentDto> reCommentList= new ArrayList<>();
            for(Comment rc: reComments){
                final ReCommentDto dto= ReCommentDto.builder()
                        .userId(rc.getUser().getUserId())
                        .nickname(rc.getUser().getNickname())
                        .profileImage(rc.getUser().getProfileImage())
                        .reCommentId(rc.getCommentId())
                        .upperCommentNickname(c.getUser().getNickname())
                        .content(rc.getContent())
                        .createdDate(dateConversion(rc.getCreatedDate()))
                        .good(commentGoodRepository.countByComment(rc))
                        .flagMySelf(Objects.equals(rc.getUser().getUserId(), user.getUserId()))
                        .flagGood(commentGoodRepository.existsByCommentAndUser(rc, user))
                        .build();
                reCommentList.add(dto);
            }

            final CommentDto commentDto= CommentDto.builder()
                    .userId(c.getUser().getUserId())
                    .nickname(c.getUser().getNickname())
                    .profileImage(c.getUser().getProfileImage())
                    .reviewId(reviewId)
                    .commentId(c.getCommentId())
                    .content(c.getContent())
                    .createdDate(dateConversion(c.getCreatedDate()))
                    .good(commentGoodRepository.countByComment(c))
                    .reCommentList(reCommentList)
                    .reCommentNumber(commentRepository.findAllByUpperCommentId(c.getCommentId()).size()-1)
                    .flagMySelf(Objects.equals(c.getUser().getUserId(), user.getUserId()))
                    .flagGood(commentGoodRepository.existsByCommentAndUser(c, user))
                    .build();
            result.add(commentDto);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public List<ReCommentDto> getReCommentList(Pageable pageable, Long commentId){
        final User user= userUtil.getUserByAuthentication();
        final Comment c= liquorUtil.getComment(commentId);

        final List<Comment> reCommentList= commentRepository.findByUpperComment(pageable, commentId).toList();
        final List<ReCommentDto> result= new ArrayList<>();

        for(Comment rc: reCommentList){
            final ReCommentDto dto= ReCommentDto.builder()
                    .userId(rc.getUser().getUserId())
                    .nickname(rc.getUser().getNickname())
                    .profileImage(rc.getUser().getProfileImage())
                    .reCommentId(rc.getCommentId())
                    .upperCommentNickname(c.getUser().getNickname())
                    .content(rc.getContent())
                    .createdDate(dateConversion(rc.getCreatedDate()))
                    .good(commentGoodRepository.countByComment(rc))
                    .flagMySelf(Objects.equals(rc.getUser().getUserId(), user.getUserId()))
                    .flagGood(commentGoodRepository.existsByCommentAndUser(rc, user))
                    .build();
            result.add(dto);
        }
        return result;
    }


    @Transactional
    public PushNotification postComment(Long reviewId, CommentRequest request){
        final User user= userUtil.getUserByAuthentication();
        final Review review= liquorUtil.getReview(reviewId);

        final Comment comment= Comment.builder()
                .content(request.getContent())
                .createdDate(LocalDateTime.now())
                .user(user)
                .review(review)
                .upperCommentId(null)
                .build();
        final Comment savedComment= commentRepository.save(comment);
        savedComment.updateUpperCommentId(savedComment.getCommentId());

        return PushNotification.builder()
                .objectId(savedComment.getCommentId())
                .receiveUser(review.getUser())
                .build();
    }


    @Transactional
    public void putComment(Long commentId, CommentRequest request){
        final Comment comment= liquorUtil.getComment(commentId);

        if(!comment.getContent().equals(request.getContent()))
            comment.updateContent(request.getContent());
    }


    @Transactional
    public void deleteComment(Long commentId){
        commentRepository.deleteAllByUpperCommentId(commentId);
        commentRepository.findById(commentId);
    }


    @Transactional
    public PushNotification postReComment(Long upperCommentId, CommentRequest request){
        final User user= userUtil.getUserByAuthentication();
        final Comment upperComment= liquorUtil.getComment(upperCommentId);

        final Comment comment= Comment.builder()
                .content(request.getContent())
                .createdDate(LocalDateTime.now())
                .user(user)
                .review(upperComment.getReview())
                .upperCommentId(upperComment.getCommentId())
                .build();

        return PushNotification.builder()
                .objectId(commentRepository.save(comment).getCommentId())
                .receiveUser(upperComment.getUser())
                .build();
    }


    @Transactional
    public void deleteReComment(Long commentId){
        commentRepository.deleteById(commentId);
    }


    @Transactional
    public void postCommentLike(Long commentId){
        final User user= userUtil.getUserByAuthentication();
        final Comment comment= liquorUtil.getComment(commentId);

        final CommentGood good= CommentGood.builder()
                .comment(comment)
                .user(user)
                .build();
        commentGoodRepository.save(good);
    }


    @Transactional
    public void deleteCommentLike(Long commentId){
        final User user= userUtil.getUserByAuthentication();
        final Comment comment= liquorUtil.getComment(commentId);

        commentGoodRepository.deleteByCommentAndUser(comment, user);
    }


    @Transactional(readOnly = true)
    public Integer getCommentLike(Long commentId){
        final Comment comment= liquorUtil.getComment(commentId);
        return commentGoodRepository.countByComment(comment);
    }



    private String dateConversion(LocalDateTime request){
        final LocalDateTime now= LocalDateTime.now();
        final long subSecond= ChronoUnit.SECONDS.between(request, now);

        if(subSecond<=60) return "지금";
        else if(subSecond<=3600){
            int min= Long.valueOf(subSecond).intValue()/60;
            return min+"분 전";
        }
        else if(subSecond<=86400){
            int min= Long.valueOf(subSecond).intValue()/3600;
            return min+"시간 전";
        }
        else if(subSecond<=604800){
            int min= Long.valueOf(subSecond).intValue()/86400;
            return min+"일 전";
        }
        else return request.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}
