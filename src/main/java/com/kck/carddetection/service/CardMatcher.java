package com.kck.carddetection.service;

import com.kck.carddetection.componnent.CardTemplateGiver;
import com.kck.carddetection.model.Card;
import com.kck.carddetection.model.CardRank;
import com.kck.carddetection.utils.ArrayUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Service;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
@RequiredArgsConstructor
public class CardMatcher {
    private final ContourGiver contourGiver;
    private final ArrayUtils arrayUtils;
    private final CardTemplateGiver cardTemplateGiver;
    private final MatrixProcessor matrixProcessor;

    public Card matchCard(Mat imageMatrix) {
        double lowestDiff = 1000000.0;
        CardRank lowestRank = null;

        for (CardRank rank : CardRank.values()) {
            double diff = calcMatch(imageMatrix, cardTemplateGiver.getCardRankMatrix(rank));
            // System.out.println(diff);

            if (diff < lowestDiff) {
                lowestDiff = diff;
                lowestRank = rank;
            }
             System.out.println("diff for card " + rank + "is " + diff);

        }
        contourGiver.contourToImage( contourGiver.getContours(imageMatrix));

        return new Card(lowestRank, null);
    }

    private double calcMatch(Mat imageMatrix, Mat template) {
        // System.out.println(imageMatrix);
        //imageMatrix = contourGiver.getContours(imageMatrix);
        //  System.out.println(imageMatrix);
        //   System.out.println(template);
        //template = contourGiver.getContours(template);
        //  System.out.println(template);
        //todo trzeba resizowac template do imagematrix zeby mozna bylo porownac kontury
        //matrixProcessor.resizeImage(template,imageMatrix.arrayWidth(),imageMatrix.arrayHeight());
        matrixProcessor.resizeImage(template,imageMatrix.cols(),imageMatrix.rows());
        double diff = 3;
         //Mat grayImage = matrixProcessor.grayImage(imageMatrix);
        //Mat grayTemplate = matrixProcessor.grayImage(template);
        //diff = matchShapes(grayImage, grayTemplate, 1, diff);
        return diff;
    }

}
