package com.example.soonsul.main;

import com.example.soonsul.liquor.entity.Liquor;
import com.example.soonsul.liquor.entity.RegionClick;
import com.example.soonsul.liquor.repository.*;
import com.example.soonsul.main.dto.RegionLiquorDto;
import com.example.soonsul.main.dto.WeekLiquorDto;
import com.example.soonsul.main.entity.Sorting;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.util.LiquorUtil;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {
    private final ClickRepository clickRepository;
    private final UserUtil userUtil;
    private final LiquorUtil liquorUtil;
    private final LiquorRepository liquorRepository;
    private final RegionClickRepository regionClickRepository;
    private final ReviewRepository reviewRepository;
    private final ScrapRepository scrapRepository;


    @Transactional(readOnly = true)
    public List<WeekLiquorDto> getWeekLiquor(){
        HashMap<String, Integer> map = new HashMap<>();

        final List<Liquor> liquorList= liquorRepository.findAll();
        for(Liquor l: liquorList){
            map.put(l.getLiquorId(), clickRepository.findAllByLiquorId(l.getLiquorId()).size());
        }

        map= map.entrySet()
                .stream()
                .filter(m -> m.getValue() > 0)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        final List<WeekLiquorDto> result= new ArrayList<>();
        for(String key : map.keySet()){
            final Liquor liquor= liquorUtil.getLiquor(key);
            final WeekLiquorDto dto= WeekLiquorDto.builder()
                    .liquorId(key)
                    .imageUrl(liquor.getImageUrl())
                    .name(liquor.getName())
                    .averageRating(liquor.getAverageRating())
                    .build();
            result.add(dto);
        }

        return result;
    }


    @Transactional(readOnly = true)
    public List<RegionLiquorDto> getRegionLiquor(String region, Sorting sorting, Double latitude, Double longitude){
        final List<RegionLiquorDto> result= new ArrayList<>();
        final List<String> codeList= new ArrayList<>();
        switch (region) {
            case "Around-me":
                return aroundMeLiquor(sorting, latitude, longitude);
            case "Metropolitan-area":
                codeList.add("R001");
                codeList.add("R002");
                codeList.add("R003");
                return regionLiquor(sorting, codeList);
            case "Gangwon-do":
                codeList.add("R004");
                return regionLiquor(sorting, codeList);
            case "Chungcheong-do":
                codeList.add("R005");
                codeList.add("R006");
                codeList.add("R012");
                return regionLiquor(sorting, codeList);
            case "Gyeongsang-do":
                codeList.add("R007");
                codeList.add("R008");
                codeList.add("R013");
                codeList.add("R014");
                codeList.add("R015");
                return regionLiquor(sorting, codeList);
            case "Jeolla-do":
                codeList.add("R009");
                codeList.add("R010");
                codeList.add("R016");
                return regionLiquor(sorting, codeList);
            case "Jeju-do":
                codeList.add("R011");
                return regionLiquor(sorting, codeList);
            default:
                return result;
        }
    }


    //내주변
    private List<RegionLiquorDto> aroundMeLiquor(Sorting sorting, Double latitude, Double longitude){
        final User user= userUtil.getUserByAuthentication();

        final List<RegionClick> clickList= (List<RegionClick>) regionClickRepository.findAll();
        HashMap<String, Pair<Integer, Double>> liquorMap = makeLiquorMap(clickList, latitude, longitude);
        liquorMap= sortByClickNumber(liquorMap);

        return sortByCategory(sorting, makeList(liquorMap, user));
    }


    //지역
    private List<RegionLiquorDto> regionLiquor(Sorting sorting, List<String> codeList){
        final User user= userUtil.getUserByAuthentication();

        final List<RegionClick> clickList= new ArrayList<>();
        for(String regionCode: codeList){
            clickList.addAll(regionClickRepository.findAllByRegion(regionCode));
        }
        HashMap<String, Pair<Integer, Double>> liquorMap = makeLiquorMap(clickList, null, null);
        liquorMap= sortByClickNumber(liquorMap);

        return sortByCategory(sorting, makeList(liquorMap, user));
    }


    public HashMap<String, Pair<Integer, Double>> makeLiquorMap(List<RegionClick> clickList, Double latitude, Double longitude){
        final HashMap<String, Pair<Integer, Double>> map = new HashMap<>();

        for(RegionClick r: clickList){
            if(map.containsKey(r.getLiquorId()) && map.get(r.getLiquorId()).getSecond() == -1.0) continue;

            Double distance;
            if(latitude==null && longitude==null) distance= 0.0;
            else distance= distanceDifference(latitude, longitude, r.getLatitude(), r.getLongitude());

            if(!map.containsKey(r.getLiquorId()))
                map.put(r.getLiquorId(), Pair.of(1, distance));
            else
                map.put(r.getLiquorId(), Pair.of(map.get(r.getLiquorId()).getFirst()+1, map.get(r.getLiquorId()).getSecond()));
        }
        return map;
    }


    public List<RegionLiquorDto> makeList(HashMap<String, Pair<Integer, Double>> liquorMap, User user){
        final List<RegionLiquorDto> list= new ArrayList<>();
        for(String liquorId: liquorMap.keySet()){
            final Liquor liquor = liquorUtil.getLiquor(liquorId);
            final RegionLiquorDto dto= RegionLiquorDto.builder()
                    .liquorId(liquorId)
                    .name(liquor.getName())
                    .averageRating(liquor.getAverageRating())
                    .alcohol(liquor.getAlcohol())
                    .capacity(liquor.getCapacity())
                    .liquorCategory(liquorUtil.getCodeName(liquor.getLiquorCategory()))
                    .brewery(liquor.getBrewery())
                    .lowestPrice(liquor.getLowestPrice())
                    .flagScrap(scrapRepository.existsByUserAndLiquor(user, liquor))
                    .ratingNumber(reviewRepository.countByLiquor(liquor))
                    .imageUrl(liquor.getImageUrl())
                    .clickNumber(liquorMap.get(liquorId).getFirst())
                    .distance(liquorMap.get(liquorId).getSecond())
                    .build();
            list.add(dto);
        }
        return list;
    }


    public Double distanceDifference(Double myLat, Double myLon, Double comLat, Double comLon){
        double R = 6371;
        final Double lat1= Math.toRadians(myLat);
        final Double lon1= Math.toRadians(myLon);
        final Double lat2= Math.toRadians(comLat);
        final Double lon2= Math.toRadians(comLon);

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = R * c * 1000;

        if(distance<=10000) return distance;
        else return -1.0;
    }


    public List<RegionLiquorDto> sortByCategory(Sorting sorting, List<RegionLiquorDto> list){
        switch (sorting) {
            case STAR:
                return byStar(list);
            case REVIEW:
                return byReview(list);
            case LOWEST_COST:
                return byLowestCost(list);
            case HIGHEST_COST:
                return byHighestCost(list);
            default:
                return list;
        }
    }


    public HashMap<String, Pair<Integer,Double>> sortByClickNumber(HashMap<String, Pair<Integer,Double>> map){
        return map.entrySet()
                .stream()
                .filter(m -> m.getValue().getFirst() > 0 && m.getValue().getSecond() != -1.0)
                .sorted(
                        (entry1, entry2) -> {
                            int compareResult = entry2.getValue().getFirst().compareTo(entry1.getValue().getFirst());
                            if (compareResult != 0) {
                                return compareResult;
                            }
                            return Double.compare(entry1.getValue().getSecond(), entry2.getValue().getSecond());
                        }
                )
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }


    //필터링
    private List<RegionLiquorDto> byStar(List<RegionLiquorDto> list){
        return list.stream()
                .sorted(Comparator.comparing(RegionLiquorDto::getAverageRating).reversed())
                .collect(Collectors.toList());
    }

    private List<RegionLiquorDto> byReview(List<RegionLiquorDto> list){
        return list.stream()
                .sorted(Comparator.comparing(RegionLiquorDto::getRatingNumber).reversed())
                .collect(Collectors.toList());
    }

    private List<RegionLiquorDto> byLowestCost(List<RegionLiquorDto> list){
        final List<RegionLiquorDto> noPriceLiquors= new ArrayList<>();
        for(RegionLiquorDto dto: list){
            if(dto.getLowestPrice()==null) noPriceLiquors.add(dto);
        }

        final List<RegionLiquorDto> sortedLiquors= list.stream()
                .filter(dto -> dto.getLowestPrice()!= null)
                .sorted(Comparator.comparing(RegionLiquorDto::getLowestPrice))
                .collect(Collectors.toList());

        sortedLiquors.addAll(noPriceLiquors);

        return sortedLiquors;
    }

    private List<RegionLiquorDto> byHighestCost(List<RegionLiquorDto> list){
        final List<RegionLiquorDto> noPriceLiquors= new ArrayList<>();
        for(RegionLiquorDto dto: list){
            if(dto.getLowestPrice()==null) noPriceLiquors.add(dto);
        }

        final List<RegionLiquorDto> sortedLiquors= list.stream()
                .filter(dto -> dto.getLowestPrice()!= null)
                .sorted(Comparator.comparing(RegionLiquorDto::getLowestPrice).reversed())
                .collect(Collectors.toList());

        sortedLiquors.addAll(noPriceLiquors);

        return sortedLiquors;
    }

}