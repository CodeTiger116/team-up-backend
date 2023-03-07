package com.hanhu.teamupbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.hanhu.teamupbackend.mapper")
@EnableScheduling
public class TeamUpBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamUpBackendApplication.class, args);
    }

}
