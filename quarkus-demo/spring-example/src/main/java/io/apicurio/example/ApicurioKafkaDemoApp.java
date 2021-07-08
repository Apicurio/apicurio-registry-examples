package io.apicurio.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApicurioKafkaDemoApp {

    public static final String EVENTS_TOPIC = "events";

    public static void main(String[] args) {
        SpringApplication.run(ApicurioKafkaDemoApp.class, args);
    }

}
