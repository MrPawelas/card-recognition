package com.kck.carddetection.service;

import com.kck.carddetection.model.Card;
import com.kck.carddetection.model.CardRank;
import com.kck.carddetection.model.CardSuit;
import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardMatcher {

    private final CardProcessor cardProcessor;

    public Card matchCard(Mat imageMatrix) {
        //todo porownac karte
        return new Card(CardRank.ace, CardSuit.diamonds);
    }

}
