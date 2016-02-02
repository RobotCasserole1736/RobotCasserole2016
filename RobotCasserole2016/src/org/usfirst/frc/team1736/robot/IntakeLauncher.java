package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SpeedController;

public class IntakeLauncher {

	//-Motors
	SpeedController launchMotor;
	SpeedController intakeRoller;
	
	//-Encoder
	Encoder launchEncoder;
	
	//-Ball Sensor
	DigitalInput ballSensor;
	
	//-PIDController
	PIDController launchPID;
	
	double Kp = 0;
	double Ki = 0;
	double Kd = 0;
	
	double PIDTolerancePercent = 5;
	
	public IntakeLauncher(SpeedController launchMotor, SpeedController intakeRoller, 
						  Encoder launchEncoder, DigitalInput ballSensor)
	{
		this.launchMotor = launchMotor;
		this.intakeRoller = intakeRoller;
		
		this.launchEncoder = launchEncoder;
//		launchEncoder.setDistancePerPulse(distancePerPulse);
		
		this.ballSensor = ballSensor;
		
		launchPID = new PIDController(Kp, Ki, Kd, launchEncoder, launchMotor);
		launchPID.enable();
		launchPID.setPercentTolerance(PIDTolerancePercent);
		launchPID.setOutputRange(0, 1);
	}
	
	public void spinIntake(double speed)
	{
		intakeRoller.set(speed);
	}
	
	public boolean getBallSensor()
	{
		return ballSensor.get();
	}
	
	public void prepLaunchMotor(double setPoint)
	{
		launchPID.setSetpoint(setPoint);
//		launchPID.
	}
}