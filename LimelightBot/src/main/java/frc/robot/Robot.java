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

import java.awt.geom.Area;

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
	private final double TURN_DIV = 16; // Make sure to tune to robot
	private final double MOVE_TURN_DIV = 2;

	private final double FORWARD_AREA = 0.01; // Make sure to tune to target

	private final double BACKWARD_AREA = 0.02; // Backup value

	private final double SPEED = 1 / FORWARD_AREA * 2;

	Gamepad controller;

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		// table = NetworkTableInstance.getDefault().getTable("limelight");

		m_chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", m_chooser);
		// client = new NetworkTableClient("limelight");

		// Initialize Motors (HAD TO BE REWIRED)
		// Looks random, but this is very specific
		rightFrontMotor = new WPI_TalonSRX(3);
		rightRearMotor = new WPI_TalonSRX(2);
		leftFrontMotor = new WPI_TalonSRX(1);
		leftRearMotor = new WPI_TalonSRX(4);

		// Motors were built backwards
		rightFrontMotor.setInverted(true);
		rightRearMotor.setInverted(true);
		leftFrontMotor.setInverted(true);
		leftRearMotor.setInverted(true);

		// Geriodicallyroup Motors
		rightSpeedController = new SpeedControllerGroup(rightFrontMotor, rightRearMotor);
		leftSpeedController = new SpeedControllerGroup(leftFrontMotor, leftRearMotor);

		// Make differential drive from motor groups
		differentialDrive = new DifferentialDrive(leftSpeedController, rightSpeedController);

		// Be able to read from controller
		controller = new Gamepad(0);
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
		final double X = LimeLight.getTargetXOffset();
		final double Y = LimeLight.getTargetYOffset();
		final double TURN_VAL = capValue(X / TURN_DIV);
		final double AREA = LimeLight.getTargetArea();

		// Post to smart dashboard periodically
		SmartDashboard.putNumber("X Offset", X);
		SmartDashboard.putNumber("Y Offset", Y);
		SmartDashboard.putNumber("Target Area", AREA);
		SmartDashboard.putNumber("Turn Value", TURN_VAL);

		if (controller.getRawRightButton()) {
			// Move backwards first for saftey reasons
			if (AREA > BACKWARD_AREA) {
				// If area is too big, its too close, move backwards
				SmartDashboard.putString("Driving Status", "Backwards");

				// \/ TURNING WHILE MOVING \/
				differentialDrive.tankDrive(capValue(-0.75 + TURN_VAL / MOVE_TURN_DIV),
						capValue(-0.75 - TURN_VAL / MOVE_TURN_DIV));
			} else if (AREA < FORWARD_AREA && AREA != 0) {
				// If area is too small, its too far, move forwarclosed
				SmartDashboard.putString("Driving Status", "Forwards");

				// \/ TURNING WHILE MOVING \/
				final double MOVE_SPEED = (FORWARD_AREA - AREA) * SPEED; // Change Speed
				differentialDrive.tankDrive(capValue(MOVE_SPEED + TURN_VAL / MOVE_TURN_DIV),
						capValue(MOVE_SPEED - TURN_VAL / MOVE_TURN_DIV));
			} else { 
				// Turn the tank drive
				SmartDashboard.putString("Driving Status", "Turning (" + TURN_VAL + ")");
				differentialDrive.tankDrive(TURN_VAL, -TURN_VAL);
	
				LimeLight.setCamMode(LimeLight.CAM_MODE.VISION);
			} 

			LimeLight.setCamMode(LimeLight.CAM_MODE.VISION);
		} else if (controller.getRawBottomButton()) {
			// Turn the tank drive
			SmartDashboard.putString("Driving Status", "Turning (" + TURN_VAL + ")");
			differentialDrive.tankDrive(TURN_VAL, -TURN_VAL);

			LimeLight.setCamMode(LimeLight.CAM_MODE.VISION);
		} else if (controller.getRawLeftButton()) {
			SmartDashboard.putString("Driving Status", "Vision Mode");

			LimeLight.setCamMode(LimeLight.CAM_MODE.VISION);
		} else {
			final double LEFT = controller.getLeftY();
			final double RIGHT = controller.getRightY();

			// Cube Inputs
			differentialDrive.tankDrive(-LEFT * LEFT * LEFT, -RIGHT * RIGHT * RIGHT);
			SmartDashboard.putNumber("Left Stick", LEFT);
			SmartDashboard.putNumber("Right Stick", RIGHT);
			SmartDashboard.putString("Driving Status", "Drive Train");

			LimeLight.setCamMode(LimeLight.CAM_MODE.DRIVER);
		}
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
