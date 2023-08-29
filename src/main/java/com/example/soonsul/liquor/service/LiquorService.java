package com.example.soonsul.liquor.service;

import com.example.soonsul.cache.CacheKey;
import com.example.soonsul.liquor.dto.*;
import com.example.soonsul.liquor.entity.*;
import com.example.soonsul.liquor.repository.*;
import com.example.soonsul.main.entity.Sorting;
import com.example.soonsul.user.entity.PersonalEvaluation;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.repository.PersonalEvaluationRepository;
import com.example.soonsul.util.LiquorUtil;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiquorService {
    private final LiquorRepository liquorRepository;
    private final PersonalEvaluationRepository personalEvaluationRepository;
    private final UserUtil userUtil;
    private final LiquorUtil liquorUtil;
    private final PrizeRepository prizeRepository;
    private final LocationRepository locationRepository;
    private final SalePlaceRepository salePlaceRepository;
    private final ReviewRepository reviewRepository;
    private final LiquorFilteringRepository filteringRepository;
    private final FilteringClickRepository filteringClickRepository;
    private final ScrapRepository scrapRepository;


    @Transactional(readOnly = true)
    @Cacheable(value = CacheKey.LIQUOR, cacheManager = "cacheManager")
    public LiquorInfoDto getLiquorInfo(String liquorId){
        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final Optional<PersonalEvaluation> personalEvaluation= personalEvaluationRepository.findByUserAndLiquor(user,liquor);

        Double liquorPersonalRating= null;
        if(personalEvaluation.isPresent()){
            liquorPersonalRating= personalEvaluation.get().getLiquorPersonalRating();
        }

        final String region= liquorUtil.getCodeName(liquor.getRegion());
        final String liquorCategory= liquorUtil.getCodeName(liquor.getLiquorCategory());

        final List<LiquorFilteringDto> filtering= new ArrayList<>();
        final List<LiquorFiltering> filteringList= filteringRepository.findAllByLiquorId(liquorId);
        for(LiquorFiltering l: filteringList){
            final LiquorFilteringDto dto= LiquorFilteringDto.builder()
                    .age(l.getAge())
                    .gender(l.getGender())
                    .build();
            filtering.add(dto);
        }

        return LiquorInfoDto.builder()
                .name(liquor.getName())
                .ingredient(liquor.getIngredient())
                .averageRating(liquor.getAverageRating())
                .lowestPrice(liquor.getLowestPrice())
                .alcohol(liquor.getAlcohol())
                .capacity(liquor.getCapacity())
                .region(region)
                .imageUrl(liquor.getImageUrl())
                .liquorCategory(liquorCategory)
                .liquorPersonalRating(liquorPersonalRating)
                .ratingNumber(reviewRepository.countByLiquor(liquor))
                .filtering(filtering)
                .flagScrap(scrapRepository.existsByUserAndLiquor(user, liquor))
                .build();
    }


    @Transactional(readOnly = true)
    public List<PrizeListDto> getLiquorPrize(String liquorId){
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final List<Prize> prizeList= prizeRepository.findAllByLiquor(liquor);

        final List<PrizeListDto> result= new ArrayList<>();
        for(Prize p: prizeList){
            final PrizeInfo info= liquorUtil.getPrizeInfo(p.getPrizeInfoId());
            final PrizeListDto dto = PrizeListDto.builder()
                    .prizeId(p.getPrizeId())
                    .name(info.getName())
                    .build();
            result.add(dto);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public List<LocationListDto> getLiquorLocation(String liquorId){
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final List<Location> locationList= locationRepository.findAllByLiquor(liquor);

        final List<LocationListDto> result= new ArrayList<>();
        for(Location l: locationList){
            final LocationInfo info= liquorUtil.getLocationInfo(l.getLocationInfoId());
            final LocationListDto dto = LocationListDto.builder()
                    .locationInfoId(l.getLocationId())
                    .name(info.getName())
                    .latitude(info.getLatitude())
                    .longitude(info.getLongitude())
                    .brewery(info.getBrewery())
                    .build();
            result.add(dto);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public List<SalePlaceListDto> getLiquorSalePlace(String liquorId){
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final List<SalePlace> salePlaceList= salePlaceRepository.findAllByLiquor(liquor);

        final List<SalePlaceListDto> result= new ArrayList<>();
        for(SalePlace s: salePlaceList){
            final SalePlaceInfo info= liquorUtil.getSalePlaceInfo(s.getSalePlaceInfoId());
            final SalePlaceListDto dto = SalePlaceListDto.builder()
                    .salePlaceId(s.getSalePlaceId())
                    .name(info.getName())
                    .phoneNumber(info.getPhoneNumber())
                    .siteUrl(info.getSiteUrl())
                    .build();
            result.add(dto);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public boolean getEvaluationCheck(String liquorId){
        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final Optional<PersonalEvaluation> evaluation= personalEvaluationRepository.findByUserAndLiquor(user, liquor);

        return evaluation.isPresent();
    }


    @Transactional(readOnly = true)
    public EvaluationDto getFlavorAverage(String liquorId){
        final Evaluation evaluation= liquorUtil.getEvaluation(liquorId);

        return EvaluationDto.builder()
                .sweetness(Math.toIntExact(Math.round(evaluation.getSweetness())))
                .acidity(Math.toIntExact(Math.round(evaluation.getAcidity())))
                .carbonicAcid(Math.toIntExact(Math.round(evaluation.getCarbonicAcid())))
                .heavy(Math.toIntExact(Math.round(evaluation.getHeavy())))
                .scent(Math.toIntExact(Math.round(evaluation.getScent())))
                .density(Math.toIntExact(Math.round(evaluation.getDensity())))
                .build();
    }


    @Transactional(readOnly = true)
    public PersonEvaluationDto getFlavorPerson(String liquorId){
        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final PersonalEvaluation evaluation= liquorUtil.getPersonalEvaluation(user, liquor);

        return PersonEvaluationDto.builder()
                .sweetness(evaluation.getSweetness())
                .acidity(evaluation.getAcidity())
                .carbonicAcid(evaluation.getCarbonicAcid())
                .heavy(evaluation.getHeavy())
                .scent(evaluation.getScent())
                .density(evaluation.getDensity())
                .build();
    }


    @Transactional(readOnly = true)
    public boolean getFlavorCheck(String liquorId){
        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);
        final PersonalEvaluation evaluation= liquorUtil.getPersonalEvaluation(user, liquor);

        boolean flag= false;
        if(evaluation.getSweetness()!=null) flag= true;
        else if(evaluation.getAcidity()!=null) flag= true;
        else if(evaluation.getCarbonicAcid()!=null) flag= true;
        else if(evaluation.getHeavy()!=null) flag= true;
        else if(evaluation.getScent()!=null) flag= true;
        else if(evaluation.getDensity()!=null) flag= true;

        return flag;
    }


    @Transactional(readOnly = true)
    public List<String> getLiquorListName(){
        return liquorRepository.findAllName();
    }


    @Transactional
    public void updateFiltering(){
        final List<Integer> age= Arrays.asList(20, 30, 40, 50, 60);
        final List<String> gender= Arrays.asList("f", "g");

        filteringRepository.deleteAll();
        for(Integer a: age){
            for(String g: gender){
                final String liquorId= getPopularLiquor(a, g);

                if(liquorId.equals("continue")) continue;

                final LiquorFiltering filtering= LiquorFiltering.builder()
                        .age(a)
                        .gender(g)
                        .liquorId(liquorId)
                        .build();
                filteringRepository.save(filtering);
            }
        }
        filteringClickRepository.deleteAll();
    }

    public String getPopularLiquor(Integer age, String gender){
        final HashMap<String, Integer> map = new HashMap<>();
        final List<FilteringClick> clickList= filteringClickRepository.findAllByAgeAndGender(age, gender);

        for(FilteringClick f: clickList){
            map.put(f.getLiquorId(), (map.get(f.getLiquorId())==null) ? 1: map.get(f.getLiquorId())+1);
        }
        if(map.size()==0) return "continue";
        List<Map.Entry<String, Integer>> entries = new LinkedList<>(map.entrySet());
        entries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return entries.get(0).getKey();
    }

    @Transactional
    public void postScrap(String liquorId){
        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);

        final Scrap scrap= Scrap.builder()
                .scrapDate(LocalDate.now())
                .user(user)
                .liquor(liquor)
                .build();
        scrapRepository.save(scrap);
    }


    @Transactional
    public void deleteScrap(String liquorId){
        final User user= userUtil.getUserByAuthentication();
        final Liquor liquor= liquorUtil.getLiquor(liquorId);

        scrapRepository.deleteByUserAndLiquor(user, liquor);
    }


    @Transactional(readOnly = true)
    public List<LiquorInfoDto> getScrapList(Pageable pageable, Sorting sorting){
        final User user= userUtil.getUserByAuthentication();

        List<Scrap> list= new ArrayList<>();
        switch (sorting) {
            case DATE:
                list= scrapRepository.findByDate(pageable, user.getUserId()).toList();
                break;
            case STAR:
                list= scrapRepository.findByStar(pageable, user.getUserId()).toList();
                break;
            case LOWEST_COST:
                list= scrapRepository.findByLowestCost(pageable, user.getUserId()).toList();
                break;
            case HIGHEST_COST:
                list= scrapRepository.findByHighestCost(pageable, user.getUserId()).toList();
                break;
        }

        final List<LiquorInfoDto> result= new ArrayList<>();
        final Integer totalScrapNumber= scrapRepository.countByUser(user);
        for(Scrap s: list){
            final Liquor liquor= s.getLiquor();
            final String liquorCategory= liquorUtil.getCodeName(liquor.getLiquorCategory());

            final LiquorInfoDto dto= LiquorInfoDto.builder()
                    .liquorId(liquor.getLiquorId())
                    .name(liquor.getName())
                    .averageRating(liquor.getAverageRating())
                    .lowestPrice(liquor.getLowestPrice())
                    .imageUrl(liquor.getImageUrl())
                    .liquorCategory(liquorCategory)
                    .ratingNumber(reviewRepository.countByLiquor(liquor))
                    .flagScrap(true)
                    .scrapDate(s.getScrapDate())
                    .totalScrapNumber(totalScrapNumber)
                    .build();
            result.add(dto);
        }
        return result;
    }


    private List<LiquorInfoDto> byDate(List<LiquorInfoDto> result){
        return result.stream()
                .sorted(Comparator.comparing(LiquorInfoDto::getScrapDate).reversed())
                .collect(Collectors.toList());
    }

    private List<LiquorInfoDto> byStar(List<LiquorInfoDto> result){
        return result.stream()
                .sorted(Comparator.comparing(LiquorInfoDto::getAverageRating).reversed())
                .collect(Collectors.toList());
    }

    private List<LiquorInfoDto> byLowestCost(List<LiquorInfoDto> result){
        return result.stream()
                .sorted(Comparator.comparing(LiquorInfoDto::getLowestPrice))
                .collect(Collectors.toList());
    }

    private List<LiquorInfoDto> byHighestCost(List<LiquorInfoDto> result){
        return result.stream()
                .sorted(Comparator.comparing(LiquorInfoDto::getLowestPrice).reversed())
                .collect(Collectors.toList());
    }


}
