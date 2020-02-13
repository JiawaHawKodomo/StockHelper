package com.kodomo.stockhelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StockhelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockhelperApplication.class, args);
    }

}
