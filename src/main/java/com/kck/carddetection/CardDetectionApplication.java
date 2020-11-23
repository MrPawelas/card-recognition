package com.kck.carddetection;

import com.kck.carddetection.temporary.TemporaryMainRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CardDetectionApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(CardDetectionApplication.class, args);
        applicationContext.getBean(TemporaryMainRunner.class).run();
    }

}
