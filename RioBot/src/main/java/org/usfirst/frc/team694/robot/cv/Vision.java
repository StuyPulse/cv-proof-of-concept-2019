package org.usfirst.frc.team694.robot.cv;

import org.opencv.core.Mat;

import stuyvision.VisionModule;

public class Vision extends VisionModule {

    @Override
    public void run(Mat frame) {
        postImage(frame, "Frame");
    }
}