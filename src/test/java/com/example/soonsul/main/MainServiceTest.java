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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MainServiceTest {

    @InjectMocks
    private MainService mainService;

    @Mock
    private ClickRepository clickRepository;

    @Mock
    private LiquorRepository liquorRepository;

    @Mock
    private UserUtil userUtil;

    @Mock
    private LiquorUtil liquorUtil;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ScrapRepository scrapRepository;

    @Mock
    private RegionClickRepository regionClickRepository;


    @Test
    void 이번주_사랑받는_전통주(){
        //given
        final List<Liquor> liquorList= liquorList();
        doReturn(liquorList).when(liquorRepository).findAll();

        final List<Integer> size= Arrays.asList(1, 0, 4, 5, 5, 2, 7, 10, 2, 0, 8, 9, 10, 7, 5);
        for(int i=0; i<liquorList.size(); i++){
            doReturn(sizeList(size.get(i))).when(clickRepository).findAllByLiquorId(liquorList.get(i).getLiquorId());
        }

        //when
        final List<WeekLiquorDto> result= mainService.getWeekLiquor();

        //then
        assertEquals(result.get(0).getLiquorId(), "L013");
        assertEquals(result.get(1).getLiquorId(), "L008");
        assertEquals(result.get(9).getLiquorId(), "L003");
    }


    @Nested
    class 지금_사랑받는_지역술{

        private final Double myLat= 104.2;
        private final Double myLon= 23.4;
        private final User user= user();

        private final Liquor liquor1= liquor("L001", 4.5, 3000L);
        private final Liquor liquor2= liquor("L002", 2.5, 15500L);
        private final Liquor liquor3= liquor("L003", 2.8, 1800L);
        private final Liquor liquor4= liquor("L004", 4.9, 3000L);
        private final Liquor liquor5= liquor("L005", 5.0, 1900L);
        private final Liquor liquor6= liquor("L006", 0.0, 20000L);
        private final Liquor liquor7= liquor("L007", 1.5, 3400L);
        private final Liquor liquor8= liquor("L008", 2.2, 5500L);
        private final Liquor liquor9= liquor("L009", 3.5, 4000L);
        private final Liquor liquor10= liquor("L010", 3.7, 4000L);
        private final Liquor liquor11= liquor("L011", 3.7, 4700L);
        private final Liquor liquor12= liquor("L012", 4.1, 2800L);
        final List<Liquor> liquorList= Arrays.asList(liquor1, liquor2, liquor3, liquor4, liquor5, liquor6, liquor7, liquor8, liquor9, liquor10, liquor11, liquor12);


        @BeforeEach
        void init(){
            doReturn(user).when(userUtil).getUserByAuthentication();
            doReturn("").when(liquorUtil).getCodeName(null);
            doReturn(Collections.emptyList()).when(liquorUtil).getBreweryList(any(String.class));
            doReturn(true).when(scrapRepository).existsByUserAndLiquor(any(User.class), any(Liquor.class));
            doReturn(10).when(reviewRepository).countByLiquor(any(Liquor.class));
        }


        @Test
        void 내주변(){
            //given
            final List<RegionClick> clickList= new ArrayList<>();
            final List<Integer> clickNumberList= Arrays.asList(10, 2, 14, 20, 43, 1, 4, 3, 8, 23, 33, 0);
            final List<Double> latList= Arrays.asList(3.0, 8.0, 3.0, 15.2, 8.0, 8.0, 15.2, 15.2, 15.2, 3.0, 15.2, 3.0);
            final List<Double> lonList= Arrays.asList(12.4, 80.9, 12.4, 100.3, 80.9, 80.9, 100.3, 100.3, 100.3, 12.4, 100.3, 12.4);
            for(int i=0; i<12; i++){
                for(int j=0; j<clickNumberList.get(i); j++){
                    clickList.add(regionClick(liquorList.get(i).getLiquorId(), "", latList.get(i), lonList.get(i)));
                }
                if(i==0 || i==2 || i==9 || i==11) continue;
                doReturn(liquorList.get(i)).when(liquorUtil).getLiquor(liquorList.get(i).getLiquorId());
            }
            doReturn(clickList).when(regionClickRepository).findAll();

            //when
            final List<RegionLiquorDto> result= mainService.getRegionLiquor("Around-me", Sorting.STAR, myLat, myLon);

            //then
            assertEquals(result.size(), 8);
            assertEquals(result.get(0).getLiquorId(), "L005");
            assertEquals(result.get(7).getLiquorId(), "L006");
        }


        @Test
        void 지역별_전라남도(){
            //given
            final List<RegionClick> clicks1= new ArrayList<>();
            clicks1.add(regionClick("L001","R009",8.0,80.9));
            clicks1.add(regionClick("L003","R009",8.0,80.9));
            clicks1.add(regionClick("L003","R009",8.0,80.9));
            clicks1.add(regionClick("L005","R009",8.0,80.9));
            final List<RegionClick> clicks2= new ArrayList<>();
            clicks2.add(regionClick("L006","R010",8.0,80.9));
            clicks2.add(regionClick("L007","R010",8.0,80.9));
            clicks2.add(regionClick("L009","R010",8.0,80.9));
            clicks2.add(regionClick("L011","R010",8.0,80.9));
            final List<RegionClick> clicks3= new ArrayList<>();
            clicks3.add(regionClick("L004","R016",8.0,80.9));
            clicks3.add(regionClick("L008","R016",8.0,80.9));
            clicks3.add(regionClick("L010","R016",8.0,80.9));
            clicks3.add(regionClick("L012","R016",8.0,80.9));

            doReturn(clicks1).when(regionClickRepository).findAllByRegion("R009");
            doReturn(clicks2).when(regionClickRepository).findAllByRegion("R010");
            doReturn(clicks3).when(regionClickRepository).findAllByRegion("R016");
            for(int i=0; i<12; i++){
                if(i==1 || i==7) continue;
                doReturn(liquorList.get(i)).when(liquorUtil).getLiquor(liquorList.get(i).getLiquorId());
            }

            //when
            final List<RegionLiquorDto> result= mainService.getRegionLiquor("Jeolla-do", Sorting.LOWEST_COST, null, null);

            //then
            assertEquals(result.size(), 10);
            assertEquals(result.get(0).getLiquorId(), "L003");
            assertEquals(result.get(9).getLiquorId(), "L006");
        }
    }


    @Nested
    class 클릭정보를_map에_저장{

        private final List<RegionClick> clickList= new ArrayList<>();

        @BeforeEach
        void init(){
            clickList.add(regionClick("L001","", 3.0, 12.4));
            clickList.add(regionClick("L002","", 8.0, 80.9));
            clickList.add(regionClick("L001","", 3.0, 12.4));
            clickList.add(regionClick("L002","", 8.0, 80.9));
            clickList.add(regionClick("L002","", 8.0, 80.9));
            clickList.add(regionClick("L002","", 8.0, 80.9));
            clickList.add(regionClick("L003","", 15.2, 100.3));
        }

        @Test
        void 내주변(){
            //given
            final Double myLat= 104.2;
            final Double myLon= 23.4;

            //when
            final HashMap<String, Pair<Integer, Double>> result= mainService.makeLiquorMap(clickList, myLat, myLon);

            //then
            assertEquals(Math.round(result.get("L001").getSecond()*10) / 10.0, -1.0);
            assertEquals(result.get("L001").getFirst(), 1);
            assertEquals(Math.round(result.get("L002").getSecond()*10) / 10.0, 9979.5);
            assertEquals(result.get("L002").getFirst(), 4);
            assertEquals(Math.round(result.get("L003").getSecond()*10) / 10.0, 8721.3);
            assertEquals(result.get("L003").getFirst(), 1);
            assertNull(result.get("L004"));
        }

        @Test
        void 지역별(){
            //given

            //when
            final HashMap<String, Pair<Integer, Double>> result= mainService.makeLiquorMap(clickList, null, null);

            //then
            assertEquals(Math.round(result.get("L001").getSecond()*10) / 10.0, 0.0);
            assertEquals(result.get("L001").getFirst(), 2);
            assertEquals(Math.round(result.get("L002").getSecond()*10) / 10.0, 0.0);
            assertEquals(result.get("L002").getFirst(), 4);
            assertEquals(Math.round(result.get("L003").getSecond()*10) / 10.0, 0.0);
            assertEquals(result.get("L003").getFirst(), 1);
            assertNull(result.get("L004"));
        }
    }


    @Test
    void 지금_사랑받는_지역술_10개_조회(){
        //given
        final HashMap<String, Pair<Integer,Double>> liquorMap= new HashMap<>();
        final List<Integer> clickNumberList= Arrays.asList(10, 2, 14, 20, 43, 2, 4, 0, 8, 23, 33, 0, 13, 50, 28);
        for(int i=1; i<=clickNumberList.size(); i++){
            liquorMap.put("L"+String.format("%03d", i), Pair.of(clickNumberList.get(i-1), 0.0));
        }

        //when
        final HashMap<String, Pair<Integer,Double>> result= mainService.sortByClickNumber(liquorMap);
        List<String> keys = new ArrayList<>(result.keySet());

        //then
        assertEquals(keys.get(0), "L014");
        assertEquals(keys.get(3), "L015");
        assertEquals(keys.get(9), "L009");
    }

    @Nested
    class 필터링{

        private final List<RegionLiquorDto> list= new ArrayList<>();

        @BeforeEach
        void init(){
            list.add(regionLiquorDto("L007",2.0,3500L,13));
            list.add(regionLiquorDto("L002",1.3,4500L,0));
            list.add(regionLiquorDto("L003",4.7,13500L,12));
            list.add(regionLiquorDto("L004",4.3,2700L,50));
            list.add(regionLiquorDto("L005",0.0,15000L,1));
            list.add(regionLiquorDto("L006",4.7,20000L,25));
            list.add(regionLiquorDto("L001",3.6,4500L,13));
        }

        @Test
        void 별점순(){
            //given

            //when
            final List<RegionLiquorDto> result= mainService.sortByCategory(Sorting.STAR, list);

            //then
            assertEquals(result.get(0).getLiquorId(), "L003");
            assertEquals(result.get(3).getLiquorId(), "L001");
            assertEquals(result.get(6).getLiquorId(), "L005");
        }

        @Test
        void 리뷰순(){
            //given


            //when
            final List<RegionLiquorDto> result= mainService.sortByCategory(Sorting.REVIEW, list);

            //then
            assertEquals(result.get(0).getLiquorId(), "L004");
            assertEquals(result.get(3).getLiquorId(), "L001");      //동일한 값 존재o-> 객체 들어간 순서로(idx)
            assertEquals(result.get(6).getLiquorId(), "L002");
        }

        @Test
        void 최저가순(){
           //given


            //when
            final List<RegionLiquorDto> result= mainService.sortByCategory(Sorting.LOWEST_COST, list);

            //then
            assertEquals(result.get(0).getLiquorId(), "L004");
            assertEquals(result.get(3).getLiquorId(), "L001");
            assertEquals(result.get(6).getLiquorId(), "L006");
        }

        @Test
        void 최고가순(){
            //given


            //when
            final List<RegionLiquorDto> result= mainService.sortByCategory(Sorting.HIGHEST_COST, list);

            //then
            assertEquals(result.get(0).getLiquorId(), "L006");
            assertEquals(result.get(3).getLiquorId(), "L002");
            assertEquals(result.get(6).getLiquorId(), "L004");
        }

    }


    private List<Liquor> liquorList(){
        final List<Liquor> list= new ArrayList<>();
        for(int i=1;i<=15;i++){
            final Liquor liquor= liquor("L"+String.format("%03d", i), 0.0,0L);
            list.add(liquor);
            if(i==1||i==2||i==6||i==9||i==10) continue;
            doReturn(liquor).when(liquorUtil).getLiquor(liquor.getLiquorId());
        }
        return list;
    }

    private List<Liquor> sizeList(int size){
        final List<Liquor> list= new ArrayList<>();
        for(int i=1;i<=size;i++){
            list.add(liquor("", 0.0,0L));
        }
        return list;
    }

    private Liquor liquor(String liquorId, Double averageRating, Long lowestPrice){
        return Liquor.builder()
                .liquorId(liquorId)
                .averageRating(averageRating)
                .lowestPrice(lowestPrice)
                .imageUrl("url")
                .build();
    }


    private RegionLiquorDto regionLiquorDto(String liquorId, Double averageRating, Long lowestPrice, Integer ratingNumber){
        return RegionLiquorDto.builder()
                .liquorId(liquorId)
                .averageRating(averageRating)
                .lowestPrice(lowestPrice)
                .ratingNumber(ratingNumber)
                .build();
    }

    private User user(){
        return User.builder().build();
    }

    private RegionClick regionClick(String liquorId, String region, Double latitude, Double longitude){
        return RegionClick.builder()
                .liquorId(liquorId)
                .region(region)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

}
