package org.usfirst.frc.team694.robot.cv;

import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import stuyvision.VisionModule;
import stuyvision.gui.IntegerSliderVariable;

public class Vision extends VisionModule {

    public IntegerSliderVariable minHue = new IntegerSliderVariable("Min Hue", 0, 0, 255);
    public IntegerSliderVariable maxHue = new IntegerSliderVariable("Max Hue", 0, 0, 255);
    public IntegerSliderVariable minSaturation = new IntegerSliderVariable("Min Saturation", 0, 0, 255);
    public IntegerSliderVariable maxSaturation = new IntegerSliderVariable("Max Saturation", 0, 0, 255);

    RotatedRect left = new RotatedRect();
    RotatedRect right = new RotatedRect();

    @Override
    public void run(Mat frame) {
        
        Mat contourClone = frame.clone();
        Mat rectClone = frame.clone();

        postImage(frame, "Frame");
        
        Imgproc.cvtColor(frame,frame,Imgproc.COLOR_BGR2HSV);
        
        ArrayList<Mat> channels = new ArrayList<Mat>();
        Core.split(frame, channels);
        
        Mat hue = new Mat();
        Core.inRange(channels.get(0), new Scalar(minHue.value()), new Scalar(maxHue.value()),hue);
        //postImage(hue, "Hue");
        
        Mat saturation = new Mat();
        Core.inRange(channels.get(1), new Scalar(minSaturation.value()), new Scalar(maxSaturation.value()),saturation);
        //postImage(saturation, "Saturation");
        
        Mat filtered = new Mat();
        Core.bitwise_and(hue, saturation, filtered);
        postImage(filtered, "Filtered Image");

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(filtered, filtered, kernel);
        postImage(filtered, "Eroded");
        
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(filtered, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(contourClone, contours, -1, new Scalar(0, 255, 0), 2);
        postImage(contourClone, "Contours");

        for (MatOfPoint x : contours) {
            RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(x.toArray()));
            Point[] vertices = new Point[4];
            rotatedRect.points(vertices);
            MatOfPoint points = new MatOfPoint(vertices);
            //Imgproc.drawContours(rectClone, Arrays.asList(points),-1, new Scalar(0, 255, 0), 2);
           
            //https://namkeenman.wordpress.com/2015/12/18/open-cv-determine-angle-of-rotatedrect-minarearect/
            //Check above site to see how angle works.

            if (rotatedRect.angle < -45) {
                left = rotatedRect;            
                Imgproc.drawContours(rectClone, Arrays.asList(points),-1, new Scalar(255, 0, 0), 2);
                Imgproc.putText(rectClone, rotatedRect.angle + "", rotatedRect.center, Core.FONT_HERSHEY_COMPLEX, 1, new Scalar(0, 0, 0));
            } else {
                right = rotatedRect;
                Imgproc.drawContours(rectClone, Arrays.asList(points),-1, new Scalar(0, 255, 0), 2);
            }
        }
        if (left.center.x < right.center.x) {
            System.out.println("Correct spot");
        } else {
            System.out.println("Between targets");
        }
        postImage(rectClone, "Rect");
    }
}