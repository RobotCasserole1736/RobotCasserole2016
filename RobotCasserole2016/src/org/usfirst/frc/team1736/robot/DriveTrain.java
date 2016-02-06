package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;

public class DriveTrain extends RobotDrive { //Inherits methods from RobotDrive for basic drive functionality
	
	//Local Motors
	//Left
	protected SpeedController leftMotor_1;
	protected SpeedController leftMotor_2;
	//Right
	protected SpeedController rightMotor_1;
	protected SpeedController rightMotor_2;
	
	//Power Distribution Panel
	protected PowerDistributionPanel pdp;
	//Battery Param Estimator
	protected BatteryParamEstimator bpe;
	
	//Chris' CIM Current Estimators
	public CIMCurrentEstimator leftCCE;
	public CIMCurrentEstimator rightCCE;
	
	//Using Chris' naming convention
	double motorEncRatio = 0;
	double controllerVDrop_V = 0;
	
	//Encoders
	protected Encoder leftEncoder;
	protected Encoder rightEncoder;
	//Encoder channels //CMG - Confirmed 2-2-2016
	protected int leftEncoderChannel_1 = 0; 
	protected int leftEncoderChannel_2 = 1;
	protected int rightEncoderChannel_1 = 2;
	protected int rightEncoderChannel_2 = 3;

	//Encoder Ratios
	public static final double WHEEL_RADIUS_IN = 8.75; //kinda, cuz they're pneumatic... 
	public static final double WHEEL_TO_ENCODER_RATIO = 24/60*12/24*12/36; //encoder is downstream of shifter, so same ratio hg/lg - Third Stage * chain stage * encoder stage 
	public static final double MOTOR_TO_ENCODER_RATIO_HG = 3/2.8333;
	public static final double MOTOR_TO_ENCODER_RATIO_LG = 3/6.1275;
	public static final int ENCODER_TICKS_PER_REV = 128;
	
	
	public DriveTrain(SpeedController leftMotor_1, SpeedController leftMotor_2, 
			SpeedController rightMotor_1, SpeedController rightMotor_2,
			PowerDistributionPanel pdp, BatteryParamEstimator bpe)
	{
		//Super Constructor
		super(leftMotor_1, leftMotor_2, rightMotor_1, rightMotor_2);
		
		//Left Motors
		this.leftMotor_1 = leftMotor_1;
		this.leftMotor_2 = leftMotor_2;
		//Right Motors
		this.rightMotor_1 = rightMotor_1;
		this.rightMotor_2 = rightMotor_2;
		
		//CIM Current Estimators
		leftCCE = new CIMCurrentEstimator(2, motorEncRatio, controllerVDrop_V, pdp);
		rightCCE = new CIMCurrentEstimator(2, motorEncRatio, controllerVDrop_V, pdp);
		
		//Battery Para Estimator
		this.bpe = bpe;
		
		//Power Distribution Panel
		this.pdp = pdp;
		
		//Encoders
		leftEncoder = new Encoder(leftEncoderChannel_1, leftEncoderChannel_2);
		rightEncoder = new Encoder(rightEncoderChannel_1, rightEncoderChannel_2);

		//Return encoder distance in radians
		leftEncoder.setDistancePerPulse(Math.PI*2/ENCODER_TICKS_PER_REV);
		rightEncoder.setDistancePerPulse(Math.PI*2/ENCODER_TICKS_PER_REV);
		rightEncoder.setReverseDirection(true);
		
	}
	
	public boolean isAcceptableVoltage(double leftOutput, double rightOutput)
	{
		//Implement current/voltage checking here
		//Need to ask Chris about how to implement with BPE
		return true;
	}
	
	public void setLeftRightMotorOutputs(double leftOutput, double rightOutput)
	{
		if(isAcceptableVoltage(leftOutput, rightOutput))
		{
			super.setLeftRightMotorOutputs(leftOutput, rightOutput);
		}
		else
		{
			super.setLeftRightMotorOutputs(0, 0);
			System.out.println("Voltage too low!");
		}
	}
	
	public double getLeftMotorSpeedRadPerS(){
		if(Pneumatics.isHighGear())
			return leftEncoder.getRate()*MOTOR_TO_ENCODER_RATIO_HG;
		else
			return leftEncoder.getRate()*MOTOR_TO_ENCODER_RATIO_LG;
	}
	
	public double getRightMotorSpeedRadPerS(){
		if(Pneumatics.isHighGear())
			return leftEncoder.getRate()*MOTOR_TO_ENCODER_RATIO_HG;
		else
			return leftEncoder.getRate()*MOTOR_TO_ENCODER_RATIO_LG;
	}
	
	public double getRightDistanceFt(){
		return rightEncoder.getDistance()*WHEEL_TO_ENCODER_RATIO;
	}
	
	public double getLeftDistanceFt(){
		return leftEncoder.getDistance()*WHEEL_TO_ENCODER_RATIO;
	}
	
	public void resetEncoderDistances(){
		leftEncoder.reset();
		rightEncoder.reset();
	}
	
	public double getLeftCurrent(double leftMotorCmd)
	{
		return leftCCE.getCurrentEstimate(getLeftMotorSpeedRadPerS(), leftMotorCmd);
	}
	
	public double getRightCurrent(double rightMotorCmd)
	{
		return rightCCE.getCurrentEstimate(getRightMotorSpeedRadPerS(), rightMotorCmd);
	}
	
	public void alignToVisionTarget()
	{
		
	}
	
	public String getMemes()
	{
		return "Memes";
	}

}