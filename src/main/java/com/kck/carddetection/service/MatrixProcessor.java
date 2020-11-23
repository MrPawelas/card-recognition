package com.kck.carddetection.service;

import com.sun.scenario.effect.GaussianBlur;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_imgproc.Vec2fVector;
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
    public Mat imageThresholded(Mat imageMatrix){
        Mat result = new Mat();
        threshold(imageMatrix, result, 130, 255, CV_THRESH_OTSU );
        return result;
    }
    public Mat bluredImage(Mat imageMatrix){
        Mat result = new Mat();
        medianBlur(imageMatrix,result,11);
        return result;
    }
}
