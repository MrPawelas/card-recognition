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

//toDo chyba mozna wywalic

@Service
@RequiredArgsConstructor
public class CardProcessor {

    private final CardExtractor cardExtractor;

    public static double probabilityOfRectangle(Mat contour) {
        Mat boxMat = new Mat();
        RotatedRect rotatedRect = minAreaRect(contour);
        boxPoints(rotatedRect, boxMat);
        FloatRawIndexer uByteIndexer = boxMat.createIndexer();
        int rows = boxMat.rows(), cols = boxMat.cols();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                uByteIndexer.put(i, j, Math.round(uByteIndexer.get(i, j)));
            }
        }
        return contourArea(contour) / contourArea(boxMat);
    }

    //todo rozbic na dwie metody
    public ArrayList<Mat> extractCardsFromPicture(Mat imageMatrix, Mat originalImage) {
        Mat hierarchy = new Mat();
        ArrayList<Mat> matArrayList = new ArrayList<>();
        Mat result = originalImage.clone();
        matArrayList.add(result);

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
                //System.out.println("rect");
            }
        }

        ArrayList<Scalar> colors = new ArrayList<>();
        addExampleColors(colors);

        List<Mat> mats = Arrays.asList(cntfiltered.get()); //taki tam odpowiednik enumerate w javie xd
        ListIterator<Mat> it = mats.listIterator();

        while (it.hasNext()) {
            int index = it.nextIndex();
            it.next();
            drawContours(result, cntfiltered, index, colors.get(index), 10, 10, hierarchy, 0, new Point(0, 0));
            matArrayList.add(cardExtractor.extractSubImage(cntfiltered.get(index), originalImage));
        }
        return matArrayList;
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


}
