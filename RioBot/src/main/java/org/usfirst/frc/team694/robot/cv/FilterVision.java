package org.usfirst.frc.team694.robot.cv;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import stuyvision.capture.DeviceCaptureSource;

public class FilterVision {

    public void filter(DeviceCaptureSource cam) {

        System.out.println("Bumper pressed");
        LocalDateTime time = LocalDateTime.now();
        String localtime = time.toString();
        Mat frame = Camera.getImage(cam);

        if (frame == null) {
            System.out.println("Failed to read from camera");
        } else {
        Imgcodecs.imwrite("/tmp/" + localtime + ".png", frame);
        System.out.println("Succeeded in reading from camera");

        Mat contourClone = frame.clone();
        Mat rectClone = frame.clone();

        Imgproc.cvtColor(frame,frame,Imgproc.COLOR_BGR2HSV);

        ArrayList<Mat> channels = new ArrayList<Mat>();
        Core.split(frame, channels);

        Mat hue = new Mat();
        Core.inRange(channels.get(0), new Scalar(80), new Scalar(104), hue);
        //Imgcodecs.imwrite("/tmp/" + localtime + "hue.png", hue);

        Mat saturation = new Mat();
        Core.inRange(channels.get(1), new Scalar(199), new Scalar(255), saturation);
        //Imgcodecs.imwrite("/tmp/" + localtime + "sat.png", saturation);

        Mat filtered = new Mat();
        Core.bitwise_and(hue, saturation, filtered);
        Imgcodecs.imwrite("/tmp/" + localtime + "filtered.png", filtered);

        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(filtered, contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(contourClone, contours, -1, new Scalar(0, 255, 0), 1);
        Imgcodecs.imwrite("/tmp/" + localtime + "contours.png", filtered);

            for (MatOfPoint x : contours) {
            RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(x.toArray()));
            Point[] vertices = new Point[4];
            rotatedRect.points(vertices);
            MatOfPoint points = new MatOfPoint(vertices);
            Imgproc.drawContours(rectClone, Arrays.asList(points),-1, new Scalar(0, 255, 0), 2);
            }
            Imgcodecs.imwrite("/tmp/" + localtime + "rect.png", rectClone);
        }
        frame.release();
}
}