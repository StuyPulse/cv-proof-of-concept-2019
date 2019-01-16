/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// import edu.wpi.first.networktables.NetworkTable;
// import edu.wpi.first.networktables.NetworkTableEntry;
// import edu.wpi.first.networktables.NetworkTableInstance;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import frc.util.Gamepad;

import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.ExampleSubsystem;
import frc.util.LimeLight;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	public static ExampleSubsystem m_subsystem = new ExampleSubsystem();
	//public NetworkTable table;
	//NetworkTableClient client;
	Command m_autonomousCommand;
	SendableChooser<Command> m_chooser = new SendableChooser<>();

	private WPI_TalonSRX leftFrontMotor;
	private WPI_TalonSRX rightFrontMotor;
	private WPI_TalonSRX leftRearMotor;
	private WPI_TalonSRX rightRearMotor;

    private SpeedControllerGroup leftSpeedController;
	private SpeedControllerGroup rightSpeedController;

	private DifferentialDrive differentialDrive;

	// Changes the speed that the robot will turn
	// DO NOT set lower than 30
	private final double SPEED_DIV = 16;
	private final double SPEED_DIV_MOVE = SPEED_DIV*4;
	private final double FORWARD_AREA = 0.006;
	private final double BACKWARD_AREA = 0.01;
	private final boolean MOVE = true;

	Gamepad controller;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		// table = NetworkTableInstance.getDefault().getTable("limelight");

		m_chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", m_chooser);
		//client = new NetworkTableClient("limelight");

		// Initialize Motors
		leftFrontMotor = new WPI_TalonSRX(1);
		rightFrontMotor = new WPI_TalonSRX(2);
		leftRearMotor = new WPI_TalonSRX(3);
		rightRearMotor = new WPI_TalonSRX(4);

		// Motors were built backwards
		leftFrontMotor.setInverted(true);
        rightFrontMotor.setInverted(true);
        leftRearMotor.setInverted(true);
		rightRearMotor.setInverted(true);

		// Group Motors
		leftSpeedController = new SpeedControllerGroup(leftFrontMotor, leftRearMotor);
		rightSpeedController = new SpeedControllerGroup(rightFrontMotor, rightRearMotor);

		// Make differential drive from motor groups
		differentialDrive = new DifferentialDrive(leftSpeedController, rightSpeedController);

		// Be able to read from controller
		controller = new Gamepad(0 /* TODO: find port number*/);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		m_autonomousCommand = m_chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (m_autonomousCommand != null) {
			m_autonomousCommand.start();
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
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

		// Recieve data from lime light
		double x = LimeLight.getTargetXOffset();
		double y = LimeLight.getTargetYOffset();
		double area = LimeLight.getTargetArea();

		// Post to smart dashboard periodically 
		SmartDashboard.putNumber("LimelightX", x);
		SmartDashboard.putNumber("LimelightY", y);
		SmartDashboard.putNumber("LimelightArea", area);
		SmartDashboard.putNumber("Turn Value", x/SPEED_DIV);

		// If area is too big, its too close, move backwards
		if(area > BACKWARD_AREA && controller.getRawRightButton()) {
			SmartDashboard.putString("Driving Status", "Backwards");
			differentialDrive.tankDrive(-0.75, -0.75);
		} 
		// If area is too small, its too far, move forward
		else if (area < FORWARD_AREA && controller.getRawRightButton()) {
			SmartDashboard.putString("Driving Status", "Forwards");
			differentialDrive.tankDrive(0.75, 0.75);
		} 
		// Turn the tank drive
		else if(controller.getRawBottomButton()) {
			SmartDashboard.putString("Driving Status", "Turning (" + x/SPEED_DIV + ")");
			differentialDrive.tankDrive(capValue(x/SPEED_DIV), capValue(-x/SPEED_DIV));
		} else {
			// Disable Tank Drive
			differentialDrive.tankDrive(0, 0);
			SmartDashboard.putString("Driving Status", "Disabled");
		}

		/* Backup Code */
		//NetworkTableEntry tx = table.getEntry("tx");
		//NetworkTableEntry ty = table.getEntry("ty");
		//NetworkTableEntry ta = table.getEntry("ta");
		
		//read values periodically
		//double x = tx.getDouble(0.0);
		//double y = ty.getDouble(0.0);
		//double area = ta.getDouble(0.0);
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
