package com.kck.carddetection;

import com.kck.carddetection.services.ImageLoader;
import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ImageLoaderTest {

    @Autowired
    ImageLoader imageLoader;

    @BeforeAll
    public static void init(){
        File file = new File("src/test/resources/newImage.jpg");
        file.delete();
    }

    @Test
    public void shouldLoadAndSaveImage(){
        Mat mat = imageLoader.loadImage("src/main/resources/Spades.jpg");
        imageLoader.saveImage(mat, "src/test/resources/newImage.jpg");
        Mat mat2 = imageLoader.loadImage("src/test/resources/newImage.jpg");
        assertNotNull(mat);
        assertEquals(mat.toString(),mat2.toString());
    }
}
