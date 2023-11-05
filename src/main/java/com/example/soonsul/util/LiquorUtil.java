package com.example.soonsul.util;

import com.example.soonsul.liquor.entity.*;
import com.example.soonsul.liquor.exception.*;
import com.example.soonsul.liquor.repository.*;
import com.example.soonsul.response.error.ErrorCode;
import com.example.soonsul.user.entity.PersonalEvaluation;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.exception.PersonalEvaluationNotExist;
import com.example.soonsul.user.repository.PersonalEvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class LiquorUtil {
    private final LiquorRepository liquorRepository;
    private final PersonalEvaluationRepository personalEvaluationRepository;
    private final EvaluationRepository evaluationRepository;
    private final ReviewRepository reviewRepository;
    private final EvaluationNumberRepository evaluationNumberRepository;
    private final CommentRepository commentRepository;
    private final CodeRepository codeRepository;


    public Liquor getLiquor(String liquorId){
        return liquorRepository.findById(liquorId)
                .orElseThrow(()-> new LiquorNotExist("liquor not exist", ErrorCode.LIQUOR_NOT_EXIST));
    }

    public Evaluation getEvaluation(String liquorId){
        return evaluationRepository.findById(liquorId)
                .orElseThrow(()-> new EvaluationNotExist("evaluation not exist", ErrorCode.EVALUATION_NOT_EXIST));
    }

    public EvaluationNumber getEvaluationNumber(String liquorId){
        return evaluationNumberRepository.findById(liquorId)
                .orElseThrow(()-> new LiquorNotExist("evaluation number not exist", ErrorCode.LIQUOR_NOT_EXIST));
    }

    public PersonalEvaluation getPersonalEvaluation(User user, Liquor liquor){
        return personalEvaluationRepository.findByUserAndLiquor(user, liquor)
                .orElseThrow(()-> new PersonalEvaluationNotExist("liquor evaluation not exist", ErrorCode.PERSONAL_EVALUATION_NOT_EXIST));
    }

    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotExist("review not exist", ErrorCode.REVIEW_NOT_EXIST));
    }

    public Comment getComment(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(()-> new CommentNotExist("comment not exist", ErrorCode.COMMENT_NOT_EXIST));
    }

    public String getCodeName(String codeId){
        return codeRepository.findById(codeId)
                .orElseThrow(()->new CodeNotExist("code not exist", ErrorCode.CODE_NOT_EXIST)).getCodeName();
    }

    public String getCodeId(String codeName){
        return codeRepository.findByCodeName(codeName)
                .orElseThrow(()->new CodeNotExist("code not exist", ErrorCode.CODE_NOT_EXIST)).getCodeId();
    }

}
