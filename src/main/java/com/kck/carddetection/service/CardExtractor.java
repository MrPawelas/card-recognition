package com.kck.carddetection.service;

import lombok.RequiredArgsConstructor;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.opencv.opencv_core.*;
import org.opencv.core.CvType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

import static org.bytedeco.opencv.global.opencv_core.flip;
import static org.bytedeco.opencv.global.opencv_core.transpose;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
@RequiredArgsConstructor
public class CardExtractor {

    private final MatrixProcessor matrixProcessor;

    public Mat extractSubImage(Mat contour, Mat image) {
        findCorners(contour);
        Mat boxMat = new Mat();
        RotatedRect rotatedRect = minAreaRect(contour);
        boxMat = findCorners(contour);
        FloatRawIndexer floatRawIndexer = boxMat.createIndexer();
        Mat pointsMat = new Mat();
        pointsMat.create(4, 2, CvType.CV_32F);

        FloatRawIndexer intRawIndexer = pointsMat.createIndexer();
        float x1 = floatRawIndexer.get(0, 0);
        float y1 = floatRawIndexer.get(0, 1);

        float x2 = floatRawIndexer.get(1, 0);
        float y2 = floatRawIndexer.get(1, 1);

        float x3 = floatRawIndexer.get(2, 0);
        float y3 = floatRawIndexer.get(2, 1);

        int height = (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1- y2,2) );
        int width = (int) Math.sqrt(Math.pow(x2 - x3, 2) + Math.pow(y2- y3,2) );

        int[][] points = new int[][]{{0, height - 1},
                {0, 0},
                {width - 1, 0},
                {width - 1, height - 1}};

        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[0].length; j++) {
                intRawIndexer.put(i, j, points[i][j]);
            }
        }

        Mat perspectiveTransformMat = getPerspectiveTransform(boxMat, pointsMat);
        Mat card = new Mat();
        warpPerspective(image, card, perspectiveTransformMat, new Size(width, height));

        if(height < width){
            transpose(card, card);
            flip(card,card,1); //1 means horizonaly
        }
        card = matrixProcessor.resizeImage(card, 425, 600);
        return card;

    }

    private Mat findCorners(Mat contour) {
        IntRawIndexer intRawIndexer = contour.createIndexer();
        ArrayList<Point> pointList = new ArrayList<>();
        for (int i = 0; i < contour.rows(); i += 1) {
            pointList.add(new Point(intRawIndexer.get(i, 0, 0), intRawIndexer.get(i, 0, 1))); //oczywiscie jak mamy punkt to pierwszy indeks to (0,0) a drugi to (0,1) zamiast po prostu pierwszy = 0, drugi = 1 XDD
        }
        Point[] points = new Point[]{
                pointList.stream().max(Comparator.comparingInt(point -> -point.x() + point.y())).get(),
                pointList.stream().max(Comparator.comparingInt(point -> -point.x() - point.y())).get(),
                pointList.stream().max(Comparator.comparingInt(point -> +point.x() - point.y())).get(),
                pointList.stream().max(Comparator.comparingInt(point -> +point.x() + point.y())).get(),
        };
        Mat boxmat = new Mat();
        boxmat.create(4, 2, CvType.CV_32F);
        FloatRawIndexer floatRawIndexer = boxmat.createIndexer();
        for (int i = 0; i < 4; i++) {
            floatRawIndexer.put(i, 0, points[i].x());
            floatRawIndexer.put(i, 1, points[i].y());
        }
        return boxmat;
    }
}
