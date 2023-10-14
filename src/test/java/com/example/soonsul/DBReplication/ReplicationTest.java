package com.example.soonsul.DBReplication;

import com.example.soonsul.DBReplication.common.DataSourceType;
import com.example.soonsul.DBReplication.replication.ReplicationRoutingDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.properties ,"
        + "classpath:oauth.yml"
)
public class ReplicationTest {

    private static final String Test_Method_Name = "determineCurrentLookupKey";


    @Test
    @Transactional
    void 쓰기전용_트랜잭션() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //given
        ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();

        //when
        Method determineCurrentLookupKey = ReplicationRoutingDataSource.class.getDeclaredMethod(Test_Method_Name);
        determineCurrentLookupKey.setAccessible(true);

        DataSourceType dataSourceType = (DataSourceType) determineCurrentLookupKey.invoke(replicationRoutingDataSource);

        //then
        assertEquals(dataSourceType, DataSourceType.Master);
    }


    @Test
    @Transactional(readOnly = true)
    void 읽기전용_트랜잭션() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //given
        ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();

        //when
        Method determineCurrentLookupKey = ReplicationRoutingDataSource.class.getDeclaredMethod(Test_Method_Name);
        determineCurrentLookupKey.setAccessible(true);

        DataSourceType dataSourceType = (DataSourceType) determineCurrentLookupKey.invoke(replicationRoutingDataSource);

        //then
        assertEquals(dataSourceType, DataSourceType.Slave);
    }

}