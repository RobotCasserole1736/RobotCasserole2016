package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;

public class DriveTrain {
	
	//Local Motors
	//Left
	protected SpeedController leftMotor_1;
	protected SpeedController leftMotor_2;
	//Right
	protected SpeedController rightMotor_1;
	protected SpeedController rightMotor_2;
	
	//Chris' CIM Current Estimators
	CIMCurrentEstimator leftCCE;
	CIMCurrentEstimator rightCCE;
	//Using Chris' naming convention
	double motorEncRatio = 0;
	double controllerVDrop_V = 0;
	
	
	//Encoders
	protected Encoder leftEncoder;
	protected Encoder rightEncoder;
	//Encoder channels
	protected int leftEncoderChannel_1 = 0;
	protected int leftEncoderChannel_2 = 0;
	protected int rightEncoderChannel_1 = 0;
	protected int rightEncoderChannel_2 = 0;
	
	public DriveTrain(SpeedController leftMotor_1, SpeedController leftMotor_2, 
			SpeedController rightMotor_1, SpeedController rightMotor_2)
	{
		//Left Motors
		this.leftMotor_1 = leftMotor_1;
		this.leftMotor_2 = leftMotor_2;
		//Right Motors
		this.rightMotor_1 = rightMotor_1;
		this.rightMotor_2 = rightMotor_2;
		
		//CIM Current Estimators
		leftCCE = new CIMCurrentEstimator(2, motorEncRatio, controllerVDrop_V);
		rightCCE = new CIMCurrentEstimator(2, motorEncRatio, controllerVDrop_V);
		
		//Encoders
		leftEncoder = new Encoder(leftEncoderChannel_1, leftEncoderChannel_2);
		rightEncoder = new Encoder(rightEncoderChannel_1, rightEncoderChannel_2);
		
		//Copied this out of last year's code cause I assume we need to
		leftEncoder.setDistancePerPulse(0.073);
		rightEncoder.setDistancePerPulse(0.073);
		rightEncoder.setReverseDirection(true);
		
	}
	
	public void drive(double joy_x, double joy_y, double tunedVal)
	{
		double leftOutput, rightOutput;
		
		if(joy_y > Math.abs(0.1) && joy_x < Math.abs(0.1))
		{
			leftOutput = joy_y;
			rightOutput = joy_y;
		}
		else if(joy_x > Math.abs(0.1))
		{
			//This code creates a scenario where joy_x and joy_y can both be 1 or -1
			//and thus would create outputs of 2 and 0...
			leftOutput = joy_x + joy_y;
			rightOutput = -joy_x + joy_y;
		}
		else
		{
			leftOutput = 0;
			rightOutput = 0;
		}
		
		leftOutput = Math.pow(Math.max(1, leftOutput), tunedVal);
		rightOutput = Math.pow(Math.max(1, rightOutput), tunedVal);
		setMotorVals(leftOutput, rightOutput);
	}
	
	public void setMotorVals(double leftOut, double rightOut)
	{
		leftMotor_1.set(leftOut);
		leftMotor_2.set(leftOut);
		
		rightMotor_1.set(rightOut);
		rightMotor_2.set(rightOut);
	}

}