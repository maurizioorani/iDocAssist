package com.marsk.docassist.util;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.javacv.Frame;

import java.awt.image.BufferedImage;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class ImagePreprocessor {

    /**
     * Preprocesses image for better OCR results
     * @param image Input image to process
     * @return Processed BufferedImage
     */
    public static BufferedImage preprocessForOcr(BufferedImage image) {
        // Convert BufferedImage to OpenCV Mat
        Mat mat = BufferedImageToMat(image);
        
        // Apply preprocessing pipeline
        mat = binarize(mat);
        mat = removeNoise(mat);
        mat = deskew(mat);
        mat = resize(mat);
        
        // Convert back to BufferedImage
        return matToBufferedImage(mat);
    }

    private static Mat binarize(Mat mat) {
        Mat gray = new Mat();
        Mat binary = new Mat();
        
        // Convert to grayscale if needed
        if (mat.channels() > 1) {
            opencv_imgproc.cvtColor(mat, gray, COLOR_BGR2GRAY);
        } else {
            gray = mat.clone();
        }
        
        // Apply Gaussian blur for noise reduction
        opencv_imgproc.GaussianBlur(gray, gray, new Size(3, 3), 0);
        
        // Apply adaptive thresholding with optimized parameters
        opencv_imgproc.adaptiveThreshold(gray, binary, 255,
            ADAPTIVE_THRESH_GAUSSIAN_C,
            THRESH_BINARY, 11, 2);
            
        return binary;
    }

    private static Mat removeNoise(Mat mat) {
        Mat denoised = new Mat();
        // Median blur is more effective for salt-and-pepper noise in documents
        opencv_imgproc.medianBlur(mat, denoised, 5);
        return denoised;
    }

    private static Mat deskew(Mat mat) {
        // TODO: Implement deskewing logic
        return mat;
    }

    private static Mat resize(Mat mat) {
        Mat resized = new Mat();
        opencv_imgproc.resize(mat, resized, new Size(0, 0), 2.0, 2.0, INTER_CUBIC);
        return resized;
    }

    // Helper methods for BufferedImage ↔ Mat conversion
    private static Mat BufferedImageToMat(BufferedImage image) {
        return Java2DFrameUtils.toMat(image);
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        Frame frame = new Frame(mat.cols(), mat.rows(),
            Frame.DEPTH_UBYTE, mat.channels());
        frame.image[0] = mat.createBuffer();
        return Java2DFrameUtils.toBufferedImage(frame);
    }
}