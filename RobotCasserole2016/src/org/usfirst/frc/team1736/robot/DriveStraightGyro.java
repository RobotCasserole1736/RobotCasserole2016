package org.usfirst.frc.team1736.robot;

/**
 * Drive Straight Gyro - simple class to power drivetrain forward
 * with PD controller to adjust course to maintain a given angle setpoint
 * @author Chris Gerth
 *
 */
public class DriveStraightGyro {
	
	private static final double Kp = 0.01;
	private static final double Kd = 0;
	
	DerivativeCalculator err_deriv;
	
	DriveTrain l_dt; //local drivetrain object - this is the thing to push in a straight line
	
	private double des_heading_deg = 0;
	
	DriveStraightGyro(DriveTrain dt){
		l_dt = dt;
		err_deriv = new DerivativeCalculator();
	}
	
	/**
	 * given a present heading (as measured from gyro in degrees), drive at fwd_speed_cmd
	 * and maintain the set heading via a PD controller making left/right adjustments to the
	 * direction of travel
	 * @param cur_heading_deg - present heading in degrees as measured by gyro
	 * @param fwd_speed_cmd - desired linear speed.
	 */
	public void driveStraight(double cur_heading_deg, double fwd_speed_cmd){
		//calculate error and PD correction factor - no accounting for the fact that degrees loop back around...
		double cur_err = des_heading_deg - cur_heading_deg;
		double corr_factor = Kp*cur_err + Kd*err_deriv.calcDeriv(cur_err); //PD controller
		
		//calcualte motor commands
		double lm_cmd = Math.max(Math.min(corr_factor + fwd_speed_cmd, 1),-1); //probably need to work out +/- signs here.
		double rm_cmd = Math.max(Math.min(-corr_factor + fwd_speed_cmd, 1),-1);
		
		//set drivetrain motor to sepeed
		l_dt.leftMotor_1.set(lm_cmd);
		l_dt.leftMotor_2.set(lm_cmd);
		l_dt.rightMotor_1.set(rm_cmd);
		l_dt.rightMotor_2.set(rm_cmd);
	}
	
	public void stop(){
		l_dt.leftMotor_1.set(0);
		l_dt.leftMotor_2.set(0);
		l_dt.rightMotor_1.set(0);
		l_dt.rightMotor_2.set(0);
	}
	
	
	/**
	 * Sets the desired heading
	 * @param heading_deg desired heading in degrees
	 */
	public void setHeading(double heading_deg){
		des_heading_deg = heading_deg;
	}
	
	

}
