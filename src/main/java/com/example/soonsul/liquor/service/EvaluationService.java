package com.example.soonsul.liquor.service;

import com.example.soonsul.liquor.dto.EvaluationRequest;
import com.example.soonsul.liquor.entity.*;
import com.example.soonsul.liquor.exception.PersonalRatingNull;
import com.example.soonsul.liquor.repository.ReviewRepository;
import com.example.soonsul.response.error.ErrorCode;
import com.example.soonsul.user.entity.PersonalEvaluation;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.repository.PersonalEvaluationRepository;
import com.example.soonsul.util.LiquorUtil;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final PersonalEvaluationRepository personalEvaluationRepository;
    private final UserUtil userUtil;
    private final ReviewRepository reviewRepository;
    private final LiquorUtil liquorUtil;

    private final List<FlavorType> flavorTypes= Arrays.asList(FlavorType.SWEETNESS, FlavorType.ACIDITY,
            FlavorType.CARBONIC_ACID, FlavorType.HEAVY, FlavorType.SCENT, FlavorType.DENSITY);


    @Transactional
    public void postEvaluation(String liquorId, EvaluationRequest request){
        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final Evaluation evaluation= liquorUtil.getEvaluation(liquorId);
        final EvaluationNumber number= liquorUtil.getEvaluationNumber(liquorId);

        final PersonalEvaluation personalEvaluation= PersonalEvaluation.builder()
                .user(user)
                .liquor(liquor)
                .build();
        final PersonalEvaluation pe= personalEvaluationRepository.save(personalEvaluation);


        calAverageRating(CalculationType.ADD, liquor, number, request.getLiquorPersonalRating(), pe);

        for(FlavorType fType: flavorTypes){
            if(request.getFlavor(fType)!=null)
                calAverageFlavor(fType, CalculationType.ADD, evaluation, number, request.getFlavor(fType), pe);
        }


        if(request.getReviewContent()!=null){
            final Review review= Review.builder()
                    .content(request.getReviewContent())
                    .createdDate(LocalDateTime.now())
                    .liquorRating(request.getLiquorPersonalRating())
                    .user(user)
                    .liquor(liquor)
                    .build();
            reviewRepository.save(review);
        }
    }


    @Transactional
    public void putEvaluation(String liquorId, EvaluationRequest request){
        if(request.getLiquorPersonalRating()==null) throw new PersonalRatingNull("personal rating is null", ErrorCode.PERSONAL_RATING_NULL);

        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final PersonalEvaluation pe= liquorUtil.getPersonalEvaluation(user, liquor);
        final Evaluation evaluation= liquorUtil.getEvaluation(liquorId);
        final EvaluationNumber number= liquorUtil.getEvaluationNumber(liquorId);


        if(!pe.getLiquorPersonalRating().equals(request.getLiquorPersonalRating())){
            calAverageRating(CalculationType.SUB_AND_ADD, liquor, number, request.getLiquorPersonalRating(), pe);
        }

        for(FlavorType fType: flavorTypes){
            if(checkByEqual(pe.getFlavor(fType),request.getFlavor(fType))){
                if(pe.getFlavor(fType)==null && request.getFlavor(fType)!=null) calAverageFlavor(fType, CalculationType.ADD, evaluation, number, request.getFlavor(fType), pe);
                else if(pe.getFlavor(fType)!=null && request.getFlavor(fType)==null) calAverageFlavor(fType, CalculationType.SUB, evaluation, number, null, pe);
                else if(pe.getFlavor(fType) != null) calAverageFlavor(fType, CalculationType.SUB_AND_ADD, evaluation, number, request.getFlavor(fType), pe);
            }
        }


        final Optional<Review> review= reviewRepository.findByUserAndLiquor(user, liquor);
        if(request.getReviewContent() == null && review.isPresent())
            review.ifPresent(value -> reviewRepository.deleteById(value.getReviewId()));
        else if(request.getReviewContent()!= null && review.isPresent()){
            if(!request.getLiquorPersonalRating().equals(review.get().getLiquorRating()))
                review.get().updateLiquorRating(request.getLiquorPersonalRating());
            review.get().updateContent(request.getReviewContent());
        }
        else if(request.getReviewContent() != null){
            final Review newReview= Review.builder()
                    .content(request.getReviewContent())
                    .createdDate(LocalDateTime.now())
                    .liquorRating(request.getLiquorPersonalRating())
                    .user(user)
                    .liquor(liquor)
                    .build();
            reviewRepository.save(newReview);
        }
    }

    private boolean checkByEqual(Integer origin, Integer request){
        if(origin==null) return true;
        return !origin.equals(request);
    }


    @Transactional
    public void deletePersonalEvaluation(String liquorId){
        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final Evaluation evaluation= liquorUtil.getEvaluation(liquorId);
        final EvaluationNumber number= liquorUtil.getEvaluationNumber(liquorId);
        final PersonalEvaluation pe= liquorUtil.getPersonalEvaluation(user, liquor);


        calAverageRating(CalculationType.SUB, liquor, number, null, pe);

        for(FlavorType fType: flavorTypes){
            if(pe.getFlavor(fType)!=null)
                calAverageFlavor(fType, CalculationType.SUB, evaluation, number, null, pe);
        }


        personalEvaluationRepository.delete(pe);
        if(reviewRepository.findByUserAndLiquor(user, liquor).isPresent())
            reviewRepository.deleteByUserAndLiquor(user, liquor);
    }


    public void calAverageRating(CalculationType cType, Liquor liquor, EvaluationNumber number, Double request, PersonalEvaluation pe){
        if(cType.equals(CalculationType.ADD)){
            liquor.updateAverageRating(calAverage(liquor.getAverageRating(), number.getAverageRating(), request, 1));
            number.addAverageRating(1);
            pe.updateLiquorPersonalRating(request);
        }
        else if(cType.equals(CalculationType.SUB)){
            liquor.updateAverageRating(calAverage(liquor.getAverageRating(), number.getAverageRating(), pe.getLiquorPersonalRating(), -1));
            number.addAverageRating(-1);
            pe.updateLiquorPersonalRating(null);
        }
        else if(cType.equals(CalculationType.SUB_AND_ADD)){
            liquor.updateAverageRating(calAverage(liquor.getAverageRating(), number.getAverageRating(), pe.getLiquorPersonalRating(), -1));
            number.addAverageRating(-1);
            liquor.updateAverageRating(calAverage(liquor.getAverageRating(), number.getAverageRating(), request, 1));
            number.addAverageRating(1);
            pe.updateLiquorPersonalRating(request);
        }
    }


    public void calAverageFlavor(FlavorType fType, CalculationType cType, Evaluation evaluation, EvaluationNumber number,
                      Integer request, PersonalEvaluation pe){

        if(cType.equals(CalculationType.ADD)){
            evaluation.updateFlavor(fType, calAverage(fType, cType, evaluation, number, request));
            number.updateFlavor(fType, cType);
            pe.updateFlavor(fType, request);
        }
        else if(cType.equals(CalculationType.SUB)){
            evaluation.updateFlavor(fType, calAverage(fType, cType, evaluation, number, pe.getFlavor(fType)));
            number.updateFlavor(fType,cType);
            pe.updateFlavor(fType, null);
        }
        else if(cType.equals(CalculationType.SUB_AND_ADD)){
            evaluation.updateFlavor(fType, calAverage(fType, CalculationType.SUB, evaluation, number, pe.getFlavor(fType)));
            number.updateFlavor(fType,CalculationType.SUB);
            evaluation.updateFlavor(fType, calAverage(fType, CalculationType.ADD, evaluation, number, request));
            number.updateFlavor(fType,CalculationType.ADD);
            pe.updateFlavor(fType, request);
        }
    }



    private Double calAverage(Double origin, Integer number, Double rating, Integer mark){
        double result= ((origin*number)+(mark*rating))/(number+mark);
        return Math.round(result*10)/10.0;
    }

    private Double calAverage(FlavorType fType, CalculationType cType, Evaluation evaluation,
                              EvaluationNumber evaluationNumber, Integer request){
        int mark= 0;
        if(cType.equals(CalculationType.ADD)) mark= 1;
        else if(cType.equals(CalculationType.SUB)) mark= -1;

        double origin= evaluation.getFlavor(fType);
        int number= evaluationNumber.getFlavor(fType);

        if(number+mark==0) return 0.0;
        double result= (double) ((origin*number)+(mark*request))/(number+mark);
        return Math.round(result*10)/10.0;
    }
}
