package com.kck.carddetection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class Card {
    private CardRank cardRank;
    private CardSuit cardSuit;
}
