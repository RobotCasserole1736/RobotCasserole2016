package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.StatusFrameRate;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

public class Shooter extends PIDSubsystem {
	CANTalon shooterController;
	static double F = 0.0001737;
	static double P = 0.0003; //CMG - these were poorly tuned with just the gearbox - will need to be adjusted to be better
	static double I = 0.000024; //I is definitely needed to overcome friction, otherwise there is a noticeable steady-state error
	static double D = 0.00002; 
	int SHOOTER_CHANNEL = 1; //CMG - confirmed 2/2/2016
	int codesPerRev = 1024;
	double decodeMultiplier = 1; //counts each encoder tick as 4 ticks
	MedianFilter speedFilt;
	private final static int SPEED_FILT_LEN = 3;
	
	//Squish sensor cal
	AnalogInput squishSensor;
	private static final int SQUISH_SENSOR_PORT = 0;
	private double squishSensorZeroOffsetPoint = 0;
	private double squishSensorOffsetGain = 0;
	
	double filtered_act_speed = 0;
	double motorCmd = 0;
	
	public Shooter() {
		super("ShooterPID", P, I, D); //we don't need to WPILIB feed forward. we do feed fowrard ourselfs.
		shooterController = new CANTalon(SHOOTER_CHANNEL);
		shooterController.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		shooterController.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		shooterController.enableBrakeMode(true); //when 0 RPM is commanded, rely upon speed controller to quickly stop the rotation of the launch wheel.
		shooterController.configEncoderCodesPerRev(codesPerRev);
		shooterController.setStatusFrameRateMs(StatusFrameRate.Feedback, 20); //Bump up the sample rates to get better response time (at the impact of higher CAN bus load)
		shooterController.setStatusFrameRateMs(StatusFrameRate.General, 20);
		speedFilt = new MedianFilter(SPEED_FILT_LEN, 0);
		setOutputRange(-0.5,1); //Must not command the motor in reverse since the input speed taken as unsigned (negative motor commands cause instability)
		enable();
		
		squishSensor = new AnalogInput(SQUISH_SENSOR_PORT);
	}
	
	/**
	 * Set a new desired speed for the launch motor in RPM
	 * @param speed - setpoint to command the motor to in RPM
	 */
	public void setSpeed(double speed){
		setSetpoint(speed/decodeMultiplier);
	}
	
	/**
	 * Get the present current draw of the launch motor in Amps
	 * @return
	 */
	public double getCurrent(){
		return shooterController.getOutputCurrent();
	}
	
	/**
	 * Get the actual rotational speed of the launch wheel in RPM
	 * @return
	 */
	public double getActSpeed(){
		return filtered_act_speed;

	}
	
	/**
	 * Get the present desired speed (setpoint) in RPM
	 * @return
	 */
	public double getDesSpeed(){
		return getSetpoint()*decodeMultiplier;
	}
	
	/**
	 * Get the present error (act - des) in RPM
	 * @return
	 */
	public double getError(){
		return (getActSpeed() - getDesSpeed());
	}
	
	/**
	 * Get the present command to the motor (range 0-1)
	 * @return
	 */
	public double getMotorCmd(){
		return motorCmd;
	}

	/**
	 * returns the value of the ball compression sensor
	 * where 1 is something and 0 is the opposite
	 * @return
	 */
	public double getSquishSensorVal(){
		return squishSensor.getVoltage()/5.0;
	}
	
	@Override
	protected double returnPIDInput() {
		//convert raw encoder ticks to RPM and filter
		//Median filter to reject single-pulse encoder failures
		//Abs to reject sign flips occurring from an unfortunate encoder tick miss
		//filtered_act_speed = -shooterController.getEncVelocity()/(double)codesPerRev*60.0*decodeMultiplier;
		//filtered_act_speed = speedFilt.filter(Math.abs(shooterController.getEncVelocity()/(double)codesPerRev*60.0))*decodeMultiplier;
		filtered_act_speed = -shooterController.getSpeed();
		return filtered_act_speed + (getSquishSensorVal() - squishSensorZeroOffsetPoint) * squishSensorOffsetGain;
	}

	@Override
	protected void usePIDOutput(double arg0) {
		double cmd = Math.max(Math.min(arg0 + F*getDesSpeed(), 1), 0);
		shooterController.set(cmd);	
		motorCmd = cmd;
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
}
	
