package com.kck.carddetection;

import com.kck.carddetection.model.Card;
import com.kck.carddetection.model.ExtractedCardsFromPictureModel;
import com.kck.carddetection.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

@RequiredArgsConstructor
@Component
public class CardDetectionRunner {
    private static final String INPUT_DIRECTORY = "src/main/resources/input";
    private static final String OUTPUT_DIRECTORY = "src/main/resources/output/";
    private final ImageLoader imageLoader;
    private final MatrixProcessor matrixProcessor;
    private final CardProcessor cardProcessor;
    private final TemplateMatcher templateMatcher;
    private final RankExtractor rankExtractor;

    public void run() {
        Path input = Paths.get(INPUT_DIRECTORY);
        Path output = Paths.get(OUTPUT_DIRECTORY);


        input.toFile().mkdir();
        output.toFile().mkdir();

        for (String fileName : input.toFile().list()) {
            fileName = input + "/" + fileName;
            Mat originalImageMatrix = imageLoader.loadImage(fileName);
            Mat processedImageMatrix = matrixProcessor.grayImage(originalImageMatrix);

            processedImageMatrix = matrixProcessor.bluredImage(processedImageMatrix);
            processedImageMatrix = matrixProcessor.imageThresholded(processedImageMatrix);
            ArrayList<Mat> arrayList = new ArrayList<>();
            ExtractedCardsFromPictureModel extractedCardsFromPictureModel = new ExtractedCardsFromPictureModel();
            extractedCardsFromPictureModel = cardProcessor.extractCardsFromPicture(processedImageMatrix, originalImageMatrix);
            arrayList = extractedCardsFromPictureModel.getCards();
            Paths.get(OUTPUT_DIRECTORY + Paths.get(fileName).getFileName()).toFile().mkdir();
            Mat contours = extractedCardsFromPictureModel.getPictureWithContours();

            imageLoader.saveImage(contours, OUTPUT_DIRECTORY + Paths.get(fileName).getFileName() + "/contours.jpg");

            Path cardsInImagePath = Paths.get(OUTPUT_DIRECTORY + Paths.get(fileName).getFileName());
            cardsInImagePath.toFile().mkdir();

            File results = new File(cardsInImagePath + "/results.txt");
            cardsInImagePath = Paths.get(cardsInImagePath + "/cards");
            cardsInImagePath.toFile().mkdir();

            for (int i = 0; i < arrayList.size(); i++) {
                imageLoader.saveImage(arrayList.get(i), cardsInImagePath.toAbsolutePath().toString() + "/" + i + ".jpg");
                Mat cardMat = rankExtractor.extractRankFromCard(arrayList.get(i));
                Mat cardSuit = rankExtractor.extractSuitFromCard(arrayList.get(i));
                imageLoader.saveImage(cardSuit, "test" + cardSuit.hashCode() + ".jpg");
                Card card = templateMatcher.matchCard(cardMat, cardSuit);

                String result = "karta " + i + " to :" + card.toString() + "\n";
                try {
                    putText(contours, card.getCardRank() + " " + card.getCardSuit(), extractedCardsFromPictureModel.getCoordinatesOfTextsToPutOnCard().get(i), 1, 5, Scalar.GREEN, 8, 8, false);
                    FileUtils.writeStringToFile(results, result, "UTF-8", true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            imageLoader.saveImage(contours, OUTPUT_DIRECTORY + Paths.get(fileName).getFileName() + "/contoursDone.jpg");
        }
    }
}