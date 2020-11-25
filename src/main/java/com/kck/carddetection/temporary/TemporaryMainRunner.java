package com.kck.carddetection.temporary;

import com.kck.carddetection.componnent.CardTemplateGiver;
import com.kck.carddetection.service.CardProcessor;
import com.kck.carddetection.service.ImageLoader;
import com.kck.carddetection.service.MatrixProcessor;
import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemporaryMainRunner {

    private final ImageLoader imageLoader;
    private final MatrixProcessor matrixProcessor;
    private final CardProcessor cardProcessor;
    private final CardTemplateGiver cardTemplateGiver;
    //todo to tylko tymczasowa klasa gdzie mozemy sobie testowac w mainie rzeczy
    //todo docelowo zrobi sie jakis serwis ktory w tle bedzie chodzil i czytal zdjecia z jakiegos folderu i wypluwal do innego folderu output

    public void run() {
//        Mat mat = imageLoader.loadImage("src/main/resources/Zbiory/1.jpg");
//        mat = matrixProcessor.resizeImage(mat, 500, 500);
//        mat = matrixProcessor.grayImage(mat);
//        mat = matrixProcessor.bluredImage(mat);
//        mat = matrixProcessor.imageThresholded(mat);
//        imageLoader.saveImage(mat, "src/main/resources/testImage.jpg");
        Mat mat2 = imageLoader.loadImage("src/main/resources/Zbiory/2.jpg");

        //  mat2 = matrixProcessor.resizeImage(mat2, 600, 400);

        Mat mat = matrixProcessor.grayImage(mat2);
        imageLoader.saveImage(mat, "src/main/resources/szary.jpg");

        mat = matrixProcessor.bluredImage(mat);
        imageLoader.saveImage(mat, "src/main/resources/rozmyty.jpg");

        mat = matrixProcessor.imageThresholded(mat);
        imageLoader.saveImage(mat, "src/main/resources/tresholded.jpg");

        mat = cardProcessor.extractCardsFromPicture(mat, mat2);
        imageLoader.saveImage(mat, "src/main/resources/done.jpg");

        //imageLoader.saveImage(cardTemplateGiver.getCardSuitMatrix(CardSuit.clubs), "src/main/resources/clubsfortesting.jpg");

    }
}
