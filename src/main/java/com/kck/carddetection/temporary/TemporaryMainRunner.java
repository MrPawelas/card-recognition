package com.kck.carddetection.temporary;

import com.kck.carddetection.componnent.CardTemplateGiver;
import com.kck.carddetection.service.*;
import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class TemporaryMainRunner {

    private final ImageLoader imageLoader;
    private final MatrixProcessor matrixProcessor;
    private final CardProcessor cardProcessor;
    private final CardMatcher cardMatcher;
    private final TemplateMatcher templateMatcher;
    private final CardTemplateGiver cardTemplateGiver;
    private final RankExtractor rankExtractor;
    //todo to tylko tymczasowa klasa gdzie mozemy sobie testowac w mainie rzeczy
    //todo docelowo zrobi sie jakis serwis ktory w tle bedzie chodzil i czytal zdjecia z jakiegos folderu i wypluwal do innego folderu output

    public void run() {

        Mat mat2 = imageLoader.loadImage("src/main/resources/Zbiory/1.jpg");

        Mat mat = matrixProcessor.grayImage(mat2);
        imageLoader.saveImage(mat, "src/main/resources/szary.jpg");

        mat = matrixProcessor.bluredImage(mat);
        imageLoader.saveImage(mat, "src/main/resources/rozmyty.jpg");

        mat = matrixProcessor.imageThresholded(mat);
        imageLoader.saveImage(mat, "src/main/resources/tresholded.jpg");

        ArrayList<Mat> arrayList = new ArrayList<>();

        arrayList = cardProcessor.extractCardsFromPicture(mat, mat2);

        imageLoader.saveImage(arrayList.get(0), "src/main/resources/done/done1.jpg"); //indeks 0 to zdjecie z obramowaniami kart, nastepne ineksy to indywidualne karty

        for (int i = 1; i < arrayList.size(); i++) {
            imageLoader.saveImage(arrayList.get(i), "src/main/resources/done/temp1" + i + ".jpg");
            Mat card = rankExtractor.extractRankFromCard(arrayList.get(i));
            imageLoader.saveImage(card, "src/main/resources/done/tempRank1" + i + ".jpg");
            System.out.println("karta to :" + templateMatcher.matchCard(card).toString());
        }

        imageLoader.saveImage(mat, "src/main/resources/done.jpg");


    }
}
