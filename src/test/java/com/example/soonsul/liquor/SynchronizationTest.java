package com.example.soonsul.liquor;

import com.example.soonsul.liquor.dto.EvaluationRequest;
import com.example.soonsul.liquor.service.EvaluationService;
import com.example.soonsul.util.LiquorUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.properties ,"
        + "classpath:oauth.yml"
)
public class SynchronizationTest {

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private LiquorUtil liquorUtil;


    private final String liquorId= "L0010052";

    private final EvaluationRequest request1= evaluationRequest();
    private final EvaluationRequest request2= evaluationRequest();
    private final EvaluationRequest request3= evaluationRequest();
    private final EvaluationRequest request4= evaluationRequest();
    private final EvaluationRequest request5= evaluationRequest();


    @Test
    void 평균_평점_동기화_테스트(){
        //given
        request1.setLiquorPersonalRating(1.0);      //서로 다른 평점을 매기는 5개의 요청
        request2.setLiquorPersonalRating(2.0);
        request3.setLiquorPersonalRating(3.0);
        request4.setLiquorPersonalRating(4.0);
        request5.setLiquorPersonalRating(5.0);


        //when
        Thread thread1 = new Thread(() -> {     // 스레드 생성
            authentication("userId1");
            evaluationService.postEvaluation(liquorId, request1);
        });

        Thread thread2 = new Thread(() -> {
            authentication("userId2");
            evaluationService.postEvaluation(liquorId, request2);
        });

        Thread thread3 = new Thread(() -> {
            authentication("userId3");
            evaluationService.postEvaluation(liquorId, request3);
        });

        Thread thread4 = new Thread(() -> {
            authentication("userId4");
            evaluationService.postEvaluation(liquorId, request4);
        });

        Thread thread5 = new Thread(() -> {
            authentication("userId5");
            evaluationService.postEvaluation(liquorId, request5);
        });

        thread1.start();        // 스레드 시작
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

        try {
            thread1.join();     // 모든 스레드가 종료될 때까지 기다림
            thread2.join();
            thread3.join();
            thread4.join();
            thread5.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //then
        // 5개의 요청이 동시에 실행되어도 평균값은 정상적으로 계산됨
        // (1.0 + 2.0 + 3.0 + 4.0 + 5.0) / 5 = 3.0
        assertEquals(liquorUtil.getLiquor(liquorId).getAverageRating(), 3.0);
    }


    private void authentication(String userId){
        AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                AuthorityUtils.NO_AUTHORITIES
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }


    private EvaluationRequest evaluationRequest(){
        return EvaluationRequest.builder().build();
    }
}
