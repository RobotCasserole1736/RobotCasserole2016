package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

public class StateMachine {
	
	//IntakeMotorSpeeds
	final static double intakeSpeed = 0.5;
	final static double ejectSpeed = -0.5;
	
	//PrepMotor Intake Motor Rotation Time
	final static double prepEjectTime = 0.5; //Seconds
	
	final static int shooterSpeedTolerance = 200; //RPM

	//Main Components of shooting mechanism
	protected Shooter shooter;
	protected Talon intake;
	
	//Sensor for ball detection
	protected DigitalInput ballSensor;
	
	//IDs
	final static int intake_ID = 0;
	final static int ballSensor_ID = 0;
	
	//Initial State
	String state = "Inactive";
	
	public StateMachine(Shooter shooter) {
		
		this.shooter = shooter;
		intake = new Talon(intake_ID);
		
	}
	
	public void processState()
	{
		switch(state)
		{
			case "Inactive":
				
				break;
			case "Intake":
				
				break;
			case "Eject":
				
				break;
			case "Carry Ball":
				
				break;
			case "Spooldown":
				
				break;
		}
	}
	
	public void processInputs(Joystick operator)
	{
		if(!operator.getRawButton(Robot.XBOX_SELECT_BUTTON))
		{
			if(operator.getRawButton(Robot.XBOX_LEFT_BUTTON) && !getBallSensor())
			{
				setState("Intake");
			}
			else if(operator.getRawButton(Robot.XBOX_RIGHT_BUTTON))
			{
				setState("Eject");
			}
			else
			{
				setState("Inactive");
			}
		}
		else
		{
			setState("Intake");		
		}
		
		
		
		processState();
		
		
	}
	
	public void prepMotor()
	{
		
		
		
	}
	
	public void setState(String newState)
	{
		state = newState;
	}
	
	//Intake Motor Methods
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
	
	//Inactive State
	public void stopMotors()
	{
		setIntake(0);
		shooter.setSpeed(0);
	}
	
	public void setLaunchMotor(double speed)
	{
		shooter.setSpeed(speed);
	}
	
	public void stopLaunchMotor()
	{
		setLaunchMotor(0);
	}
	
	public boolean spooledDown()
	{
		if(Math.abs(shooter.getActSpeed()) < shooterSpeedTolerance)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean getBallSensor()
	{
		return ballSensor.get();
	}
	
}