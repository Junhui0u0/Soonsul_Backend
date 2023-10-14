package com.example.soonsul.DBReplication;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.properties ,"
        + "classpath:oauth.yml"
)
public class DataSourceTest {

    @Autowired
    Environment environment;


    @Test
    void Master_DataSource_테스트(@Qualifier("masterDataSource") final DataSource masterDataSource) {
        //given
        String url = environment.getProperty("spring.datasource.master.hikari.jdbc-url");
        String username = environment.getProperty("spring.datasource.master.hikari.username");
        String driverClassName = environment.getProperty("spring.datasource.master.hikari.driver-class-name");
        Boolean readOnly = Boolean.valueOf(environment.getProperty("spring.datasource.master.hikari.read-only"));

        //when
        HikariDataSource hikariDataSource = (HikariDataSource) masterDataSource;

        //then
        assertThat(hikariDataSource.isReadOnly()).isEqualTo(readOnly);
        assertThat(hikariDataSource.getJdbcUrl()).isEqualTo(url);
        assertThat(hikariDataSource.getUsername()).isEqualTo(username);
        assertThat(hikariDataSource.getDriverClassName()).isEqualTo(driverClassName);
    }


    @Test
    void Slave_DataSource_테스트(@Qualifier("slaveDataSource") final DataSource slaveDataSource) {
        //given
        String url = environment.getProperty("spring.datasource.slave.hikari.jdbc-url");
        String username = environment.getProperty("spring.datasource.slave.hikari.username");
        String driverClassName = environment.getProperty("spring.datasource.slave.hikari.driver-class-name");
        Boolean readOnly = Boolean.valueOf(environment.getProperty("spring.datasource.slave.hikari.read-only"));

        //when
        HikariDataSource hikariDataSource = (HikariDataSource) slaveDataSource;

        //then
        assertThat(hikariDataSource.isReadOnly()).isEqualTo(readOnly);
        assertThat(hikariDataSource.getJdbcUrl()).isEqualTo(url);
        assertThat(hikariDataSource.getUsername()).isEqualTo(username);
        assertThat(hikariDataSource.getDriverClassName()).isEqualTo(driverClassName);
    }

}