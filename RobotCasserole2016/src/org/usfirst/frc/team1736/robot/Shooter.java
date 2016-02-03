package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.CANTalon;

public class Shooter {
	CANTalon shooterController;
	double P = 1;
	double I = 0;
	double D = 0;
	int SHOOTER_CHANNEL = 1; //CMG - confirmed 2/2/2016
	double MAX_SPEED = 6000;
	int codesPerRev = 1024;
	
	public Shooter() {
		shooterController = new CANTalon(SHOOTER_CHANNEL);
		shooterController.setPID(P, I, D);
		shooterController.changeControlMode(CANTalon.TalonControlMode.Speed);
		shooterController.enableBrakeMode(false);
		shooterController.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		shooterController.configEncoderCodesPerRev(codesPerRev);
		shooterController.enableControl();
	}
	
	public void setSpeed(double speed){
		shooterController.setSetpoint(speed);
	}
	public double getCurrent(){
		return shooterController.getOutputCurrent();
	}
	public double getActSpeed(){
		return shooterController.getSpeed();
	}
	public double getDesSpeed(){
		return shooterController.getSetpoint();
	}
}
	
