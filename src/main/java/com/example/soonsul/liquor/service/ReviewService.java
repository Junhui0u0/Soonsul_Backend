package com.example.soonsul.liquor.service;

import com.example.soonsul.liquor.dto.ReviewDto;
import com.example.soonsul.liquor.entity.Liquor;
import com.example.soonsul.liquor.entity.Review;
import com.example.soonsul.liquor.entity.ReviewGood;
import com.example.soonsul.liquor.repository.*;
import com.example.soonsul.notification.dto.PushNotification;
import com.example.soonsul.user.entity.PersonalEvaluation;
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

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final UserUtil userUtil;
    private final LiquorUtil liquorUtil;
    private final ReviewGoodRepository reviewGoodRepository;


    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewListByLatest(Pageable pageable, String liquorId){
        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final List<Review> reviews= reviewRepository.findAllByLatest(pageable, liquorId).toList();
        final Integer totalReviewNumber= reviewRepository.countByLiquor(liquor);

        final List<ReviewDto> result= new ArrayList<>();
        for(Review r: reviews){
            final PersonalEvaluation evaluation= liquorUtil.getPersonalEvaluation(r.getUser(), liquor);

            final ReviewDto reviewDto= ReviewDto.builder()
                    .reviewId(r.getReviewId())
                    .averageRating(evaluation.getLiquorPersonalRating())
                    .content(r.getContent())
                    .goodNumber(reviewGoodRepository.countByReview(r))
                    .createdDate(dateConversion(r.getCreatedDate()))
                    .commentNumber(commentRepository.countByReview(r))
                    .userId(r.getUser().getUserId())
                    .nickname(r.getUser().getNickname())
                    .profileImage(r.getUser().getProfileImage())
                    .reviewNumber(reviewRepository.countByUser(r.getUser()))
                    .flagGood(reviewGoodRepository.existsByReviewAndUser(r, user))
                    .totalReviewNumber(totalReviewNumber)
                    .build();
            result.add(reviewDto);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewListByRating(Pageable pageable, String liquorId){
        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final List<Review> reviews= reviewRepository.findAllByRating(pageable, liquorId).toList();
        final Integer totalReviewNumber= reviewRepository.countByLiquor(liquor);

        final List<ReviewDto> result= new ArrayList<>();
        for(Review r: reviews){
            final PersonalEvaluation evaluation= liquorUtil.getPersonalEvaluation(r.getUser(), liquor);

            final ReviewDto reviewDto= ReviewDto.builder()
                    .reviewId(r.getReviewId())
                    .averageRating(evaluation.getLiquorPersonalRating())
                    .content(r.getContent())
                    .goodNumber(reviewGoodRepository.countByReview(r))
                    .createdDate(dateConversion(r.getCreatedDate()))
                    .commentNumber(commentRepository.countByReview(r))
                    .userId(r.getUser().getUserId())
                    .nickname(r.getUser().getNickname())
                    .profileImage(r.getUser().getProfileImage())
                    .reviewNumber(reviewRepository.countByUser(r.getUser()))
                    .flagGood(reviewGoodRepository.existsByReviewAndUser(r, user))
                    .totalReviewNumber(totalReviewNumber)
                    .build();
            result.add(reviewDto);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public ReviewDto getReview(Long reviewId){
        final User user= userUtil.getUserByAuthentication();
        final Review review= liquorUtil.getReview(reviewId);
        final PersonalEvaluation evaluation= liquorUtil.getPersonalEvaluation(review.getUser(), review.getLiquor());

        return ReviewDto.builder()
                .reviewId(review.getReviewId())
                .averageRating(evaluation.getLiquorPersonalRating())
                .content(review.getContent())
                .goodNumber(reviewGoodRepository.countByReview(review))
                .createdDate(dateConversion(review.getCreatedDate()))
                .commentNumber(commentRepository.countByReview(review))
                .userId(review.getUser().getUserId())
                .nickname(review.getUser().getNickname())
                .profileImage(review.getUser().getProfileImage())
                .reviewNumber(reviewRepository.countByUser(review.getUser()))
                .flagGood(reviewGoodRepository.existsByReviewAndUser(review, user))
                .build();
    }


    @Transactional
    public PushNotification postReviewLike(Long reviewId){
        final User user= userUtil.getUserByAuthentication();
        final Review review= liquorUtil.getReview(reviewId);

        final ReviewGood good= ReviewGood.builder()
                .review(review)
                .user(user)
                .build();

        return PushNotification.builder()
                .objectId(reviewGoodRepository.save(good).getReviewGoodId())
                .receiveUser(review.getUser())
                .build();
    }


    @Transactional
    public Long deleteReviewLike(Long reviewId){
        final User user= userUtil.getUserByAuthentication();
        final Review review= liquorUtil.getReview(reviewId);
        final Long objectId= reviewGoodRepository.findByReviewAndUser(review, user).get().getReviewGoodId();

        reviewGoodRepository.deleteByReviewAndUser(review, user);
        return objectId;
    }


    @Transactional(readOnly = true)
    public Integer getReviewLike(Long reviewId){
        final Review review= liquorUtil.getReview(reviewId);
        return reviewGoodRepository.countByReview(review);
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
