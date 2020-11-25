package com.kck.carddetection.service;

import com.kck.carddetection.componnent.CardTemplateGiver;
import com.kck.carddetection.model.Card;
import com.kck.carddetection.model.CardRank;
import com.kck.carddetection.utils.ArrayUtils;
import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Service;

import static org.bytedeco.opencv.global.opencv_core.*;

@Service
@RequiredArgsConstructor
public class CardMatcher {

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
        }

        return new Card(lowestRank, null);
    }

    private double calcMatch(Mat imageMatrix, Mat template) {
        Mat img = matrixProcessor.blackAndWhiteImage(imageMatrix);
        Mat templ = matrixProcessor.blackAndWhiteImage(template);
        Mat diff = new Mat();

        absdiff(img, templ, diff);

        return arrayUtils.sumArray(diff);
    }

}
