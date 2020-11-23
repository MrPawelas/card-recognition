package com.kck.carddetection.service;

import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;

@Service
public class CardProcessor {

    public Mat extractSuitFromCard(Mat imageMatrix) {
        //todo wyciac kolor z karty
        return null;
    }

    public Mat extractRankFromCard(Mat imageMatrix) {
        //todo wyciac numer z karty
        return null;
    }


}
