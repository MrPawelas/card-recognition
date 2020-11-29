package com.kck.carddetection.service;

import com.kck.carddetection.componnent.CardTemplateGiver;
import com.kck.carddetection.model.Card;
import com.kck.carddetection.model.CardRank;
import lombok.RequiredArgsConstructor;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.absdiff;

@Service
@RequiredArgsConstructor
public class TemplateMatcher {
    private final CardTemplateGiver cardTemplateGiver;
    private final MatrixProcessor matrixProcessor;
    private final ImageLoader imageLoader;

    private final static int STEP = 3;
    @Value("${config.rankWeight}")
    private int rankWidth;
    @Value("${config.rankHeight}")
    private int rankHeight;
    private static final int threshold = 122;

    public Card matchCard(Mat imageMatrix) {

        CardRank lowestRank =  Arrays.stream(CardRank.values())
                .min(Comparator.comparing(r -> TemplateMatch(r, imageMatrix)))
                .get();

        return new Card(lowestRank, null);
    }
    private double TemplateMatch(CardRank cardRank, Mat imageMatrix){
        double minDiff = Double.MAX_VALUE;
        Mat template = new Mat();
        Mat minMat = new Mat();
        Mat rankMat =  matrixProcessor.grayImage(cardTemplateGiver.getCardRankMatrix(cardRank));
        Mat grayImage = matrixProcessor.grayImage(imageMatrix);

        for (int i = 0; i < grayImage.rows() - rankMat.rows() + 1; i+=STEP) {
            for (int j = 0; j < grayImage.cols() - rankMat.cols() + 1; j+=STEP) {
                Rect rect = new Rect(j, i, rankWidth, rankHeight);
                template = new Mat(grayImage, rect);
                double diff = diffrence(rankMat, template);
                if(minDiff >= diff){
                    minDiff = diff;
                    minMat = template;
                }

            }
        }
        //imageLoader.saveImage(minMat, "src/main/resources/templates/Best_" + imageMatrix.hashCode() + cardRank.name()  + ".jpg");
        return minDiff;
    }
    private double diffrence(Mat rankMat, Mat imageMat){
        UByteIndexer floatRawIndexer1 = rankMat.createIndexer();
        UByteIndexer floatRawIndexer2 = imageMat.createIndexer();
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < imageMat.rows(); i++) {
            for (int j = 0; j < imageMat.cols(); j++) {
                float value = floatRawIndexer2.get(i,j);
                if (value < threshold){
                    points.add(new Point(i,j));
                }
            }
        }

        long count = points.stream()
                .filter(point -> floatRawIndexer1.get(point.x, point.y) < threshold)
                .count();

        if (count == 0){
            return Double.MAX_VALUE;
        }

        double coefficient = (double)points.size() / count;
        return coefficient * coefficient *
                points.stream()
                .mapToDouble(point -> (double) Math.abs(floatRawIndexer1.get(point.x, point.y) - floatRawIndexer2.get(point.x, point.y)))
                .average()
                .orElse(Double.MAX_VALUE);
    }

}
