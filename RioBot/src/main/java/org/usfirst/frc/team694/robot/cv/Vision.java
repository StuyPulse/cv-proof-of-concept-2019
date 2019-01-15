package org.usfirst.frc.team694.robot.cv;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import stuyvision.VisionModule;
import stuyvision.gui.IntegerSliderVariable;

public class Vision extends VisionModule {

    public IntegerSliderVariable minHue = new IntegerSliderVariable("Min Hue", 0, 0, 255);
    public IntegerSliderVariable maxHue = new IntegerSliderVariable("Max Hue", 0, 0, 255);
    public IntegerSliderVariable minSaturation = new IntegerSliderVariable("Min Saturation", 0, 0, 255);
    public IntegerSliderVariable maxSaturation = new IntegerSliderVariable("Max Saturation", 0, 0, 255);

    @Override
    public void run(Mat frame) {
        postImage(frame, "Frame");
        Imgproc.cvtColor(frame,frame,Imgproc.COLOR_BGR2HSV);
    	ArrayList<Mat> channels = new ArrayList<Mat>();
        Core.split(frame, channels);
        Mat hue = new Mat();
        Core.inRange(channels.get(0), new Scalar(minHue.value()), new Scalar(maxHue.value()),hue);
        postImage(hue, "Filtered");
        Mat saturation = new Mat();
        Core.inRange(channels.get(1), new Scalar(minSaturation.value()), new Scalar(maxSaturation.value()),saturation);
        postImage(saturation, "Saturation");
        Mat filtered = new Mat();
        Core.bitwise_and(hue, saturation, filtered);
        postImage(filtered, "Filtered Image");
    }
}