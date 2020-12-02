package com.kck.carddetection.service;

import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Service;

@Service
public class RankAndSuitExtractor {
    public static final int RANK_WIDTH = 60;
    public static final int RANK_HEIGHT = 90;

    public Mat extractRankFromCard(Mat imageMatrix) {
        Mat rankMatrix = new Mat();
        imageMatrix.copyTo(rankMatrix);
        Rect rect = new Rect(0, 10, RANK_WIDTH, RANK_HEIGHT);
        rankMatrix = new Mat(rankMatrix, rect);
        return rankMatrix;
    }

    public Mat extractSuitFromCard(Mat imageMatrix) {
        Mat rankMatrix = new Mat();
        imageMatrix.copyTo(rankMatrix);
        Rect rect = new Rect(0, 70, RANK_WIDTH, RANK_HEIGHT);
        rankMatrix = new Mat(rankMatrix, rect);
        return rankMatrix;
    }

}
