package com.kck.carddetection.utils;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.opencv_core.*;
import org.springframework.stereotype.Service;

@Service
public class ArrayUtils {

    public double sumArray(Mat mat) {
        float sum = (float) 0.0;
        UByteIndexer uByteIndexer = mat.createIndexer();
        int rows = mat.rows(), cols = mat.cols();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sum += uByteIndexer.get(i, j);
            }
        }
        return sum;
    }

}
