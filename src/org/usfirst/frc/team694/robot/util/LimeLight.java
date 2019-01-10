/* Lime Light Docs: http://docs.limelightvision.io/en/latest/networktables_api.html# */

package org.usfirst.frc.team694.util;

import org.usfirst.frc.team694.util.NetworkTableClient;

// Using enums because random constants are unreadable
public enum LED_MODE {
  PIPELINE(0),    // Use LED mode set in pipeline 
  FORCE_OFF(1),   // Force LEDs off
  FORCE_BLINK(2), // Force LEDs to blink
  FORCE_ON(3);    // Force LEDs on 

  LED_MODE(int value) { this.val = value; }
  public int getCodeValue() { return val; }
  private int val;
};

public enum CAM_MODE {
  VISION(0), // Use limelight for CV
  DRIVER(1); // Use limelight for driving (this is dumb, dont do this)

  LED_MODE(int value) { this.val = value; }
  public int getCodeValue() { return val; }
  private int val;
};

public enum STREAM { // PIP = Picture-In-Picture
  STANDARD(0), PIP_MAIN(1), PIP_SECONDARY(2);

  LED_MODE(int value) { this.val = value; }
  public int getCodeValue() { return val; }
  private int val;
};

public enum SNAPSHOT_MODE {
  STOP(0), // Don't take snapshots
  TAKE_TWO_PER_SECOND(1); // Take two snapshots per second
  
  LED_MODE(int value) { this.val = value; }
  public int getCodeValue() { return val; }
  private int val;
};

class LimeLight {
  // Network Table used to contact Lime Light
  private static NetworkTableClient table = new NetworkTableClient("limelight");

  /* “Best” Contour information */
  // Whether the limelight has any valid targets (0 or 1)
  public static boolean hasValidTarget() {
    // == 1 converts double to boolean
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
  public static double getTargetYOffset() {
    return table.getDouble("ty");
  }

  // Target Area (0% of image to 100% of image)
  public static final double MIN_TARGET_AREA = 0;
  public static final double MAX_TARGET_AREA = 1;
  public static double getTargetArea() {
    // Lime light returns a double from 0 - 100
    // Divide by 100 to scale number from 0 - 1
    return table.getDouble("ta") / 100.0;
  }

  // Skew or rotation (-90 degrees to 0 degrees)
  public static final double MIN_SKEW = -90;
  public static final double MAX_SKEW = 0;
  public static double getSkew() {
    return table.getDouble("ts");
  }

  // The pipeline’s latency contribution (ms) Add at
  // least 11ms for image capture latency.
  public static final double IMAGE_CAPTURE_LATENCY = 11;
  public static double getLatency() {
    // Add Image Capture Latency to 
    // get more accurate result
    return table.getDouble("tl") + IMAGE_CAPTURE_LATENCY;
  }

  /* Camera Controls (Use Enums to prevent invalid inputs) */
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
    // Prevent input of invalid pipelines
    if(pipeline >= 0 && pipeline <= 9) { 
      table.setNumber("pipeline", pipeline);
    }
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
    table.setNumber("snapshot", snapshot.getCodeValue());
  }

  /* Advanced Usage with Raw Contours */
  /* Raw Targets */

  // Raw Contours are formatted as tx0, ty0, tx1, ty1, tx2, ty2
  // So to make this easier, you pass an int and it formats it

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
    // Lime light returns a double from 0 - 100
    // Divide by 100 to scale number from 0 - 1
    return table.getDouble("ta" + Integer.toString(Target)) / 100.0;
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
