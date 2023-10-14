package com.example.soonsul.manager;

import com.example.soonsul.liquor.entity.*;
import com.example.soonsul.liquor.exception.CodeNotExist;
import com.example.soonsul.liquor.repository.*;
import com.example.soonsul.manager.dto.LocationRes;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.oauth.jwt.JwtTokenProvider;
import com.example.soonsul.user.repository.UserRepository;
import com.example.soonsul.util.LiquorUtil;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GoogleSheetsService {

    private final Sheets sheetsService;
    private final LiquorRepository liquorRepository;
    private final LocationRepository locationRepository;
    private final LocationInfoRepository locationInfoRepository;
    private final SalePlaceRepository salePlaceRepository;
    private final SalePlaceInfoRepository salePlaceInfoRepository;
    private final PrizeRepository prizeRepository;
    private final PrizeInfoRepository prizeInfoRepository;
    private final LiquorUtil liquorUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${map.kakao.apiKey}")
    private String apiKey;

    @Value("${map.kakao.apiUrl}")
    private String apiUrl;

    private final RestTemplate restTemplate;


    public void postLiquor(String spreadsheetId, String range) throws IOException {
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();

        final List<List<Object>> list= response.getValues();
        for(List<Object> row: list){
            final String liquorId= (checkEmpty(row.get(0))==null) ? null : row.get(0).toString();
            System.out.println(liquorId);
            final String name= (checkEmpty(row.get(1))==null) ? null : row.get(1).toString();
            final Double alcohol= (checkEmpty(row.get(2))==null) ? null : Double.valueOf(row.get(2).toString());
            final String ingredient= (checkEmpty(row.get(3))==null) ? null : row.get(3).toString();
            final String brewery= (checkEmpty(row.get(4))==null) ? null : row.get(4).toString();
            final String locationName= (checkEmpty(row.get(5))==null) ? null : row.get(5).toString();
            final Integer capacity= (checkEmpty(row.get(6))==null) ? null : Integer.valueOf(row.get(6).toString());
            final Long lowestPrice= (checkEmpty(row.get(7))==null) ? null : Long.valueOf(row.get(7).toString());
            final String salePlaceName= (checkEmpty(row.get(8))==null) ? null : row.get(8).toString();
            final String siteUrl= (checkEmpty(row.get(9))==null) ? null : row.get(9).toString();
            final String phoneNumber= (checkEmpty(row.get(10))==null) ? null : row.get(10).toString();
            final String prize= (checkEmpty(row.get(11))==null) ? null : row.get(11).toString();

            final Liquor liquor= Liquor.builder()
                    .liquorId(liquorId)
                    .name(name)
                    .ingredient(ingredient)
                    .averageRating(0.0)
                    .lowestPrice(lowestPrice)
                    .alcohol(alcohol)
                    .capacity(capacity)
                    .viewCount(0L)
                    .region(liquorUtil.getCodeId(parse(locationName, " ").get(0)))
                    .imageUrl(null)
                    .liquorCategory(liquorId.substring(0, 4))
                    .build();
            final Liquor getLiquor= liquorRepository.save(liquor);


            final LocationInfo locationInfo= LocationInfo.builder()
                    .name(locationName)
                    .brewery(brewery)
                    .build();
            final LocationInfo getLocationInfo= locationInfoRepository.save(locationInfo);
            final Location location= Location.builder()
                    .locationInfoId(getLocationInfo.getLocationInfoId())
                    .liquor(getLiquor)
                    .build();
            locationRepository.save(location);


            final SalePlaceInfo salePlaceInfo= SalePlaceInfo.builder()
                    .name(salePlaceName)
                    .siteUrl(siteUrl)
                    .phoneNumber(phoneNumber)
                    .build();
            final SalePlaceInfo getSalePlaceInfo= salePlaceInfoRepository.save(salePlaceInfo);
            final SalePlace salePlace= SalePlace.builder()
                    .salePlaceInfoId(getSalePlaceInfo.getSalePlaceInfoId())
                    .liquor(getLiquor)
                    .build();
            salePlaceRepository.save(salePlace);

            if(prize!=null){
                final List<String> prizeList= parse(prize,"\n");
                for(String s: prizeList){
                    final PrizeInfo prizeInfo= PrizeInfo.builder()
                            .name(s)
                            .build();
                    final PrizeInfo getPrizeInfo= prizeInfoRepository.save(prizeInfo);

                    final Prize p= Prize.builder()
                            .prizeInfoId(getPrizeInfo.getPrizeInfoId())
                            .liquor(getLiquor)
                            .build();
                    prizeRepository.save(p);
                }
            }
        }
    }


    @Transactional(readOnly = true)
    public List<String> getLocationCheck(String spreadsheetId, String range) throws IOException {
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        final List<List<Object>> list= response.getValues();

        final List<String> errorList= new ArrayList<>();
        for(List<Object> row: list){
            final String locationName= row.get(5).toString();
            if(locationName.equals("-")) continue;

            final String url = apiUrl + "?query=" + locationName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);

            try{
                final HttpEntity<String> request = new HttpEntity<>(headers);
                LocationRes locationRes= restTemplate.exchange(url, HttpMethod.GET, request, LocationRes.class).getBody();
            } catch(ArrayIndexOutOfBoundsException e){
                errorList.add(row.get(0).toString());
            }
        }
        return errorList;
    }


    @Transactional(readOnly = true)
    public List<String> getRegionCodeCheck(String spreadsheetId, String range) throws IOException {
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        final List<List<Object>> list= response.getValues();

        final List<String> errorList= new ArrayList<>();
        for(List<Object> row: list){
            final String locationName= row.get(5).toString();
            if(locationName.equals("-")) continue;

            try{
                String codeId= liquorUtil.getCodeId(parse(locationName, " ").get(0));
            } catch(CodeNotExist e){
                errorList.add(row.get(0).toString());
            }
        }
        return errorList;
    }


    @Transactional(readOnly = true)
    public String getToken(String userId) {
        final User user= userRepository.findById(userId).get();
        return jwtTokenProvider.generateJwtToken(user);
    }


    private Object checkEmpty(Object obj){
        if(obj.equals("-") || obj.equals("")) return null;
        return obj;
    }

    private List<String> parse(String input, String split) {
        String[] lines = input.split(split);
        return new ArrayList<>(Arrays.asList(lines));
    }
}