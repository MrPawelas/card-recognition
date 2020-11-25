package com.kck.carddetection.service;

import lombok.RequiredArgsConstructor;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.opencv.opencv_core.*;
import org.opencv.core.CvType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

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
        Mat pointsMat = new Mat();
        pointsMat.create(4, 2, CvType.CV_32F);
        FloatRawIndexer intRawIndexer = pointsMat.createIndexer();

        int width = (int) rotatedRect.size().width();
        int height = (int) rotatedRect.size().height();

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
        card = matrixProcessor.resizeImage(card, 425, 600);
        return card;

    }

    private Mat findCorners(Mat contour) {
        IntRawIndexer intRawIndexer = contour.createIndexer();
        ArrayList<Point> pointList = new ArrayList<>();
        for (int i = 0; i < contour.rows(); i += 1) {
            pointList.add(new Point(intRawIndexer.get(i, 0, 0), intRawIndexer.get(i, 0, 1)));
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
