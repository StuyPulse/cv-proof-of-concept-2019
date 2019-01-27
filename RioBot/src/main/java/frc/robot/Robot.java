/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.cv.Camera;
import frc.robot.cv.FilterVision;
import stuyvision.capture.DeviceCaptureSource;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private static FilterVision vision;
  private static OI oi;
  private static DeviceCaptureSource cam;

  private WPI_TalonSRX leftFrontMotor;
	private WPI_TalonSRX rightFrontMotor;
	private WPI_TalonSRX leftRearMotor;
	private WPI_TalonSRX rightRearMotor;

	private SpeedControllerGroup leftSpeedController;
	private SpeedControllerGroup rightSpeedController;

  private DifferentialDrive differentialDrive;
  
  private final double TURN_DIV = 190;
  private final double TARGET_AREA = 3500;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    rightFrontMotor = new WPI_TalonSRX(3);
		rightRearMotor = new WPI_TalonSRX(2);
		leftFrontMotor = new WPI_TalonSRX(1);
		leftRearMotor = new WPI_TalonSRX(4);

		rightFrontMotor.setInverted(true);
		rightRearMotor.setInverted(true);
		leftFrontMotor.setInverted(true);
		leftRearMotor.setInverted(true);

		rightSpeedController = new SpeedControllerGroup(rightFrontMotor, rightRearMotor);
    leftSpeedController = new SpeedControllerGroup(leftFrontMotor, leftRearMotor);
    
    differentialDrive = new DifferentialDrive(leftSpeedController, rightSpeedController);

    oi = new OI();
    cam = Camera.initializeCamera(0);

    //CameraServer.getInstance().startAutomaticCapture(0);

    vision = new FilterVision();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  @Override
  public void disabledInit() {
    super.disabledInit();
  }

  @Override
  public void disabledPeriodic() {
    super.disabledPeriodic();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  private double capValue(double input) {
    return Math.min(Math.max(input, -1), 1);
  }
  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    Scheduler.getInstance().run();
	  //System.out.println(System.getProperty("java.library.path"));
		//ModuleRunner runner = new ModuleRunner(5);
    if (oi.gamepad.getRawLeftBumper()) {
      System.out.println("Left bumper pressed");
      double x = (vision.filter(cam)).get(0);
      double TURN_VAL = capValue(x / TURN_DIV);

      if (10 < Math.abs(x) && (Math.abs(x) < 100000)) {
        System.out.println(x);
        differentialDrive.curvatureDrive(0, TURN_VAL, true);
      }
    } 
    
    if (oi.gamepad.getRawRightBumper()) {
      System.out.println("Right bumper pressed");
      double area = vision.filter(cam).get(1);
      
      if (area > 0 && area < 10000000) {
        if (Math.abs(TARGET_AREA - area)  >= 1000) {
          double sign = Math.signum(TARGET_AREA - area);
          System.out.println(area + ", " + sign);
          differentialDrive.tankDrive(sign * 0.5, sign * 0.5);
          System.out.println("Moving");
        } else {
          differentialDrive.tankDrive(0, 0);
        }
      }
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
