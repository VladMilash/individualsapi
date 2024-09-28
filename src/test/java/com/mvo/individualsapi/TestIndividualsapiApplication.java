package com.mvo.individualsapi;

import org.springframework.boot.SpringApplication;

public class TestIndividualsapiApplication {

    public static void main(String[] args) {
        SpringApplication.from(IndividualsapiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
