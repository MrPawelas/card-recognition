package com.kck.carddetection.model;

import lombok.Data;
import org.bytedeco.opencv.opencv_core.*;

import java.util.ArrayList;

@Data
public class ExtractedCardsFromPictureModel {
    private ArrayList<Mat> cards;
    private Mat pictureWithContours;
    private ArrayList<Point> coordinatesOfTextsToPutOnCard;
}
