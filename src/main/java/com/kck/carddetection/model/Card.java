package com.kck.carddetection.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Card {
    private CardRank cardRank;
    private CardSuit cardSuit;
}
