package com.kck.carddetection;

import com.kck.carddetection.services.ImageLoader;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CardDetectionApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(CardDetectionApplication.class, args);
//        Mat mat = applicationContext.getBean(ImageLoader.class).loadImage("src/main/resources/Spades.jpg");
//        applicationContext.getBean(ImageLoader.class).saveImage(mat, "src/main/resources/newImage.jpg");
//    todo trzeba zrobić jakąs klase która jedną metodą zczyta całość z jakiegos folderu i da wynik zeby nie bawic sie w getBean
    }

}
