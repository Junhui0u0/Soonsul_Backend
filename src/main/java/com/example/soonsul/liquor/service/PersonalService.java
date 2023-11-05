package com.example.soonsul.liquor.service;

import com.example.soonsul.liquor.dto.PersonalDto;
import com.example.soonsul.liquor.entity.Liquor;
import com.example.soonsul.liquor.entity.Review;
import com.example.soonsul.liquor.repository.*;
import com.example.soonsul.user.entity.PersonalEvaluation;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.repository.PersonalEvaluationRepository;
import com.example.soonsul.util.LiquorUtil;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonalService {
    private final PersonalEvaluationRepository personalEvaluationRepository;
    private final UserUtil userUtil;
    private final LiquorUtil liquorUtil;
    private final ReviewRepository reviewRepository;
    private final ReviewGoodRepository reviewGoodRepository;
    private final CommentRepository commentRepository;


    @Transactional(readOnly = true)
    public List<PersonalDto> getPersonalEvaluationList(String userId, Pageable pageable){
        User user;
        if(userId==null) user= userUtil.getUserByAuthentication();
        else user= userUtil.getUserById(userId);

        final List<PersonalEvaluation> list= personalEvaluationRepository.findAll(pageable, user.getUserId()).toList();
        final Integer totalReviewNumber= personalEvaluationRepository.countByUser(user);

        final List<PersonalDto> result= new ArrayList<>();
        for(PersonalEvaluation p: list){
            final Liquor liquor= p.getLiquor();
            final String liquorCategory= liquorUtil.getCodeName(liquor.getLiquorCategory());
            final Optional<Review> review= reviewRepository.findByUserAndLiquor(user, liquor);

            final PersonalDto dto= PersonalDto.builder()
                    .liquorId(liquor.getLiquorId())
                    .name(liquor.getName())
                    .averageRating(liquor.getAverageRating())
                    .imageUrl(liquor.getImageUrl())
                    .liquorCategory(liquorCategory)
                    .brewery(liquor.getBrewery())
                    .evaluationDate(p.getEvaluationDate())
                    .personalEvaluationId(p.getPersonalEvaluationId())
                    .personalRating(p.getLiquorPersonalRating())
                    .reviewId(review.map(Review::getReviewId).orElse(null))
                    .reviewContent(review.map(Review::getContent).orElse(null))
                    .sweetness((userId==null) ? p.getSweetness() : null)
                    .acidity((userId==null) ? p.getAcidity() : null)
                    .carbonicAcid((userId==null) ? p.getCarbonicAcid() : null)
                    .heavy((userId==null) ? p.getHeavy() : null)
                    .scent((userId==null) ? p.getScent() : null)
                    .density((userId==null) ? p.getDensity() : null)
                    .totalReviewNumber(totalReviewNumber)
                    .goodNumber(review.map(reviewGoodRepository::countByReview).orElse(0))
                    .commentNumber(review.map(commentRepository::countByReview).orElse(0))
                    .salePlace(liquor.getSalePlace())
                    .flagGood(review.isPresent() && reviewGoodRepository.existsByReviewAndUser(review.get(), userUtil.getUserByAuthentication()))
                    .build();
            result.add(dto);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public PersonalDto getEvaluation(String liquorId){
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final User user= userUtil.getUserByAuthentication();
        final PersonalEvaluation personalEvaluation= liquorUtil.getPersonalEvaluation(user, liquor);
        final Optional<Review> review= reviewRepository.findByUserAndLiquor(user, liquor);

        return PersonalDto.builder()
                .personalRating(personalEvaluation.getLiquorPersonalRating())
                .sweetness(personalEvaluation.getSweetness())
                .acidity(personalEvaluation.getAcidity())
                .carbonicAcid(personalEvaluation.getCarbonicAcid())
                .heavy(personalEvaluation.getHeavy())
                .scent(personalEvaluation.getScent())
                .density(personalEvaluation.getDensity())
                .reviewContent(review.map(Review::getContent).orElse(null))
                .build();
    }

}
