package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.StatusFrameRate;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

public class Shooter extends PIDSubsystem {
	CANTalon shooterController;
	static double F = 0.0001737; //We use FF because setpoint is proportional to motor command
	static double P = 0.0008; //CMG - tuned with two wheels, will need to tune 
	static double I = 0.000001; //I is definitely needed to overcome friction, otherwise there is a noticeable steady-state error
	static double D = 0.00004; 
	int SHOOTER_CHANNEL = 1; //CMG - confirmed 2/2/2016
	
	//Squish sensor cal
	AnalogInput squishSensor;
	private static final int SQUISH_SENSOR_PORT = 0;
	private double squishSensorZeroOffsetPoint = 0;
	private double squishSensorOffsetGain = 0;
	
	double filtered_act_speed = 0;
	double motorCmd = 0;
	
	//Watchdog - try to catch when this thread just stops working. This variable will
	//be set to zero in the methods called periodically here, but incrimented externally.
	//When it gets to high, it is assumed this module has crashed and should be restarted 
	//That functionality is still in the works, but the variable has been added for logging
	//purposes.
	volatile public int wdog_ctr;
	
	public Shooter() {
		super("ShooterPID", P, I, D); //we don't need to WPILIB feed forward. we do feed fowrard ourselfs cuz they were silly with their implementation.
		shooterController = new CANTalon(SHOOTER_CHANNEL);
		shooterController.changeControlMode(CANTalon.TalonControlMode.PercentVbus); //We'll use the standard 0-1 pct of input voltage method since our PID accounts for voltage drop.
		shooterController.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative); //We're using the mag encoder
		shooterController.enableBrakeMode(true); //when 0 RPM is commanded, rely upon speed controller to quickly stop the rotation of the launch wheel.
		shooterController.configEncoderCodesPerRev(1024); //Very unknown if this is needed?
		shooterController.setStatusFrameRateMs(StatusFrameRate.Feedback, 20); //Bump up the sample rates to get better response time (at the impact of higher CAN bus load)
		shooterController.setStatusFrameRateMs(StatusFrameRate.General, 20);
		setOutputRange(-0.5,1); //Must not command the motor in reverse since the input speed taken as unsigned (negative motor commands cause instability)
		enable();
		
		//Squish sensor - a force sensor that registers how much the ball is compressing
		squishSensor = new AnalogInput(SQUISH_SENSOR_PORT);
		
		wdog_ctr = 0;
	}
	
	/**
	 * Set a new desired speed for the launch motor in RPM
	 * @param speed - setpoint to command the motor to in RPM
	 */
	public void setSpeed(double speed){
		if(speed == 0.0){
			getPIDController().reset();
			getPIDController().enable();
		}
		
		setSetpoint(speed);
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
		return getSetpoint();
	}
	
	/**
	 * Get the present error (act - des) in RPM
	 * @return
	 */
	public double getError(){
		return (getActSpeed() - getDesSpeed());
	}
	
	/**
	 * Get absolute value of present error in RPM
	 * @return
	 */
	public double getAbsError() {
		return Math.abs(getError());
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
		filtered_act_speed = -shooterController.getSpeed();
		return filtered_act_speed + (getSquishSensorVal() - squishSensorZeroOffsetPoint) * squishSensorOffsetGain;
	}

	@Override
	protected void usePIDOutput(double arg0) {
		double cmd = Math.max(Math.min(arg0 + F*getDesSpeed(), 1), 0);
		shooterController.set(cmd);	
		motorCmd = cmd;
		wdog_ctr = 0;
		
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
}
	
