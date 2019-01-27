package frc.robot.cv;

import org.opencv.videoio.VideoCapture;

import stuyvision.ModuleRunner;
import stuyvision.capture.DeviceCaptureSource;
import stuyvision.capture.ImageCaptureSource;
import stuyvision.gui.VisionGui;

public class Main {
    public static void main(String[] args) {
        System.out.println(System.getProperty("java.library.path"));
        ModuleRunner runner = new ModuleRunner(5);
        //DeviceCaptureSource cam = Camera.initializeCamera(0);
        //runner.addMapping(cam, new Vision());
        runner.addMapping(new ImageCaptureSource("E:/test13.png"), new Vision());
        VisionGui.begin(args, runner);
    }
}