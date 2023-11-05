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
import org.springframework.data.util.Pair;
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
    private final PrizeRepository prizeRepository;
    private final LiquorUtil liquorUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationNumberRepository numberRepository;

    @Value("${map.kakao.apiKey}")
    private String apiKey;

    @Value("${map.kakao.apiUrl}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    @Transactional
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
            final String location= (checkEmpty(row.get(5))==null) ? null : row.get(5).toString();
            final Integer capacity= (checkEmpty(row.get(6))==null) ? null : Integer.valueOf(row.get(6).toString());
            final Long lowestPrice= (checkEmpty(row.get(7))==null) ? null : Long.valueOf(row.get(7).toString());
            final String salePlace= (checkEmpty(row.get(8))==null) ? null : row.get(8).toString();
            final String siteUrl= (checkEmpty(row.get(9))==null) ? null : row.get(9).toString();
            final String phoneNumber= (checkEmpty(row.get(10))==null) ? null : row.get(10).toString();
            final String prize= (checkEmpty(row.get(11))==null) ? null : row.get(11).toString();
            final String presenceImage= (checkEmpty(row.get(12))==null) ? null : row.get(12).toString().substring(0, 1);
            final String update= (checkEmpty(row.get(13))==null) ? null : row.get(13).toString().substring(0, 1);

            if(!liquorRepository.findById(liquorId).isPresent()) {
                insertLiquor(liquorId, name, ingredient, lowestPrice, alcohol, capacity, location, brewery,
                        salePlace, phoneNumber, siteUrl, prize, presenceImage);
            }
            else if(update.equals("O")){
                updateLiquor(liquorId, name, ingredient, lowestPrice, alcohol, capacity, location,
                        brewery, salePlace, phoneNumber, siteUrl, prize, presenceImage);
            }
        }
    }

    private void insertLiquor(String liquorId, String name, String ingredient, Long lowestPrice,
                              Double alcohol, Integer capacity, String location, String brewery,
                              String salePlace, String phoneNumber, String siteUrl, String prize, String presenceImage){
        //양조장 위도,경도
        final Pair<Double,Double> locationInfo= getLatitude(location);

        //전통주 메인사진
        String imageUrl= "";
        if(presenceImage.equals("O")) imageUrl= "https://soonsool-bucket.s3.ap-northeast-2.amazonaws.com/liquor/main/"+liquorId+"_main.png";
        else imageUrl= "https://cdn.discordapp.com/attachments/1103554508484792390/1154012305667928074/IMG_2787.png";

        final Liquor liquor= Liquor.builder()
                .liquorId(liquorId)
                .name(name)
                .ingredient(ingredient)
                .averageRating(0.0)
                .lowestPrice(lowestPrice)
                .alcohol(alcohol)
                .capacity(capacity)
                .viewCount(0L)
                .region(liquorUtil.getCodeId(parse(location, " ").get(0)))
                .imageUrl(imageUrl)
                .liquorCategory(liquorId.substring(0, 4))
                .brewery(brewery)
                .location(location)
                .latitude(locationInfo.getFirst())
                .longitude(locationInfo.getSecond())
                .salePlace(salePlace)
                .phoneNumber(phoneNumber)
                .siteUrl(siteUrl)
                .build();
        final Liquor getLiquor= liquorRepository.save(liquor);

        if(prize!=null){
            final List<String> prizeList= parse(prize,"\n");
            for(String s: prizeList){
                final Prize p= Prize.builder()
                        .name(s)
                        .liquor(getLiquor)
                        .build();
                prizeRepository.save(p);
            }
        }

        insertEvaluation(liquorId);
        insertEvaluationNumber(liquorId);
    }

    private Pair<Double,Double> getLatitude(String location){
        final String url = apiUrl + "?query=" + location;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);

        final HttpEntity<String> request = new HttpEntity<>(headers);
        LocationRes response= restTemplate.exchange(url, HttpMethod.GET, request, LocationRes.class).getBody();

        return Pair.of(response.documents[0].latitude, response.documents[0].longitude);
    }

    private void insertEvaluation(String liquorId){
        final Optional<Evaluation> e = evaluationRepository.findById(liquorId);
        if (e.isPresent()) return;

        final Evaluation evaluation = Evaluation.builder()
                .evaluationId(liquorId)
                .sweetness(0.0)
                .acidity(0.0)
                .carbonicAcid(0.0)
                .heavy(0.0)
                .scent(0.0)
                .density(0.0)
                .build();
        evaluationRepository.save(evaluation);
    }

    private void insertEvaluationNumber(String liquorId){
        final Optional<EvaluationNumber> e = numberRepository.findById(liquorId);
        if (e.isPresent()) return;

        final EvaluationNumber number = EvaluationNumber.builder()
                .liquorId(liquorId)
                .averageRating(0)
                .sweetness(0)
                .acidity(0)
                .carbonicAcid(0)
                .heavy(0)
                .scent(0)
                .density(0)
                .build();
        numberRepository.save(number);
    }


    private void updateLiquor(String liquorId, String name, String ingredient, Long lowestPrice,
                              Double alcohol, Integer capacity, String location, String brewery,
                              String salePlace, String phoneNumber, String siteUrl, String prize, String presenceImage){
        final Liquor liquor= liquorUtil.getLiquor(liquorId);

        //양조장 위도,경도
        final Pair<Double,Double> locationInfo= getLatitude(location);

        //전통주 메인사진
        String imageUrl= "";
        if(presenceImage.equals("O")) imageUrl= "https://soonsool-bucket.s3.ap-northeast-2.amazonaws.com/liquor/main/"+liquorId+"_main.png";
        else imageUrl= "https://cdn.discordapp.com/attachments/1103554508484792390/1154012305667928074/IMG_2787.png";

        liquor.updateLiquor(name, ingredient, lowestPrice, liquorUtil.getCodeId(parse(location, " ").get(0)), alcohol, capacity, imageUrl,
                liquorId.substring(0, 4), location, brewery, salePlace, phoneNumber, siteUrl, locationInfo.getFirst(), locationInfo.getSecond());

        prizeRepository.deleteAllByLiquor(liquor);
        if(prize!=null){
            final List<String> prizeList= parse(prize,"\n");
            for(String s: prizeList){
                final Prize p= Prize.builder()
                        .name(s)
                        .liquor(liquor)
                        .build();
                prizeRepository.save(p);
            }
        }
    }
    

    @Transactional(readOnly = true)
    public List<Pair<String,String>> checkDataFormat(String spreadsheetId, List<String> rangeList) throws IOException {
        final List<Pair<String,String>> errorList= new ArrayList<>();
        for(String range: rangeList){
            ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
            final List<List<Object>> list= response.getValues();

            for(List<Object> row: list){
                final String location= row.get(5).toString();
                if(location.equals("-")) continue;

                final String url = apiUrl + "?query=" + location;

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", apiKey);

                try{
                    final HttpEntity<String> request = new HttpEntity<>(headers);
                    LocationRes locationRes= restTemplate.exchange(url, HttpMethod.GET, request, LocationRes.class).getBody();
                    final Double latitude= locationRes.documents[0].latitude;
                } catch(ArrayIndexOutOfBoundsException e){
                    errorList.add(Pair.of(row.get(0).toString(), "양조장 주소 다시 확인 (위도,경도값 조회X)"));
                }

                try{
                    String codeId= liquorUtil.getCodeId(parse(location, " ").get(0));
                } catch(CodeNotExist e){
                    errorList.add(Pair.of(row.get(0).toString(), "양조장 주소 행정구역 다시 확인"));
                }
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