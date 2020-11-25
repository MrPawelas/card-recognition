package com.kck.carddetection.service;

import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Service;

@Service
public class RankExtractor {

    public Mat extractRankFromCard(Mat imageMatrix) {
        Mat rankMatrix = new Mat();
        imageMatrix.copyTo(rankMatrix);
        Rect rect = new Rect(0, 10, 60, 70);
        rankMatrix = new Mat(rankMatrix, rect);
        return rankMatrix;
    }

}
