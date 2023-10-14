package com.example.soonsul.util;

import com.example.soonsul.liquor.entity.*;
import com.example.soonsul.liquor.exception.*;
import com.example.soonsul.liquor.repository.*;
import com.example.soonsul.response.error.ErrorCode;
import com.example.soonsul.user.entity.PersonalEvaluation;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.exception.PersonalEvaluationNotExist;
import com.example.soonsul.user.exception.UserNotExist;
import com.example.soonsul.user.repository.PersonalEvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LiquorUtil {
    private final LiquorRepository liquorRepository;
    private final PersonalEvaluationRepository personalEvaluationRepository;
    private final EvaluationRepository evaluationRepository;
    private final ReviewRepository reviewRepository;
    private final EvaluationNumberRepository evaluationNumberRepository;
    private final LocationInfoRepository locationInfoRepository;
    private final CommentRepository commentRepository;
    private final CodeRepository codeRepository;
    private final PrizeInfoRepository prizeInfoRepository;
    private final SalePlaceInfoRepository salePlaceInfoRepository;
    private final LocationRepository locationRepository;
    private final SalePlaceRepository salePlaceRepository;


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

    public LocationInfo getLocationInfo(Long locationInfoId){
        return locationInfoRepository.findById(locationInfoId)
                .orElseThrow(()-> new LocationInfoNotExist("location info not exist",ErrorCode.LOCATION_INFO_NOT_EXIST));
    }

    public PrizeInfo getPrizeInfo(Long prizeInfoId){
        return prizeInfoRepository.findById(prizeInfoId)
                .orElseThrow(()-> new PrizeInfoNotExist("prize info not exist",ErrorCode.PRIZE_INFO_NOT_EXIST));
    }

    public SalePlaceInfo getSalePlaceInfo(Long salePlaceInfoId){
        return salePlaceInfoRepository.findById(salePlaceInfoId)
                .orElseThrow(()-> new SalePlaceInfoNotExist("sale place info not exist",ErrorCode.SALE_PLACE_INFO_NOT_EXIST));
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

    public List<String> getBreweryList(String liquorId){
        final List<String> locationList = new ArrayList<>();
        final List<Location> locations = locationRepository.findAllById(liquorId);
        for (Location l : locations) {
            locationList.add(getLocationInfo(l.getLocationInfoId()).getBrewery());
        }
        return locationList;
    }

    public List<String> getSalePlaceList(String liquorId){
        final List<String> salePlaceList = new ArrayList<>();
        final Liquor liquor= liquorRepository.findById(liquorId)
                .orElseThrow(()-> new LiquorNotExist("liquor not exist", ErrorCode.LIQUOR_NOT_EXIST));
        final List<SalePlace> salePlaces = salePlaceRepository.findAllByLiquor(liquor);
        for (SalePlace s : salePlaces) {
            salePlaceList.add(getSalePlaceInfo(s.getSalePlaceInfoId()).getName());
        }
        return salePlaceList;
    }
}
