package com.kck.carddetection.service;

import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

@Service
@RequiredArgsConstructor
public class ImageLoader {

    public Mat loadImage(String imagePath) {
        return imread(imagePath, IMREAD_COLOR);
    }

    public void saveImage(Mat imageMatrix, String targetPath) {
        imwrite(targetPath, imageMatrix);
    }

}
