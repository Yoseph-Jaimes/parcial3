package com.parcial3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.parcial3")
public class Parcial3Application {

    public static void main(String[] args) {
        SpringApplication.run(Parcial3Application.class, args);
    }
}