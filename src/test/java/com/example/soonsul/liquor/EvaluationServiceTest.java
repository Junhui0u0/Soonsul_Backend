package com.example.soonsul.liquor;

import com.example.soonsul.liquor.dto.EvaluationRequest;
import com.example.soonsul.liquor.entity.*;
import com.example.soonsul.liquor.exception.PersonalRatingNull;
import com.example.soonsul.liquor.repository.ReviewRepository;
import com.example.soonsul.liquor.service.EvaluationService;
import com.example.soonsul.user.entity.PersonalEvaluation;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.repository.PersonalEvaluationRepository;
import com.example.soonsul.util.LiquorUtil;
import com.example.soonsul.util.UserUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;


import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EvaluationServiceTest {

    @InjectMocks
    private EvaluationService evaluationService;

    @Mock
    private UserUtil userUtil;

    @Mock
    private LiquorUtil liquorUtil;

    @Mock
    private PersonalEvaluationRepository personalEvaluationRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    private final User user= user();

    private final Liquor liquor= liquor();

    private final Evaluation evaluation= evaluation();

    private final EvaluationNumber number= number();

    private final PersonalEvaluation pe= personalEvaluation();

    private final String liquorId= "L0010001";

    private final EvaluationRequest request= evaluationRequest();


    @Nested
    class 평가등록{

        @BeforeEach
        void init(){
            doReturn(user).when(userUtil).getUserByAuthentication();
            doReturn(liquor).when(liquorUtil).getLiquor(liquorId);
            doReturn(evaluation).when(liquorUtil).getEvaluation(liquorId);
            doReturn(number).when(liquorUtil).getEvaluationNumber(liquorId);
            doReturn(pe).when(personalEvaluationRepository).save(any(PersonalEvaluation.class));
        }


        @Test
        void 평균평점만_평가할_경우(){
            //given
            request.setLiquorPersonalRating(3.0);

            //when
            evaluationService.postEvaluation(liquorId, request);

            //then
            assertEquals(pe.getLiquorPersonalRating(), 3.0);
            assertNull(pe.getSweetness());
            verify(reviewRepository,times(0)).save(any(Review.class));
        }


        @Test
        void 평균평점_맛평가만_평가할_경우(){
            //given
            request.setLiquorPersonalRating(4.0);
            request.setSweetness(3);
            request.setDensity(5);

            //when
            evaluationService.postEvaluation(liquorId, request);

            //then
            assertEquals(pe.getLiquorPersonalRating(), 4.0);
            assertEquals(pe.getSweetness(), 3);
            assertEquals(pe.getDensity(), 5);
            assertNull(pe.getAcidity());
            verify(reviewRepository,times(0)).save(any(Review.class));
        }


        @Test
        void 평균평점_리뷰만_평가할_경우(){
            //given
            request.setLiquorPersonalRating(5.0);
            request.setReviewContent("선물용으로 추천합니다");

            //when
            evaluationService.postEvaluation(liquorId, request);

            //then
            assertEquals(pe.getLiquorPersonalRating(), 5.0);
            assertNull(pe.getSweetness());
            verify(reviewRepository,times(1)).save(any(Review.class));
        }

        @Test
        void 평균평점_맛평가_리뷰_모두_평가할_경우(){
            //given
            request.setLiquorPersonalRating(5.0);
            request.setSweetness(4);
            request.setAcidity(1);
            request.setReviewContent("선물용으로 추천합니다");


            //when
            evaluationService.postEvaluation(liquorId, request);

            //then
            assertEquals(pe.getLiquorPersonalRating(), 5.0);
            assertEquals(pe.getSweetness(), 4);
            assertEquals(pe.getAcidity(), 1);
            assertNull(pe.getDensity());
            verify(reviewRepository,times(1)).save(any(Review.class));
        }
    }


    @Test
    void 평점이_NULL인_경우(){
        //given

        //when, then
        assertThatThrownBy(() -> evaluationService.putEvaluation(liquorId, request))
                .isInstanceOf(PersonalRatingNull.class)
                .hasMessageContaining("personal rating is null");
    }


    @Nested
    class 평가수정{

        @BeforeEach
        void init(){
            request.setLiquorPersonalRating(pe.getLiquorPersonalRating());
            doReturn(user).when(userUtil).getUserByAuthentication();
            doReturn(liquor).when(liquorUtil).getLiquor(liquorId);
            doReturn(evaluation).when(liquorUtil).getEvaluation(liquorId);
            doReturn(number).when(liquorUtil).getEvaluationNumber(liquorId);
            doReturn(pe).when(liquorUtil).getPersonalEvaluation(user, liquor);
        }

        @Nested
        class 평점만_수정할_경우{
            @Test
            void 수정할값이_이전이랑_같은_경우(){
                //given
                pe.updateLiquorPersonalRating(3.0);
                request.setLiquorPersonalRating(3.0);

                //when
                evaluationService.putEvaluation(liquorId, request);

                //then
                assertEquals(pe.getLiquorPersonalRating(), 3.0);
            }

            @Test
            void 수정할값이_이전이랑_다른_경우(){
                //given
                pe.updateLiquorPersonalRating(3.0);
                number.addAverageRating(CalculationType.ADD);
                request.setLiquorPersonalRating(4.0);

                //when
                evaluationService.putEvaluation(liquorId, request);

                //then
                assertEquals(liquor.getAverageRating(), 4.0);
                assertEquals(pe.getLiquorPersonalRating(), 4.0);
            }
        }

        @Nested
        class 맛평가만_수정할_경우{      //평점은 항상 요청되어야함

            @Test
            void 수정할값이_이전이랑_같은_경우(){
                //given
                evaluation.updateFlavor(FlavorType.SWEETNESS, 4.0);
                pe.updateFlavor(FlavorType.SWEETNESS, 4);
                number.updateFlavor(FlavorType.SWEETNESS, CalculationType.ADD);
                request.setSweetness(4);

                //when
                evaluationService.putEvaluation(liquorId, request);

                //then
                assertEquals(evaluation.getFlavor(FlavorType.SWEETNESS), 4.0);
            }

            @DisplayName("평균 수정")
            @Test
            void 수정할값이_이전이랑_다른_경우(){
                //given
                evaluation.updateFlavor(FlavorType.SWEETNESS, 4.0);
                pe.updateFlavor(FlavorType.SWEETNESS, 4);
                number.updateFlavor(FlavorType.SWEETNESS, CalculationType.ADD);
                request.setSweetness(2);

                //when
                evaluationService.putEvaluation(liquorId, request);

                //then
                assertEquals(evaluation.getFlavor(FlavorType.SWEETNESS), 2.0);
            }

            @DisplayName("평균 등록")
            @Test
            void 평가한값을_등록할_경우(){
                //given
                pe.updateFlavor(FlavorType.SWEETNESS, null);
                request.setSweetness(2);

                //when
                evaluationService.putEvaluation(liquorId, request);

                //then
                assertEquals(evaluation.getFlavor(FlavorType.SWEETNESS), 2);
                assertEquals(number.getFlavor(FlavorType.SWEETNESS), 1);
            }

            @DisplayName("평균 삭제")
            @Test
            void 평가한값을_삭제할_경우(){
                //given
                evaluation.updateFlavor(FlavorType.SWEETNESS, 4.0);
                pe.updateFlavor(FlavorType.SWEETNESS, 4);
                number.updateFlavor(FlavorType.SWEETNESS, CalculationType.ADD);
                request.setSweetness(null);

                //when
                evaluationService.putEvaluation(liquorId, request);

                //then
                assertEquals(evaluation.getFlavor(FlavorType.SWEETNESS), 0.0);
                assertEquals(number.getFlavor(FlavorType.SWEETNESS), 0);
            }
        }


        @Nested
        class 리뷰만_수정할_경우{

            @Test
            void 리뷰를_등록하는_경우(){     //이전 null, 요청 o
                //given
                request.setReviewContent("선물용으로 추천합니다");

                //when
                evaluationService.putEvaluation(liquorId, request);

                //then
                verify(reviewRepository,times(1)).save(any(Review.class));
            }

            @Test
            void 리뷰를_삭제하는_경우(){     //이전 o, 요청 null
                //given
                doReturn(Optional.of(review())).when(reviewRepository).findByUserAndLiquor(user, liquor);
                request.setReviewContent(null);

                //when
                evaluationService.putEvaluation(liquorId, request);

                //then
                verify(reviewRepository,times(1)).deleteById(any(Long.class));
            }

            @Test
            void 리뷰를_수정하는_경우(){
                //given
                final Review review= review();
                doReturn(Optional.of(review)).when(reviewRepository).findByUserAndLiquor(user, liquor);
                request.setReviewContent("요리에 곁들어 먹기 좋아요");

                //when
                evaluationService.putEvaluation(liquorId, request);

                //then
                assertEquals(review.getContent(), request.getReviewContent());
            }
        }
    }



    private User user(){
        return User.builder()
                .build();
    }

    private Liquor liquor(){
        return Liquor.builder()
                .averageRating(0.0)
                .build();
    }

    private Evaluation evaluation(){
        return Evaluation.builder()
                .sweetness(0.0)
                .acidity(0.0)
                .carbonicAcid(0.0)
                .density(0.0)
                .scent(0.0)
                .heavy(0.0)
                .build();
    }

    private EvaluationNumber number(){
        return EvaluationNumber.builder()
                .averageRating(0)
                .sweetness(0)
                .acidity(0)
                .carbonicAcid(0)
                .density(0)
                .scent(0)
                .heavy(0)
                .build();
    }

    private PersonalEvaluation personalEvaluation(){
        return PersonalEvaluation.builder()
                .liquorPersonalRating(0.0)
                .build();
    }

    private EvaluationRequest evaluationRequest(){
        return EvaluationRequest.builder().build();
    }

    private Review review(){
        return Review.builder()
                .reviewId(1L)
                .build();
    }
}
