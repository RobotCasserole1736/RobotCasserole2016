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
		
		//Encoders
		leftEncoder = new Encoder(leftEncoderChannel_1, leftEncoderChannel_2);
		rightEncoder = new Encoder(rightEncoderChannel_1, rightEncoderChannel_2);
		
		//Pi Radians per step of encoder rotation.
		leftEncoder.setDistancePerPulse(Math.PI/512);
		rightEncoder.setDistancePerPulse(Math.PI/512);
		rightEncoder.setReverseDirection(true);
		
	}
	
	public boolean isAcceptableVoltage()
	{
		
		return true;
	}

}