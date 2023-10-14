package com.example.soonsul.liquor;

import com.example.soonsul.liquor.entity.*;
import com.example.soonsul.liquor.service.EvaluationService;
import com.example.soonsul.user.entity.PersonalEvaluation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AverageCalculationTest {

    @InjectMocks
    private EvaluationService evaluationService;


    @Nested
    class 전통주평점_평균계산{

        @Test
        void 값을_처음_등록하는_경우(){
            //given
            final Liquor liquor= liquor();
            final EvaluationNumber number= number();
            final PersonalEvaluation pe= personalEvaluation();


            //when
            evaluationService.calAverageRating(MethodType.POST, liquor, number, 4.5, pe);


            //then
            assertEquals(liquor.getAverageRating(), 4.5);
            assertEquals(number.getAverageRating(), 1);
            assertEquals(pe.getLiquorPersonalRating(), 4.5);
        }


        @Test
        void 값을_등록하는_경우(){
            /*
             * 평가 1개 (평균 2.5)
             * 1. 2.5점
             * 2. 3.5점 -> 등록
             * */
            //given
            final Liquor liquor= liquor();
            final EvaluationNumber number= number();
            final PersonalEvaluation pe= personalEvaluation();

            liquor.updateAverageRating(2.5);
            number.addAverageRating(CalculationType.ADD);

            //when
            evaluationService.calAverageRating(MethodType.POST, liquor, number, 3.5, pe);


            //then
            assertEquals(liquor.getAverageRating(), 3.0);
            assertEquals(number.getAverageRating(), 2);
            assertEquals(pe.getLiquorPersonalRating(), 3.5);
        }


        @Test
        void 값을_수정하는_경우(){
            /*
             * 평가 2개 (평균 3.0)
             * 1. 2.5점
             * 2. 3.5점 -> 4.0점으로 수정
             * */
            //given
            final Liquor liquor= liquor();
            final EvaluationNumber number= number();
            final PersonalEvaluation pe= personalEvaluation();

            liquor.updateAverageRating(3.0);
            number.addAverageRating(CalculationType.ADD);
            number.addAverageRating(CalculationType.ADD);
            pe.updateLiquorPersonalRating(3.5);

            //when
            evaluationService.calAverageRating(MethodType.PUT, liquor, number, 4.0, pe);


            //then
            assertEquals(liquor.getAverageRating(), 3.3);   //3.25
            assertEquals(number.getAverageRating(), 2);
            assertEquals(pe.getLiquorPersonalRating(), 4.0);
        }


        @Test
        void 값을_삭제하는_경우(){
            /*
             * 평가 2개 (평균 3.0)
             * 1. 2.5점
             * 2. 3.5점 -> 삭제
             * */
            //given
            final Liquor liquor= liquor();
            final EvaluationNumber number= number();
            final PersonalEvaluation pe= personalEvaluation();

            liquor.updateAverageRating(3.0);
            number.addAverageRating(CalculationType.ADD);
            number.addAverageRating(CalculationType.ADD);
            pe.updateLiquorPersonalRating(3.5);

            //when
            evaluationService.calAverageRating(MethodType.DELETE, liquor, number, null, pe);


            //then
            assertEquals(liquor.getAverageRating(), 2.5);
            assertEquals(number.getAverageRating(), 1);
            assertNull(pe.getLiquorPersonalRating());
        }
    }


    @Nested
    class 맛평가_평균계산{

        @Test
        void 값을_처음_등록하는_경우(){
            //given
            final Evaluation evaluation= evaluation();
            final EvaluationNumber number= number();
            final PersonalEvaluation pe= personalEvaluation();


            //when
            evaluationService.calAverageFlavor(FlavorType.SWEETNESS, MethodType.POST, evaluation,
                    number, 2, pe);


            //then
            assertEquals(evaluation.getSweetness(), 2.0);
            assertEquals(number.getSweetness(), 1);
            assertEquals(pe.getSweetness(), 2);
        }

        @Test
        void 값을_등록하는_경우(){
            /*
             * 평가 1개 (평균 3.0)
             * 1. 3점
             * 2. 0점 -> 등록
             * */
            //given
            final Evaluation evaluation= evaluation();
            final EvaluationNumber number= number();
            final PersonalEvaluation pe= personalEvaluation();

            evaluation.updateFlavor(FlavorType.SWEETNESS, 3.0);
            number.updateFlavor(FlavorType.SWEETNESS, CalculationType.ADD);

            //when
            evaluationService.calAverageFlavor(FlavorType.SWEETNESS, MethodType.POST, evaluation,
                    number, 0, pe);


            //then
            assertEquals(evaluation.getSweetness(), 1.5);
            assertEquals(number.getSweetness(), 2);
            assertEquals(pe.getSweetness(), 0);
            assertNotNull(pe.getSweetness());
        }


        @Test
        void 값을_수정하는_경우(){
            /*
             * 평가 2개 (평균 2.5)
             * 1. 3점
             * 2. 2점 -> 5점으로 수정
             * */
            //given
            final Evaluation evaluation= evaluation();
            final EvaluationNumber number= number();
            final PersonalEvaluation pe= personalEvaluation();

            evaluation.updateFlavor(FlavorType.SWEETNESS, 2.5);
            number.updateFlavor(FlavorType.SWEETNESS, CalculationType.ADD);
            number.updateFlavor(FlavorType.SWEETNESS, CalculationType.ADD);
            pe.updateFlavor(FlavorType.SWEETNESS, 2);


            //when
            evaluationService.calAverageFlavor(FlavorType.SWEETNESS, MethodType.PUT, evaluation,
                    number, 5, pe);


            //then
            assertEquals(evaluation.getSweetness(), 4.0);
            assertEquals(number.getSweetness(), 2);
            assertEquals(pe.getSweetness(), 5);
        }

        @Test
        void 값을_0으로_수정하는_경우(){
            /*
             * 평가 2개 (평균 2.5)
             * 1. 3점
             * 2. 2점 -> 0점으로 수정
             * */
            //given
            final Evaluation evaluation= evaluation();
            final EvaluationNumber number= number();
            final PersonalEvaluation pe= personalEvaluation();

            evaluation.updateFlavor(FlavorType.SWEETNESS, 2.5);
            number.updateFlavor(FlavorType.SWEETNESS, CalculationType.ADD);
            number.updateFlavor(FlavorType.SWEETNESS, CalculationType.ADD);
            pe.updateFlavor(FlavorType.SWEETNESS, 2);


            //when
            evaluationService.calAverageFlavor(FlavorType.SWEETNESS, MethodType.PUT, evaluation,
                    number, 0, pe);


            //then
            assertEquals(evaluation.getSweetness(), 1.5);
            assertEquals(number.getSweetness(), 2);
            assertEquals(pe.getSweetness(), 0);
        }

        @Test
        void 값을_삭제하는_경우(){
            /*
            * 평가 2개 (평균 2.5)
            * 1. 3점
            * 2. 2점 -> 삭제
            * */
            //given
            final Evaluation evaluation= evaluation();
            final EvaluationNumber number= number();
            final PersonalEvaluation pe= personalEvaluation();

            evaluation.updateFlavor(FlavorType.SWEETNESS, 2.5);
            number.updateFlavor(FlavorType.SWEETNESS, CalculationType.ADD);
            number.updateFlavor(FlavorType.SWEETNESS, CalculationType.ADD);
            pe.updateFlavor(FlavorType.SWEETNESS, 2);


            //when
            evaluationService.calAverageFlavor(FlavorType.SWEETNESS, MethodType.DELETE, evaluation,
                    number, null, pe);


            //then
            assertEquals(evaluation.getSweetness(), 3.0);
            assertEquals(number.getSweetness(), 1);
            assertNull(pe.getSweetness());
        }
    }


    private Liquor liquor(){
        return Liquor.builder()
                .averageRating(0.0)
                .build();
    }

    private Evaluation evaluation(){
        return Evaluation.builder()
                .sweetness(0.0)
                .build();
    }

    private EvaluationNumber number(){
        return EvaluationNumber.builder()
                .averageRating(0)
                .sweetness(0)
                .build();
    }

    private PersonalEvaluation personalEvaluation(){
        return PersonalEvaluation.builder()
                .liquorPersonalRating(0.0)
                .sweetness(0)
                .build();
    }

}