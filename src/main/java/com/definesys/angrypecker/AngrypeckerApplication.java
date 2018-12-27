package com.definesys.angrypecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"com.definesys.mpaas","com.definesys.angrypecker"})
public class AngrypeckerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AngrypeckerApplication.class, args);
    }

}

