package com.kck.carddetection.service;

import lombok.RequiredArgsConstructor;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;


@Service
@RequiredArgsConstructor
public class CardProcessor {

    private final MatrixProcessor matrixProcessor;
    Mat result;

    //todo rozbic na dwie metody
    public Mat extractCardsFromPicture(Mat imageMatrix) {
        Mat hierarchy = new Mat();
        result = new Mat(imageMatrix.rows(), imageMatrix.cols(), CV_8UC1);

        MatVector contoursVec = new MatVector(); //to jest vector z c++ a nie że wektor jako jeden wiersz macierzy
        findContours(imageMatrix, contoursVec, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);

        MatVector cntfiltered = new MatVector();

        for (Mat mat : contoursVec.get()) {
            if (contourArea(mat) > 1000 && probabilityOfRectangle(mat) > 0.9) {
                System.out.println("rectangle!");
                cntfiltered.push_back(mat);
            }
        }

        ArrayList<Scalar> colors = new ArrayList<>();
        addExampleColors(colors);

        List<Mat> mats = Arrays.asList(cntfiltered.get()); //taki tam odpowiednik enumerate w javie xd
        ListIterator<Mat> it = mats.listIterator();
        while (it.hasNext()) {
            int index = it.nextIndex();
            Mat mat = it.next();
            RotatedRect rotatedRect = minAreaRect(mat);
            Mat boxMat = new Mat();
            boxPoints(rotatedRect, boxMat);

            FloatRawIndexer uByteIndexer = boxMat.createIndexer();
            int rows = boxMat.rows(), cols = boxMat.cols();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {

                    uByteIndexer.put(i, j, Math.round(uByteIndexer.get(i, j)));
                    //   System.out.println(uByteIndexer.get(i,j));
                }
            }
            System.out.println(contourArea(mat) / contourArea(boxMat));
            drawContours(result, cntfiltered, -1, Scalar.BLUE); //todo tu jest bug, moim zdaniem znajduje prostokąty tylko nie umie ich narysować.
            //todo jeżeli nie uda sie go pokonac to bedzie trzeba przejsc na innego wrappera opencv gdzie nie uzywa sie MatVector tylko liste punktow
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
        colors.add(new Scalar(34.0));
        colors.add(new Scalar(124.0));
        colors.add(new Scalar(74.0));
        colors.add(new Scalar(94.0));
        colors.add(new Scalar(154.0));
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


}
