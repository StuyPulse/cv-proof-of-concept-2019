/* Lime Light Docs: http://docs.limelightvision.io/en/latest/networktables_api.html# */

package org.usfirst.frc.team694.util;

import org.usfirst.frc.team694.util.NetworkTableClient;

public enum LED_MODE {
  PIPELINE(0), FORCE_OFF(1), FORCE_BLINK(2), FORCE_ON(3);

  private int codeValue;

  LED_MODE(int value) { this.codeValue = value; }
  public int getCodeValue() { return codeValue; }
};

public enum CAM_MODE {
  VISION(0), DRIVER(1);

  private int codeValue;

  CAM_MODE(int value) { this.codeValue = value; }
  public int getCodeValue() { return codeValue; }
};

public enum STREAM { // PIP = Picture-In-Picture
  STANDARD(0), PIP_MAIN(1), PIP_SECONDARY(2);

  private int codeValue;

  STREAM_MODE(int value) { this.codeValue = value; }
  public int getCodeValue() { return codeValue; }
};

public enum SNAPSHOT_MODE {
  STOP(0), TAKE_TWO_PER_SECOND(1);

  private int codeValue;

  SNAPSHOT_MODE(int value) { this.codeValue = value; }
  public int getCodeValue() { return codeValue; }
};

class LimeLight {
  // Network Table used to contact Lime Light
  public static NetworkTableClient table = new NetworkTableClient("limelight");

  /* “Best” Contour information */
  // Whether the limelight has any valid targets (0 or 1)
  public static boolean hasValidTarget() {
    return table.getDouble("tv") == 1;
  }

  // Horizontal Offset From Crosshair To Target (-27 degrees to 27 degrees)
  public static final double MIN_X_OFFSET = -27;
  public static final double MAX_X_OFFSET = 27;
  public static double getTargetXOffset() {
    return table.getDouble("tx");
  }

  // Vertical Offset From Crosshair To Target (-20.5 degrees to 20.5 degrees)
  public static final double MIN_Y_OFFSET = -20.5;
  public static final double MAX_Y_OFFSET = 20.5;
  public static double getTY() {
    return table.getDouble("ty");
  }

  // Target Area (0% of image to 100% of image)
  public static final double MIN_TARGET_AREA = 0;
  public static final double MAX_TARGET_AREA = 100;
  public static double getTargetArea() {
    return table.getDouble("ta");
  }

  // Skew or rotation (-90 degrees to 0 degrees)
  public static final double MIN_SKEW = -90;
  public static final double MAX_SKEW = 0;
  public static double getSkew() {
    return table.getDouble("ts");
  }

  // The pipeline’s latency contribution (ms) Add at
  // least 11ms for image capture latency.
  public static final double DEFAULT_LATENCY = 11;
  public static double getLatency() {
    return table.getDouble("tl") + DEFAULT_LATENCY;
  }

  /* Camera Controls */
  // ledMode  |	Sets limelight’s LED state
  // 0        |	use the LED Mode set in the current pipeline
  // 1        |	force off
  // 2        |	force blink
  // 3        |	force on
  public static void setLEDMode(LED_MODE mode) {
    table.setNumber("ledMode", mode.getCodeValue());
  }

  // camMode  | Sets limelight’s operation mode
  // ---------+--------------------------------
  // 0        |	Vision processor
  // 1        |	Driver Camera (Increases exposure, disables vision processing)
  public static void setCamMode(CAM_MODE mode) {
    table.setNumber("camMode", mode.getValueCode());
  }

  // pipeline |	Sets limelight’s current pipeline
  // ---------+----------------------------------
  // 0..9     |	Select pipeline 0..9
  public static void setPipeline(int pipeline) {
    if(pipeline >= 0 && pipeline <= 9) table.setNumber("pipeline", pipeline);
  }

  // stream   |	Sets limelight’s streaming mode
  // ---------+--------------------------------
  // 0        |	Standard - Side-by-side streams if a webcam is attached to Limelight
  // 1        |	PiP Main - The secondary camera stream is placed in the lower-right corner of the primary camera stream
  // 2        |	PiP Secondary - The primary camera stream is placed in the lower-right corner of the secondary camera stream
  public static void setStream(STREAM stream) {
    table.setNumber("stream", stream.getCodeValue());
  }

  // snapshot |	Allows users to take snapshots during a match
  // ---------+----------------------------------------------
  // 0        |	Stop taking snapshots
  // 1        |	Take two snapshots per second
  public static void setSnapshotMode(SNAPSHOT_MODE snapshot) {
    table.setNumber("stream", snapshot.getCodeValue());
  }

  /* Advanced Usage with Raw Contours */
  /* Raw Targets */
  // Raw Screenspace X
  public static double getTX(int Target) {
    return table.getDouble("tx" + Integer.toString(Target));
  }

  // Raw Screenspace Y
  public static double getTY(int Target) {
    return table.getDouble("ty" + Integer.toString(Target));
  }

  // Area (0% of image to 100% of image)
  public static double getTA(int Target) {
    return table.getDouble("ta" + Integer.toString(Target));
  }

  // Skew or rotation (-90 degrees to 0 degrees)
  public static double getTS(int Target) {
    return table.getDouble("ts" + Integer.toString(Target));
  }

  /* Raw Crosshairs */
  // Crosshair A X in normalized screen space
  public static double getCX(int crosshair) {
    return table.getDouble("cx" + Integer.toString(crosshair));
  }

  // Crosshair A Y in normalized screen space
  public static double getCY(int crosshair) {
    return table.getDouble("cy" + Integer.toString(crosshair));
  }
}
