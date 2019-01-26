package frc.robot.cv;

import org.opencv.core.Mat;

import stuyvision.capture.DeviceCaptureSource;

public class Camera {

    private static final String pathToV4L2 = "/usr/bin/v4l2-ctl";

    public static boolean configureCamera(int port) {
        Runtime rt = Runtime.getRuntime();
        String cmdStart = pathToV4L2 + " -d " + port + " ";
        try {
            rt.exec(cmdStart
                    + "-c exposure_auto=1,exposure_absolute=5,brightness=30,contrast=10,saturation=200,white_balance_temperature_auto=0,sharpness=50")
                    .waitFor();
            rt.exec(cmdStart + "-c white_balance_temperature=4624").waitFor();
            System.out.println("Finished configuring");
            return true;
        } catch (Exception e) {
            System.err.println("Setting v4l settings crashed!");
            return false;
        }
    }

    public static DeviceCaptureSource initializeCamera(int cameraPort) {
        DeviceCaptureSource camera = new DeviceCaptureSource(cameraPort);
        System.out.println("Made camera at " + cameraPort);
        configureCamera(cameraPort);
        return camera;
    }

    public static Mat getImage(DeviceCaptureSource camera) {
        Mat raw = new Mat();
        Mat frame = new Mat();
        for (int i = 0; i < 5; i++) {
            if (!camera.readFrame(raw)) {
                return null;
            }
        }
        if (!camera.readSized(raw, frame)) {
            return null;
        }
        raw.release();
        return frame;
    }
}