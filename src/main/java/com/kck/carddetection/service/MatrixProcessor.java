package com.kck.carddetection.service;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.springframework.stereotype.Service;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
public class MatrixProcessor {

    public Mat grayImage(Mat imageMatrix) {
        Mat result = new Mat();
        cvtColor(imageMatrix, result, COLOR_BGR2GRAY); //moje oczy >.< czemu to po prostu nie zwroci result tylko miesza w argumencie. Ehhhh...
        return result;
    }

    public Mat resizeImage(Mat imageMatrix, int width, int height) {
        Mat result = new Mat();
        resize(imageMatrix, result, new Size(width, height), 45, 45, INTER_CUBIC);
        return result;
    }

}
