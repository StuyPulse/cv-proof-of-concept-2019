package frc.robot.cv;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import stuyvision.capture.DeviceCaptureSource;

public class FilterVision {

    RotatedRect left = new RotatedRect();
    RotatedRect right = new RotatedRect();

    Point[] leftPoints = new Point[4];
    Point[] rightPoints = new Point[4];

    MatOfPoint leftPointsMat;
    MatOfPoint rightPointsMat;

    double startTime;

    public ArrayList<Double> filter(DeviceCaptureSource cam) {

        // System.out.println("Bumper pressed");
        LocalDateTime time = LocalDateTime.now();
        String localtime = time.toString();
        Mat frame = Camera.getImage(cam);
        ArrayList<Double> cont = new ArrayList<Double>();

        if (frame == null) {
            System.out.println("Failed to read from camera");
            cont.add(100000000.0);
            cont.add(100000000.0);
            return cont;
        } else {
            // Imgcodecs.imwrite("/tmp/" + localtime + ".png", frame);
            // System.out.println("Succeeded in reading from camera");

            Mat contourClone = frame.clone();
            Mat rectClone = frame.clone();

            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);

            ArrayList<Mat> channels = new ArrayList<Mat>();
            Core.split(frame, channels);

            Mat hue = new Mat();
            Core.inRange(channels.get(0), new Scalar(58), new Scalar(96), hue);
            // Imgcodecs.imwrite("/tmp/" + localtime + "hue.png", hue);

            Mat value = new Mat();
            Core.inRange(channels.get(2), new Scalar(66), new Scalar(255), value);
            // Imgcodecs.imwrite("/tmp/" + localtime + "val.png", value);

            Mat filtered = new Mat();
            Core.bitwise_and(hue, value, filtered);

            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
            Imgproc.erode(filtered, filtered, kernel);

            // Imgcodecs.imwrite("/tmp/" + localtime + "filtered.png", filtered);

            Mat hierarchy = new Mat();

            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(filtered, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            Imgproc.drawContours(contourClone, contours, -1, new Scalar(0, 255, 0), 2);
            // Imgcodecs.imwrite("/tmp/" + localtime + "contours.png", contourClone);

            if (contours.size() >= 2) {
                contours.sort(new Comparator<MatOfPoint>() {
                    @Override
                    public int compare(MatOfPoint m1, MatOfPoint m2) {
                        RotatedRect rect1 = Imgproc.minAreaRect(new MatOfPoint2f(m1.toArray()));
                        RotatedRect rect2 = Imgproc.minAreaRect(new MatOfPoint2f(m2.toArray()));

                        if (rect1.size.area() < rect2.size.area()) {
                            return -1;
                        } else if (rect2.size.area() > rect1.size.area()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });

                Collections.reverse(contours);

                RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(0).toArray()));
                if (rect.angle < -45) {
                    left = rect;
                    right = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(1).toArray()));
                } else {
                    right = rect;
                    left = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(1).toArray()));
                }

                left.points(leftPoints);
                right.points(rightPoints);

                leftPointsMat = new MatOfPoint(leftPoints);
                rightPointsMat = new MatOfPoint(rightPoints);

                Imgproc.drawContours(rectClone, Arrays.asList(leftPointsMat), -1, new Scalar(255, 0, 0), 2);
                Imgproc.drawContours(rectClone, Arrays.asList(rightPointsMat), -1, new Scalar(0, 255, 0), 2);

                if (left.center.x < right.center.x) {
                    // System.out.println("Correct spot");
                } else {
                    System.out.println("Between targets");
                }
                // Imgcodecs.imwrite("/tmp/" + localtime + "rect.png", rectClone);

                Point[] vertices = new Point[4];
                RotatedRect overallRect = Imgproc.minAreaRect(new MatOfPoint2f(leftPoints[0], leftPoints[1],
                        leftPoints[2], leftPoints[3], rightPoints[0], rightPoints[1], rightPoints[2], rightPoints[3]));
                overallRect.points(vertices);
                MatOfPoint points = new MatOfPoint(vertices);
                Imgproc.drawContours(rectClone, Arrays.asList(points), -1, new Scalar(0, 0, 255));

                Imgproc.circle(rectClone, new Point(frame.width() / 2, frame.height() / 2), 3, new Scalar(0, 0, 255),
                        2);

                Imgproc.circle(rectClone, overallRect.center, 3, new Scalar(255, 255, 0), 2);

                // Imgcodecs.imwrite("/tmp/" + localtime + "overall.png", rectClone);

                double turn = getTurn(rectClone, overallRect);
                double area = overallRect.size.area();

                cont.add(turn);
                cont.add(area);

                frame.release();
                value.release();
                hue.release();
                filtered.release();
                kernel.release();
                contourClone.release();
                rectClone.release();
                hierarchy.release();
                leftPointsMat.release();
                rightPointsMat.release();

                return cont;

            } else {
                System.out.println("No target found");
                // Imgcodecs.imwrite("/tmp/" + localtime + "nothing.png", rectClone);
            }

            frame.release();
            value.release();
            hue.release();
            filtered.release();
            kernel.release();
            contourClone.release();
            rectClone.release();
            hierarchy.release();

            for (int i = 0; i < contours.size(); i++) {
                contours.get(i).release();
            }

        }
        frame.release();
        cont.add(10000000.0);
        cont.add(10000000.0);
        return cont;
    }

    public double getTurn(Mat frame, RotatedRect rect) {
        Point centerPoint = new Point(frame.width() / 2, frame.height() / 2);
        return (rect.center.x - centerPoint.x);
    }
}