package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;

public class StateMachine {
	
	//IntakeMotorSpeeds
	final static double intakeSpeed = 0.5;
	final static double ejectSpeed = -0.5;
	
	//PrepMotor Intake Motor Rotation Time
	final static double prepEjectTime = 0.5; //Seconds

	//Main Components of shooting mechanism
	protected Shooter shooter;
	protected Talon intake;
	
	//Sensor for ball detection
	protected DigitalInput ballSensor;
	
	//IDs
	final static int intake_ID = 0;
	final static int ballSensor_ID = 0;
	
	public StateMachine(Shooter shooter) {
		
		this.shooter = shooter;
		intake = new Talon(intake_ID);
		
	}
	
	public void processInputs(Joystick operator)
	{
		if(operator.getRawButton(Robot.XBOX_LEFT_BUTTON) && !getBallSensor())
		{
			intake();
		}
		else if(operator.getRawButton(Robot.XBOX_RIGHT_BUTTON))
		{
			eject();
		}
		else
		{
			setIntake(0);
		}
		
		
		
	}
	
	public void prepMotor()
	{
		
		
		
	}

	public void setIntake(double speed)
	{
		intake.set(speed);
	}
	
	public void intake()
	{
		setIntake(intakeSpeed);
	}
	
	public void eject()
	{
		setIntake(ejectSpeed);
	}
	
	public boolean getBallSensor()
	{
		return ballSensor.get();
	}
	
}