package com.kck.carddetection.service;

import lombok.RequiredArgsConstructor;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

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
            //todo I guess usunąc caly ten kod ? :(
//            Mat mat = it.next();
//            RotatedRect rotatedRect = minAreaRect(mat);
//            Mat boxMat = new Mat();
//            boxPoints(rotatedRect, boxMat);
//            Mat boxMatIntegers = new Mat();
//
//            int rows = boxMat.rows(), cols = boxMat.cols();
//            boxMatIntegers.create(rows, cols, CvType.CV_32S);
//
//            FloatRawIndexer floatRawIndexer = boxMat.createIndexer();
//            IntRawIndexer intRawIndexer = boxMatIntegers.createIndexer();
//            for (int i = 0; i < rows; i++) {
//                for (int j = 0; j < cols; j++) {
//                    intRawIndexer.put(i, j, Math.round(floatRawIndexer.get(i, j)));
//                }
//            }
//
//            boxVector.push_back(boxMatIntegers);
            drawContours(result, cntfiltered, index, colors.get(index));
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


}
