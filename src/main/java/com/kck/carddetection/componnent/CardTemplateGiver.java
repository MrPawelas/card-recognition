package com.kck.carddetection.componnent;

import com.kck.carddetection.model.CardRank;
import com.kck.carddetection.model.CardSuit;
import com.kck.carddetection.service.ImageLoader;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
@Data
@RequiredArgsConstructor
public class CardTemplateGiver {
    private final ImageLoader imageLoader;
    private HashMap<CardRank, Mat> rankMap = new HashMap<>();
    private HashMap<CardSuit, Mat> suitMap = new HashMap<>();

    @PostConstruct
    public void init() {
        loadSuitsAndRanks();
    }

    public Mat getCardRankMatrix(CardRank cardRank) {
        return rankMap.get(cardRank);
    }


    public Mat getCardSuitMatrix(CardSuit cardSuit) {
        return suitMap.get(cardSuit);
    }

    private void loadSuitsAndRanks() {
        for (CardRank rank : CardRank.values()) {
            rankMap.put(rank, loadRank(rank));
        }

        for (CardSuit suit : CardSuit.values()) {
            suitMap.put(suit, loadSuit(suit));
        }
    }

    private Mat loadRank(CardRank cardRank) {
        return imageLoader.loadImage("src/main/resources/ranks/" + cardRank + ".jpg");
    }

    private Mat loadSuit(CardSuit cardSuit) {
        return imageLoader.loadImage("src/main/resources/suits/" + cardSuit + ".jpg");
    }

}
