package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.command.PIDSubsystem;

public class DriveMotorsPIDVelocity {
	
	DriveTrain dt;
	
	public LeftMotorPID lmpid;
	public RightMotorPID rmpid;
	
	DriveMotorsPIDVelocity(DriveTrain dt_in){
		dt = dt_in;
		lmpid = new LeftMotorPID(dt);
		rmpid = new RightMotorPID(dt);
	}	

}

class LeftMotorPID extends PIDSubsystem{
	
	static double P = 0.0001; 
	static double I = 0.0000; 
	static double D = 0.0000; 
	
	DriveTrain dt;
	
	LeftMotorPID(DriveTrain dt_in){
		super("LeftMotorPID", P, I, D);
		dt = dt_in;
	}

	@Override
	protected double returnPIDInput() {
		// TODO Auto-generated method stub
		return dt.getLeftSpdFtPerSec();
	}

	@Override
	protected void usePIDOutput(double arg0) {
		dt.leftMotor_1.set(arg0);
		dt.leftMotor_2.set(arg0);
		
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
	
}

class RightMotorPID extends PIDSubsystem{
	
	static double P = 0.0001; 
	static double I = 0.0000; 
	static double D = 0.0000; 
	DriveTrain dt;
	
	
	RightMotorPID(DriveTrain dt_in){
		super("RightMotorPID", P, I, D);
		dt = dt_in;
	}

	@Override
	protected double returnPIDInput() {
		// TODO Auto-generated method stub
		return dt.getRightSpdFtPerSec();
	}

	@Override
	protected void usePIDOutput(double arg0) {
		dt.rightMotor_1.set(-arg0); //set minus cuz this is the right side and stuff
		dt.rightMotor_2.set(-arg0);
		
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
	
}

