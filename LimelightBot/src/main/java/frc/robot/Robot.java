/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.drive.Vector2d;
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
	Command m_autonomousCommand;
	SendableChooser<Command> m_chooser = new SendableChooser<>();

	private WPI_TalonSRX leftFrontMotor;
	private WPI_TalonSRX rightFrontMotor;
	private WPI_TalonSRX leftRearMotor;
	private WPI_TalonSRX rightRearMotor;

	private SpeedControllerGroup leftSpeedController;
	private SpeedControllerGroup rightSpeedController;

	private DifferentialDrive differentialDrive;

	Gamepad controller;

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", new ExampleCommand());
		SmartDashboard.putData("Auto mode", m_chooser);

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

		// Make differential drive from motor groups
		differentialDrive = new DifferentialDrive(leftSpeedController, rightSpeedController);

		// Be able to read from controller
		controller = new Gamepad(0);

		// Set Up Limelight
		LimeLight.setCamMode(LimeLight.CAM_MODE.VISION);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode. You
	 * can use it to reset any subsystem information you want to clear when the
	 * robot is disabled.
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
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString code to get the
	 * auto name from the text box below the Gyro
	 *
	 * <p>
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons to
	 * the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		m_autonomousCommand = m_chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
		 * switch(autoSelected) { case "My Auto": autonomousCommand = new
		 * MyAutoCommand(); break; case "Default Auto": default: autonomousCommand = new
		 * ExampleCommand(); break; }
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

	/* MANUAL DRIVE VARIABLES */
	// Using averages, you can allow for smoother movement
	private final double ACCELERATION_DIV = 4;
	private double speed = 0; // Speed that stays the same through movements

	/* AIM ASSIST */
	// Changes the speed that the robot will turn
	private final double TURN_DIV = 24;

	// Changes the speed that the robot will turn
	private final double MOVE_DIV = 2;

	/* AUTO ACCELERATE VARIABLES */
	// Area at which robot will move forward
	private final double FORWARD_AREA = 0.0145;

	// Slowest speed for auto accelerate
	private final double MIN_SPEED = 0.25;

	// Auto Drive Speed
	private final double AUTO_SPEED = 1.5 / FORWARD_AREA;

	// Make sure to use when feeding values to the drive train
	// It is safer not to send values higher than 1 or lower than -1
	private double capValue(double input) {
		return Math.min(Math.max(input, -1), 1);
	}

	// Prevent overwriting to the network table
	private boolean DriverMode = false;

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();

		// Recieve data from lime light
		final double X = LimeLight.getTargetXOffset();
		final double AREA = LimeLight.getTargetArea();

		// Distance Calculations
		double cameraHeight = SmartDashboard.getNumber("lheight", 0);
		double cameraAngle = SmartDashboard.getNumber("langle", 0);
		Vector2d Coords = LimeLight.getTargetCoordinates(cameraHeight, cameraAngle);
		double distance = LimeLight.getTargetDistance(cameraHeight, cameraAngle);
		SmartDashboard.putNumber("Target Distance", distance);
		SmartDashboard.putNumber("Target X Coord", Coords.x);
		SmartDashboard.putNumber("Target Y Coord", Coords.y);

		// Auto Accelerate
		boolean quickTurn = true;
		if (controller.getRawTopButton() && AREA != 0) {
			speed = capValue(MIN_SPEED + Math.max(FORWARD_AREA - AREA, 0) * AUTO_SPEED);

			SmartDashboard.putString("Acceleration Mode", "Automatic");
		} else {
			if (controller.getRawRightTrigger()) {
				// Average speed with 1 using acceleration as the preportion
				speed *= ACCELERATION_DIV - 1;
				speed += 1;
				speed /= ACCELERATION_DIV;

				quickTurn = false;
			}
			if (controller.getRawLeftTrigger()) {
				// Average speed with 0 using acceleration as the preportion
				speed -= speed / ACCELERATION_DIV;

				quickTurn = false;
			}

			SmartDashboard.putString("Acceleration Mode", "Manual");
		}

		// Aim Assist
		double turn = Math.pow(controller.getLeftX(), 3); // Left Stick
		if (controller.getRawLeftButton() || controller.getRawTopButton()) {
			turn += X / (TURN_DIV * Math.max(MOVE_DIV * speed, 1));

			if (DriverMode) {
				LimeLight.setCamMode(LimeLight.CAM_MODE.VISION);
				DriverMode = false;
			}

			SmartDashboard.putString("Aim/Turning Mode", "Assisted");
		} else {
			if (controller.getRawBottomButton()) {
				if (DriverMode) {
					LimeLight.setCamMode(LimeLight.CAM_MODE.VISION);
					DriverMode = false;
				}
			} else {
				if (!DriverMode) {
					LimeLight.setCamMode(LimeLight.CAM_MODE.DRIVER);
					DriverMode = true;
				}
			}

			SmartDashboard.putString("Aim/Turning Mode", "Manual");
		}

		// Feed values to drive train
		differentialDrive.curvatureDrive(capValue(speed), capValue(turn), quickTurn);
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
