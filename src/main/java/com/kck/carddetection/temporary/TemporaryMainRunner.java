package com.kck.carddetection.temporary;

import com.kck.carddetection.services.ImageLoader;
import com.kck.carddetection.services.MatrixProcessor;
import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemporaryMainRunner {

    private final ImageLoader imageLoader;
    private final MatrixProcessor matrixProcessor;

    //todo to tylko tymczasowa klasa gdzie mozemy sobie testowac w mainie rzeczy
    //todo docelowo zrobi sie jakis serwis ktory w tle bedzie chodzil i czytal zdjecia z jakiegos folderu i wypluwal do innego folderu output

    public void main() {
        Mat mat = imageLoader.loadImage("src/main/resources/Spades.jpg");
        mat = matrixProcessor.resizeImage(mat, 500, 500);
        mat = matrixProcessor.grayImage(mat);
        imageLoader.saveImage(mat, "src/main/resources/newImage.jpg");
    }
}
