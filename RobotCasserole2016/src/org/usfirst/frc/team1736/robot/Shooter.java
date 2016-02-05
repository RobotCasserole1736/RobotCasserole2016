package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.StatusFrameRate;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

public class Shooter extends PIDSubsystem {
	CANTalon shooterController;
	static double P = 0.0001;
	static double I = 0.0001;
	static double D = 0.00001;
	int SHOOTER_CHANNEL = 1; //CMG - confirmed 2/2/2016
	double MAX_SPEED = 6000;
	int codesPerRev = 1024;
	MedianFilter speedFilt;
	private final static int SPEED_FILT_LEN = 13;
	
	double filtered_act_speed = 0;

	public double motorCmd = 0;
	
	public Shooter() {
		super("ShooterPID", P, I, D);
		shooterController = new CANTalon(SHOOTER_CHANNEL);
		shooterController.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		shooterController.enableBrakeMode(false);
		shooterController.configEncoderCodesPerRev(codesPerRev);
		shooterController.setStatusFrameRateMs(StatusFrameRate.Feedback, 20);
		shooterController.setStatusFrameRateMs(StatusFrameRate.General, 20);
		speedFilt = new MedianFilter(SPEED_FILT_LEN, 0);
		setInputRange(0,MAX_SPEED);
		setOutputRange(0,1);
		enable();
	}
	
	public void setSpeed(double speed){
		setSetpoint(speed);
	}
	public double getCurrent(){
		return shooterController.getOutputCurrent();
	}
	public double getActSpeed(){
		return filtered_act_speed;

	}
	public double getDesSpeed(){
		return getSetpoint();
	}

	@Override
	protected double returnPIDInput() {
		//convert raw encoder ticks to RPM and filter
		filtered_act_speed = Math.abs(shooterController.getEncVelocity()/(double)codesPerRev*60.0);
		return filtered_act_speed;
	}

	@Override
	protected void usePIDOutput(double arg0) {
		shooterController.set(arg0);	
		motorCmd = arg0;
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
}
	
