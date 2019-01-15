/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team694.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import stuyvision.ModuleRunner;
import stuyvision.capture.DeviceCaptureSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team694.robot.commands.ExampleCommand;
import org.usfirst.frc.team694.robot.cv.Camera;
import org.usfirst.frc.team694.robot.subsystems.ExampleSubsystem;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	public static ExampleSubsystem m_subsystem = new ExampleSubsystem();
	public static OI oi;

	DeviceCaptureSource cam;

	Command m_autonomousCommand;
	SendableChooser<Command> m_chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		oi = new OI();
		m_chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", m_chooser);
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
		cam = Camera.initializeCamera(0);
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
			System.out.println("Bumper pressed");
			LocalDateTime time = LocalDateTime.now();
			String localtime = time.toString();
			Mat frame = Camera.getImage(cam);
			if (frame == null) {
				System.out.println("Failed to read from camera");
			} else {
			Imgcodecs.imwrite("/tmp/" + localtime + ".png", frame);
			System.out.println("Succeeded in reading from camera");
			Imgproc.cvtColor(frame,frame,Imgproc.COLOR_BGR2HSV);
			ArrayList<Mat> channels = new ArrayList<Mat>();
			Core.split(frame, channels);
			Mat hue = new Mat();
			Core.inRange(channels.get(0), new Scalar(0), new Scalar(0), hue);
			Imgcodecs.imwrite("/tmp/" + localtime + "hue.png", frame);
			Mat saturation = new Mat();
			Core.inRange(channels.get(1), new Scalar(0), new Scalar(0), saturation);
			Imgcodecs.imwrite("/tmp/" + localtime + "sat.png", frame);
			Mat filtered = new Mat();
			Core.bitwise_and(hue, saturation, filtered);
			Imgcodecs.imwrite("/tmp/" + localtime + "filtered.png", frame);
			}
			frame.release();
			}
		}	

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
