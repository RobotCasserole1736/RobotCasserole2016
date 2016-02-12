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
	
	//Shooter spooled down tolerance for ability for intake to run
	final static int spooledDownTolerance = 50; //RPM
	
	//Shooter spooled up tolerance for validity of shot
	final static int spooledUpTolerance = 50; //RPM

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
	
	//Override boolean -- throw if override is pressed
	Boolean overridden = false;
	
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
			case "Prep Launch":
				prepMotor();
				break;
			case "Spooldown":
				setIntake(0);
				if(spooledDown())
				{
					setState("Inactive");
				}
				else
				{
					stopLaunchMotor();
				}
				break;
		}
	}
	
	public void processInputs(Joystick operator)
	{
		if(operator.getRawButton(Robot.XBOX_LEFT_BUTTON) && 
		  ((!getBallSensor() && getState() != "Preplaunch") || overridden))
		{
			setState("Intake");
		}
		else if(getBallSensor())
		{
			setState("Carry Ball");
		}
		else if(operator.getRawButton(Robot.XBOX_RIGHT_BUTTON))
		{
			setState("Eject");
		}
		else
		{
			setState("Spooldown");
		}
		
		if(getBallSensor() && operator.getRawButton(Robot.XBOX_X_BUTTON) && getState() != "Prep Launch")
		{
			setState("Prep Launch");
		}
		else if(getBallSensor() && operator.getRawButton(Robot.XBOX_X_BUTTON) && getState() == "PrepLaunch")
		{
			setState("Spooldown");
		}
		
		if(operator.getRawButton(Robot.XBOX_START_BUTTON))
		{
			override();
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
	
	public String getState()
	{
		return state;
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
	
	public boolean spooledUp()
	{
		if(shooter.getSetpoint() == shooter.SHOT_RPM && shooter.getAbsError() < spooledUpTolerance)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean spooledDown()
	{
		if(shooter.getSetpoint() == 0 && shooter.getAbsError() < spooledDownTolerance)
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
	
	public void override()
	{
		overridden = true;
	}
}