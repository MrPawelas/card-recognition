package com.kck.carddetection.service;

import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Service;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
@RequiredArgsConstructor
public class ContourGiver {
    private final MatrixProcessor matrixProcessor;
    private final ImageLoader imageLoader;

    public Mat getContours(Mat imageMatrix) {
        Mat temp = imageMatrix.clone();
        temp = matrixProcessor.grayImage(temp);
        imageLoader.saveImage(temp, "src/main/resources/gray" + imageMatrix.hashCode() + ".jpg");

        temp = matrixProcessor.bluredImage(temp, 11);
        imageLoader.saveImage(temp, "src/main/resources/xdd" + imageMatrix.hashCode() + ".jpg");

        temp = matrixProcessor.imageThresholded(temp);
        imageLoader.saveImage(temp, "src/main/resources/tres" + imageMatrix.hashCode() + ".jpg");

        Mat hierarchy = new Mat();
        MatVector contoursVec = new MatVector();
        Mat result = null;
        double biggestContour = 0;
        findContours(temp, contoursVec, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);
        for (Mat mat : contoursVec.get()) {
            double area = contourArea(mat);
            if (area > biggestContour) {
                biggestContour = area;
                result = mat;
            }
        }
        return result;

    }

}
