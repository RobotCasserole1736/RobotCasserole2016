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
	public static final double WHEEL_TO_ENCODER_RATIO = 24f/60f*12f/24f*12f/36f; //encoder is downstream of shifter, so same ratio hg/lg - Third Stage * chain stage * encoder stage 
	public static final double MOTOR_TO_ENCODER_RATIO_HG = (2.8333)/3;
	public static final double MOTOR_TO_ENCODER_RATIO_LG = (6.1275)/3;
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
		leftCCE = new CIMCurrentEstimator(2, controllerVDrop_V, pdp);
		rightCCE = new CIMCurrentEstimator(2, controllerVDrop_V, pdp);
		
		//Battery Para Estimator
		this.bpe = bpe;
		
		//Power Distribution Panel
		this.pdp = pdp;
		
		//Encoders
		leftEncoder = new Encoder(leftEncoderChannel_1, leftEncoderChannel_2);
		rightEncoder = new Encoder(rightEncoderChannel_1, rightEncoderChannel_2);

		//Return encoder distance in radians
		leftEncoder.setDistancePerPulse(Math.PI*2.0/(double)ENCODER_TICKS_PER_REV);
		rightEncoder.setDistancePerPulse(Math.PI*2.0/(double)ENCODER_TICKS_PER_REV);
		leftEncoder.setReverseDirection(true);
		
		//Disable safety timeout
		
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
			return rightEncoder.getRate()*MOTOR_TO_ENCODER_RATIO_HG;
		else
			return rightEncoder.getRate()*MOTOR_TO_ENCODER_RATIO_LG;
	}
	
	public double getLeftWheelSpeedRPM(){
			return leftEncoder.getRate()*WHEEL_TO_ENCODER_RATIO*9.5492;
	}
	
	public double getRightWheelSpeedRPM(){
			return rightEncoder.getRate()*WHEEL_TO_ENCODER_RATIO*9.5492;
	}
	
	public double getRightDistanceFt(){
		return rightEncoder.getDistance()*WHEEL_TO_ENCODER_RATIO*WHEEL_RADIUS_IN*1/12;
	}
	
	public double getLeftDistanceFt(){
		return leftEncoder.getDistance()*WHEEL_TO_ENCODER_RATIO*WHEEL_RADIUS_IN*1/12;
	}
	
	public double getLeftSpdFtPerSec(){
		return leftEncoder.getRate()*WHEEL_TO_ENCODER_RATIO*WHEEL_RADIUS_IN*1/12;
	}
	
	public double getRightSpdFtPerSec(){
		return rightEncoder.getRate()*WHEEL_TO_ENCODER_RATIO*WHEEL_RADIUS_IN*1/12;
	}
	
	public void resetEncoderDistances(){
		leftEncoder.reset();
		rightEncoder.reset();
	}
	
	public double getLeftCurrent()
	{
		return leftCCE.getCurrentEstimate(getLeftMotorSpeedRadPerS(), leftMotor_1.get()); //*3 is fudge factor to make it work....
	}
	
	public double getRightCurrent()
	{
		return rightCCE.getCurrentEstimate(getRightMotorSpeedRadPerS(), -rightMotor_1.get());
	}
	
	public void alignToVisionTarget()
	{
		
	}
	
	public String getMemes()
	{
		return "Memes";
	}

}