package com.kck.carddetection.service;

import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Service;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
public class MatrixProcessor {

    public Mat grayImage(Mat imageMatrix) {
        Mat result = new Mat();
        cvtColor(imageMatrix, result, CV_BGR2GRAY);
        return result;
    }

    public Mat resizeImage(Mat imageMatrix, int width, int height) {
        Mat result = new Mat();
        resize(imageMatrix, result, new Size(width, height), 45, 45, INTER_CUBIC);
        return result;
    }
    public Mat imageThresholded(Mat imageMatrix){
        Mat result = new Mat();
        threshold(imageMatrix, result, 130, 255, CV_THRESH_OTSU );
        return result;
    }
    public Mat bluredImage(Mat imageMatrix){
        Mat result = new Mat();
        medianBlur(imageMatrix, result, 51);
        return result;
    }
}
