package com.example.soonsul.liquor.service;

import com.example.soonsul.liquor.dto.EvaluationRequest;
import com.example.soonsul.liquor.entity.*;
import com.example.soonsul.liquor.exception.PersonalRatingNull;
import com.example.soonsul.liquor.repository.CommentRepository;
import com.example.soonsul.liquor.repository.ReviewRepository;
import com.example.soonsul.notification.NotificationRepository;
import com.example.soonsul.notification.entity.NotificationType;
import com.example.soonsul.response.error.ErrorCode;
import com.example.soonsul.user.entity.PersonalEvaluation;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.repository.PersonalEvaluationRepository;
import com.example.soonsul.util.LiquorUtil;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDate;
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
    private final PlatformTransactionManager transactionManager;
    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;

    private final List<FlavorType> flavorTypes= Arrays.asList(FlavorType.SWEETNESS, FlavorType.ACIDITY,
            FlavorType.CARBONIC_ACID, FlavorType.HEAVY, FlavorType.SCENT, FlavorType.DENSITY);


    public synchronized void postEvaluation(String liquorId, EvaluationRequest request){
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            final User user= userUtil.getUserByAuthentication();
            final Liquor liquor= liquorUtil.getLiquor(liquorId);
            final Evaluation evaluation= liquorUtil.getEvaluation(liquorId);
            final EvaluationNumber number= liquorUtil.getEvaluationNumber(liquorId);

            final PersonalEvaluation personalEvaluation= PersonalEvaluation.builder()
                    .evaluationDate(LocalDate.now())
                    .user(user)
                    .liquor(liquor)
                    .build();
            final PersonalEvaluation pe= personalEvaluationRepository.save(personalEvaluation);

            calAverageRating(MethodType.POST, liquor, number, request.getLiquorPersonalRating(), pe);

            for(FlavorType fType: flavorTypes){
                if(request.getFlavor(fType)!=null)
                    calAverageFlavor(fType, MethodType.POST, evaluation, number, request.getFlavor(fType), pe);
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

            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            throw e;
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
            calAverageRating(MethodType.PUT, liquor, number, request.getLiquorPersonalRating(), pe);
        }

        for(FlavorType fType: flavorTypes){
            if(checkByEqual(pe.getFlavor(fType),request.getFlavor(fType))){
                if(pe.getFlavor(fType)==null && request.getFlavor(fType)!=null) calAverageFlavor(fType, MethodType.POST, evaluation, number, request.getFlavor(fType), pe);
                else if(pe.getFlavor(fType)!=null && request.getFlavor(fType)==null) calAverageFlavor(fType, MethodType.DELETE, evaluation, number, null, pe);
                else if(pe.getFlavor(fType) != null) calAverageFlavor(fType, MethodType.PUT, evaluation, number, request.getFlavor(fType), pe);
            }
        }


        final Optional<Review> review= reviewRepository.findByUserAndLiquor(user, liquor);
        if(request.getReviewContent() == null && review.isPresent()){
            deleteReviewNotification(review.get());
            review.ifPresent(value -> reviewRepository.deleteById(value.getReviewId()));
        }
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


        calAverageRating(MethodType.DELETE, liquor, number, null, pe);

        for(FlavorType fType: flavorTypes){
            if(pe.getFlavor(fType)!=null)
                calAverageFlavor(fType, MethodType.DELETE, evaluation, number, null, pe);
        }


        personalEvaluationRepository.delete(pe);
        final Optional<Review> review= reviewRepository.findByUserAndLiquor(user, liquor);
        if(review.isPresent()){
            deleteReviewNotification(review.get());
            reviewRepository.deleteByUserAndLiquor(user, liquor);
        }
    }


    public void calAverageRating(MethodType mType, Liquor liquor, EvaluationNumber number, Double request, PersonalEvaluation pe){
        switch (mType){
            case POST:
                liquor.updateAverageRating(calAverage(CalculationType.ADD, liquor.getAverageRating(), number.getAverageRating(), request));
                number.addAverageRating(CalculationType.ADD);
                pe.updateLiquorPersonalRating(request);
                break;
            case DELETE:
                liquor.updateAverageRating(calAverage(CalculationType.SUB, liquor.getAverageRating(), number.getAverageRating(), pe.getLiquorPersonalRating()));
                number.addAverageRating(CalculationType.SUB);
                pe.updateLiquorPersonalRating(null);
                break;
            case PUT:
                liquor.updateAverageRating(calAverage(CalculationType.SUB, liquor.getAverageRating(), number.getAverageRating(), pe.getLiquorPersonalRating()));
                number.addAverageRating(CalculationType.SUB);
                liquor.updateAverageRating(calAverage(CalculationType.ADD, liquor.getAverageRating(), number.getAverageRating(), request));
                number.addAverageRating(CalculationType.ADD);
                pe.updateLiquorPersonalRating(request);
                break;
        }
    }


    public void calAverageFlavor(FlavorType fType, MethodType mType, Evaluation evaluation, EvaluationNumber number,
                      Integer request, PersonalEvaluation pe){

        switch (mType){
            case POST:
                evaluation.updateFlavor(fType, calAverage(fType, CalculationType.ADD, evaluation, number, request));
                number.updateFlavor(fType, CalculationType.ADD);
                pe.updateFlavor(fType, request);
                break;
            case DELETE:
                evaluation.updateFlavor(fType, calAverage(fType, CalculationType.SUB, evaluation, number, pe.getFlavor(fType)));
                number.updateFlavor(fType,CalculationType.SUB);
                pe.updateFlavor(fType, null);
                break;
            case PUT:
                evaluation.updateFlavor(fType, calAverage(fType, CalculationType.SUB, evaluation, number, pe.getFlavor(fType)));
                number.updateFlavor(fType,CalculationType.SUB);
                evaluation.updateFlavor(fType, calAverage(fType, CalculationType.ADD, evaluation, number, request));
                number.updateFlavor(fType,CalculationType.ADD);
                pe.updateFlavor(fType, request);
                break;
        }
    }



    private Double calAverage(CalculationType cType, Double origin, Integer number, Double request){
        int mark= 0;
        if(cType.equals(CalculationType.ADD)) mark= 1;
        else if(cType.equals(CalculationType.SUB)) mark= -1;

        if(number+mark==0) return 0.0;
        double result= ((origin*number)+(mark*request))/(number+mark);
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
        double result= ((origin*number)+(mark*request)) /(number+mark);
        return Math.round(result*10)/10.0;
    }


    private void deleteReviewNotification(Review review){
        for(ReviewGood reviewGood: review.getReviewGoods()){
            notificationRepository.deleteByTypeAndObjectId(NotificationType.REVIEW_GOOD, reviewGood.getReviewGoodId());
        }

        for(Comment comment: review.getCommentList()){
            final List<Comment> reCommentList= commentRepository.findAllByUpperCommentId(comment.getCommentId());
            for(Comment reComment: reCommentList){
                notificationRepository.deleteByTypeAndObjectId(NotificationType.RECOMMENT, reComment.getCommentId());
            }
            notificationRepository.deleteByTypeAndObjectId(NotificationType.COMMENT, comment.getCommentId());
        }
    }
}
