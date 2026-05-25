package com.daniel.pfm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PfmApplication {

    public static void main(String[] args) {
        EnvLoader.load();
        SpringApplication.run(PfmApplication.class, args);
    }

}
