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

/**
 * PID controller for left wheels
 * where the setpoint is in ft/s
 * @author gerthcm
 *
 */

class LeftMotorPID extends PIDSubsystem{
	
	static double P = 0.07; 
	static double I = 0.0001; 
	static double D = 0.0001; 
	static double F = 0.12;
	
	DriveTrain dt;
	
	LeftMotorPID(DriveTrain dt_in){
		super("LeftMotorPID", P, I, D);
		setOutputRange(-1, 1);
		setInputRange(-10,10);
		enable();
		dt = dt_in;
	}

	@Override
	protected double returnPIDInput() {
		return dt.getLeftSpdFtPerSec();
	}

	@Override
	protected void usePIDOutput(double arg0) {
		dt.leftMotor_1.set(arg0 + getSetpoint()*F);
		dt.leftMotor_2.set(arg0 + getSetpoint()*F);
		
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
	
}

/**
 * PID controller for right wheels
 * where the setpoint is in ft/s
 * @author gerthcm
 *
 */
class RightMotorPID extends PIDSubsystem{
	
	static double P = 0.07; 
	static double I = 0.0001; 
	static double D = 0.0001; 
	static double F = 0.12;
	DriveTrain dt;
	
	
	RightMotorPID(DriveTrain dt_in){
		super("RightMotorPID", P, I, D);
		setOutputRange(-1, 1);
		setInputRange(-10,10);
		enable();
		dt = dt_in;
	}

	@Override
	protected double returnPIDInput() {
		return dt.getRightSpdFtPerSec();
	}

	@Override
	protected void usePIDOutput(double arg0) {
		dt.rightMotor_1.set(-(arg0+ getSetpoint()*F)); //set minus cuz this is the right side and stuff
		dt.rightMotor_2.set(-(arg0+ getSetpoint()*F));
		
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
	
}

