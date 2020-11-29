package com.kck.carddetection.service;

import lombok.RequiredArgsConstructor;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.opencv.core.CvType;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.opencv.core.CvType.*;

//toDo chyba mozna wywalic

@Service
@RequiredArgsConstructor
public class ContourGiver {
    private final MatrixProcessor matrixProcessor;
    private final ImageLoader imageLoader;

    public Mat getContours(Mat imageMatrix) {
        Mat temp = imageMatrix.clone();
        temp = matrixProcessor.grayImage(temp);
        imageLoader.saveImage(temp, "src/main/resources/gray" + imageMatrix.hashCode() + ".jpg");

        //temp = matrixProcessor.bluredImage(temp, 3);
        //imageLoader.saveImage(temp, "src/main/resources/xdd" + imageMatrix.hashCode() + ".jpg");

        temp = matrixProcessor.imageThresholded(temp);
        imageLoader.saveImage(temp, "src/main/resources/tresxxxxxx" + imageMatrix.hashCode() + ".jpg");

        Mat hierarchy = new Mat();
        MatVector contoursVec = new MatVector();
        findContours(temp, contoursVec, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);
        return Arrays.stream(contoursVec.get())
                .filter(x -> CardProcessor.probabilityOfRectangle(x) < 0.9)
                .max(Comparator.comparing((opencv_imgproc::contourArea)))
                .get();

    }
    public Mat contourToImage(Mat pointsMat){
        Mat result =  createZerosMat(RankExtractor.RANK_WIDTH,RankExtractor.RANK_HEIGHT);
        Mat  hierarchy = new Mat();
        MatVector matVector = new MatVector(pointsMat);
        opencv_imgproc.drawContours(result, matVector, 0, Scalar.BLUE, 1, 1, hierarchy, 0, new Point(0, 0));
        imageLoader.saveImage(result, "src/main/resources/cnt" + result.hashCode() + ".jpg");
        return result;
    }

    private Mat createZerosMat(int width, int height){
        Mat result =  new Mat();
        result.create(RankExtractor.RANK_HEIGHT, RankExtractor.RANK_HEIGHT, CvType.CV_32F);
        FloatRawIndexer floatRawIndexer = result.createIndexer();
        for (int i = 0; i < result.rows(); i++) {
            for (int j = 0; j < result.cols(); j++) {
                floatRawIndexer.put(i,j,0);
            }
        }
        return result;
    }
}
