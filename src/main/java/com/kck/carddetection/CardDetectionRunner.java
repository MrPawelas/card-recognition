package com.kck.carddetection;

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
            arrayList = cardProcessor.extractCardsFromPicture(processedImageMatrix, originalImageMatrix);

            Paths.get(OUTPUT_DIRECTORY + Paths.get(fileName).getFileName()).toFile().mkdir();

            imageLoader.saveImage(arrayList.get(0), OUTPUT_DIRECTORY + Paths.get(fileName).getFileName() + "/contours.jpg");

            Path cardsInImagePath = Paths.get(OUTPUT_DIRECTORY + Paths.get(fileName).getFileName());
            cardsInImagePath.toFile().mkdir();

            File results = new File(cardsInImagePath + "/results.txt");
            cardsInImagePath = Paths.get(cardsInImagePath + "/cards");
            cardsInImagePath.toFile().mkdir();

            for (int i = 1; i < arrayList.size(); i++) {
                imageLoader.saveImage(arrayList.get(i), cardsInImagePath.toAbsolutePath().toString() + "/" + i + ".jpg");
                Mat card = rankExtractor.extractRankFromCard(arrayList.get(i));
                Mat cardSuit = rankExtractor.extractSuitFromCard(arrayList.get(i));

                String result = "karta " + i + " to :" + templateMatcher.matchCard(card, cardSuit).toString() + "\n";
                try {
                    FileUtils.writeStringToFile(results, result, "UTF-8", true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}