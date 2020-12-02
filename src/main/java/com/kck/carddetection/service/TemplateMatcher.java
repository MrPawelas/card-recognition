package com.kck.carddetection.service;

import com.kck.carddetection.componnent.CardTemplateGiver;
import com.kck.carddetection.model.Card;
import com.kck.carddetection.model.CardRank;
import com.kck.carddetection.model.CardSuit;
import lombok.RequiredArgsConstructor;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.opencv.opencv_core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC3;

@Service
@RequiredArgsConstructor
public class TemplateMatcher {
    private final static int STEP = 3;
    private static final int threshold = 122;
    private final CardTemplateGiver cardTemplateGiver;
    private final MatrixProcessor matrixProcessor;
    private final ImageLoader imageLoader;
    @Value("${config.rankWeight}")
    private int rankWidth;
    @Value("${config.rankHeight}")
    private int rankHeight;
    @Value("${config.suitWeight}")
    private int suitWidth;
    @Value("${config.suitHeight}")
    private int suitHeight;

    public Card matchCard(Mat rankCropped, Mat suitCropped) {

        CardRank lowestRank = Arrays.stream(CardRank.values())
                .min(Comparator.comparing(r -> templateRankMatch(r, rankCropped)))
                .get();

        List<CardSuit> includedSuits = includedSuits(suitCropped);

        CardSuit lowestSuit = Arrays.stream(CardSuit.values())
                .filter(includedSuits::contains)
                .min(Comparator.comparing(r -> templateSuitMatch(r, suitCropped)))
                .get();

        return new Card(lowestRank, lowestSuit);
    }

    private List<CardSuit> includedSuits(Mat suitMat) {
        UByteRawIndexer floatRawIndexer1 = suitMat.createIndexer();
        Mat imgThresholded = matrixProcessor.imageThresholded(matrixProcessor.grayImage(suitMat));
        UByteRawIndexer floatRawIndexer2 = imgThresholded.createIndexer();

        List<Point> points = new ArrayList<>();
        for (int i = 0; i < suitMat.rows(); i++) {
            for (int j = 0; j < suitMat.cols(); j++) {
                float value = floatRawIndexer2.get(i, j);
                if (value < threshold) {
                    points.add(new Point(i, j));
                }

            }
        }

        long sumBlack = points.stream().mapToLong(
                point -> (long) (floatRawIndexer1.get(point.x, point.y, 0)
                        + floatRawIndexer1.get(point.x, point.y, 1)
                        + floatRawIndexer1.get(point.x, point.y, 2)))
                .sum();
        long sumRed = points.stream().mapToLong(
                point -> (long)floatRawIndexer1.get(point.x, point.y, 0)
                        + floatRawIndexer1.get(point.x, point.y, 1)
                        +(255 - floatRawIndexer1.get(point.x, point.y, 2))) //ja juz nawet nie chce wiedziec dlaczego skladowa czerwona jest na 3 miejscu...
                .sum();
        List<CardSuit> cardSuitList = new ArrayList<>();

        if(sumRed > sumBlack){
            cardSuitList.add(CardSuit.spades);
            cardSuitList.add(CardSuit.clubs);
        }
        else {
            cardSuitList.add(CardSuit.diamonds);
            cardSuitList.add(CardSuit.hearts);
        }

        return cardSuitList;

    }

    private double templateRankMatch(CardRank cardRank, Mat imageMatrix) {
        double minDiff = Double.MAX_VALUE;
        Mat template = new Mat();
        Mat minMat = new Mat();
        Mat rankMat = matrixProcessor.grayImage(cardTemplateGiver.getCardRankMatrix(cardRank));
        rankMat = matrixProcessor.imageThresholded(rankMat);

        Mat grayImage = matrixProcessor.grayImage(imageMatrix);
        grayImage = matrixProcessor.imageThresholded(grayImage);

        for (int i = 0; i < grayImage.rows() - rankMat.rows() + 1; i += STEP) {
            for (int j = 0; j < grayImage.cols() - rankMat.cols() + 1; j += STEP) {
                Rect rect = new Rect(j, i, rankWidth, rankHeight);
                template = new Mat(grayImage, rect);
                double diff = diffrence(rankMat, template);
                if (minDiff >= diff) {
                    minDiff = diff;
                    minMat = template;
                }

            }
        }
        //imageLoader.saveImage(minMat, "src/main/resources/templates/Best_" + imageMatrix.hashCode() + cardRank.name()  + ".jpg");
        return minDiff;
    }

    //todo złączyć te metode z templateRankMatch
    private double templateSuitMatch(CardSuit cardSuit, Mat imageMatrix) {
        double minDiff = Double.MAX_VALUE;
        Mat template = new Mat();
        Mat minMat = new Mat();
        Mat rankMat = matrixProcessor.grayImage(cardTemplateGiver.getCardSuitMatrix(cardSuit));
        rankMat = matrixProcessor.imageThresholded(rankMat);
        Mat grayImage = matrixProcessor.grayImage(imageMatrix);
        grayImage = matrixProcessor.imageThresholded(grayImage);

        for (int i = 0; i < grayImage.rows() - rankMat.rows() + 1; i += STEP) {
            for (int j = 0; j < grayImage.cols() - rankMat.cols() + 1; j += STEP) {
                Rect rect = new Rect(j, i, rankWidth, rankHeight);
                template = new Mat(grayImage, rect);
                double diff = diffrence(rankMat, template);
                if (minDiff >= diff) {
                    minDiff = diff;
                    minMat = template;
                }

            }
        }
        //imageLoader.saveImage(minMat, "src/main/resources/templates/Best_" + imageMatrix.hashCode() + cardRank.name()  + ".jpg");
        return minDiff;
    }

    private double diffrence(Mat rankMat, Mat imageMat) {
        UByteIndexer floatRawIndexer1 = rankMat.createIndexer();
        UByteIndexer floatRawIndexer2 = imageMat.createIndexer();
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < imageMat.rows(); i++) {
            for (int j = 0; j < imageMat.cols(); j++) {
                float value = floatRawIndexer2.get(i, j);
                if (value < threshold) {
                    points.add(new Point(i, j));
                }
            }
        }

        long count = points.stream()
                .filter(point -> floatRawIndexer1.get(point.x, point.y) < threshold)
                .count();

        if (count == 0) {
            return Double.MAX_VALUE;
        }

        double coefficient = (double) points.size() / count;
        return coefficient * coefficient *
                points.stream()
                        .mapToDouble(point -> (double) Math.abs(floatRawIndexer1.get(point.x, point.y) - floatRawIndexer2.get(point.x, point.y)))
                        .average()
                        .orElse(Double.MAX_VALUE);
    }

}
