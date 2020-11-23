package com.kck.carddetection.temporary;

import com.kck.carddetection.componnent.CardTemplateGiver;
import com.kck.carddetection.model.CardSuit;
import com.kck.carddetection.service.ImageLoader;
import com.kck.carddetection.service.MatrixProcessor;
import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemporaryMainRunner {

    private final ImageLoader imageLoader;
    private final MatrixProcessor matrixProcessor;
    private final CardTemplateGiver cardTemplateGiver;
    //todo to tylko tymczasowa klasa gdzie mozemy sobie testowac w mainie rzeczy
    //todo docelowo zrobi sie jakis serwis ktory w tle bedzie chodzil i czytal zdjecia z jakiegos folderu i wypluwal do innego folderu output

    public void main() {
        Mat mat = imageLoader.loadImage("src/main/resources/Pojedyncze/1.jpg");
        mat = matrixProcessor.resizeImage(mat, 500, 500);
        mat = matrixProcessor.grayImage(mat);
        imageLoader.saveImage(mat, "src/main/resources/testImage.jpg");

        imageLoader.saveImage(cardTemplateGiver.getCardSuitMatrix(CardSuit.clubs), "src/main/resources/clubsfortesting.jpg");

    }
}
