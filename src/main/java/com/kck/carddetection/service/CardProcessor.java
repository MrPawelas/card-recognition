package com.kck.carddetection.service;

import lombok.RequiredArgsConstructor;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.opencv.opencv_core.*;
import org.opencv.core.CvType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Arrays;

import static org.bytedeco.opencv.global.opencv_imgproc.*;


@Service
@RequiredArgsConstructor
public class CardProcessor {

    private final MatrixProcessor matrixProcessor;

    //todo rozbic na dwie metody
    public Mat extractCardsFromPicture(Mat imageMatrix, Mat originalImage) {
        Mat hierarchy = new Mat();
        Mat result = originalImage.clone();

        MatVector contoursVec = new MatVector(); //to jest vector z c++ a nie że wektor jako jeden wiersz macierzy
        findContours(imageMatrix, contoursVec, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);

        MatVector cntfiltered = new MatVector();
        MatVector boxVector = new MatVector();

        for (Mat mat : contoursVec.get()) {
            //todo ten zakomentowany kawałek może się przydać - jak kiedys zamiast prostokatow bedzie widzial niepozamykane figury to odkomentowac
            //  double epsilon = 0.1*arcLength(mat,true);
            //   Mat temp = new Mat();
            //   temp=mat.clone();
            // approxPolyDP(mat,temp,epsilon,true);
            if (contourArea(mat) > 1000 && probabilityOfRectangle(mat) > 0.9) {
                cntfiltered.push_back(mat);
                System.out.println("rect");
            }
        }

        ArrayList<Scalar> colors = new ArrayList<>();
        addExampleColors(colors);

        List<Mat> mats = Arrays.asList(cntfiltered.get()); //taki tam odpowiednik enumerate w javie xd
        ListIterator<Mat> it = mats.listIterator();

        while (it.hasNext()) {
            int index = it.nextIndex();
            it.next();
            drawContours(result, cntfiltered, index, colors.get(index),10,10,hierarchy,0,new Point(0,0));
            extractSubImage(cntfiltered.get(index), originalImage,index);
        }

        return result;
    }

    public Mat extractSuitFromCard(Mat imageMatrix) {
        //todo wyciac kolor z karty
        return null;
    }

    public Mat extractRankFromCard(Mat imageMatrix) {
        //todo wyciac numer z karty
        return null;
    }

    private void addExampleColors(List<Scalar> colors) {
        colors.add(Scalar.GREEN);
        colors.add(Scalar.RED);
        colors.add(Scalar.BLACK);
        colors.add(Scalar.MAGENTA);
        colors.add(Scalar.BLUE);
        colors.add(Scalar.BLUE);
        colors.add(Scalar.BLUE);
        colors.add(Scalar.BLUE);
        colors.add(Scalar.BLUE);
        colors.add(Scalar.BLUE);

    }

    private double probabilityOfRectangle(Mat contour) {
        Mat boxMat = new Mat();
        RotatedRect rotatedRect = minAreaRect(contour);
        boxPoints(rotatedRect, boxMat);
        FloatRawIndexer uByteIndexer = boxMat.createIndexer();
        int rows = boxMat.rows(), cols = boxMat.cols();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                uByteIndexer.put(i, j, Math.round(uByteIndexer.get(i, j)));
                // System.out.println(uByteIndexer.get(i,j));

            }
        }
        return contourArea(contour) / contourArea(boxMat);
    }

    private Mat extractSubImage(Mat contour, Mat image,int index){
        findCorners(contour);
        Mat boxMat = new Mat();
        RotatedRect rotatedRect = minAreaRect(contour);
        boxPoints(rotatedRect, boxMat);
        Mat pointsMat = new Mat();
        pointsMat.create(4,2,CvType.CV_32F);
        FloatRawIndexer intRawIndexer = pointsMat.createIndexer();
        int width = (int) rotatedRect.size().width();
        int height = (int) rotatedRect.size().height();

        int[][] points = new int[][]   {{0, height-1},
                {0, 0},
                {width-1, 0},
                {width-1, height-1}};

        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[0].length; j++) {
                intRawIndexer.put(i, j, points[i][j]);
            }
        }
        Mat perspectiveTransformMat = getPerspectiveTransform(boxMat,pointsMat);
        Mat card = new Mat();
        warpPerspective(image,card,perspectiveTransformMat,new Size(width,height));

        ImageLoader imageLoader = new ImageLoader();
        imageLoader.saveImage(card, "src/main/resources/SubImage" + index + ".jpg");
        return card;
    }
    private Point[] findCorners(Mat contour){
        IntRawIndexer intRawIndexer = contour.createIndexer();
        ArrayList<Point> pointList = new ArrayList<>();
        for (int i = 0; i < contour.rows(); i+=1) {
            pointList.add(new Point(intRawIndexer.get(i,0,0),intRawIndexer.get(i,0,1)));
        }
       /* System.out.println(pointList.stream().max(Comparator.comparingInt(Point::x)).get().x() + " " + pointList.stream().max(Comparator.comparingInt(Point::x)).get().y());
        System.out.println(pointList.stream().min(Comparator.comparingInt(Point::x)).get().x() + " " + pointList.stream().min(Comparator.comparingInt(Point::x)).get().y());
        System.out.println(pointList.stream().max(Comparator.comparingInt(Point::y)).get().x() + " " + pointList.stream().max(Comparator.comparingInt(Point::y)).get().y());
        System.out.println(pointList.stream().min(Comparator.comparingInt(Point::y)).get().x() + " " + pointList.stream().min(Comparator.comparingInt(Point::y)).get().y());*/


        return new Point[]{
                pointList.stream().max(Comparator.comparingInt(Point::x)).get(),
                pointList.stream().min(Comparator.comparingInt(Point::x)).get(),
                pointList.stream().max(Comparator.comparingInt(Point::y)).get(),
                pointList.stream().min(Comparator.comparingInt(Point::y)).get()
        };
    }
}
