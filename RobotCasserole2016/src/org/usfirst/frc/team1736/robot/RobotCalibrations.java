package org.usfirst.frc.team1736.robot;

public class RobotCalibrations {

	public static CalWrangler main_wrangler  = new CalWrangler();
	public static Calibration cal_ShooterP = new Calibration("ShooterP", 0.0008, main_wrangler);
	public static Calibration cal_ShooterI = new Calibration("ShooterI", 0.000001, main_wrangler);
	public static Calibration cal_ShooterD = new Calibration("ShooterD", 0.00004, main_wrangler);
	public static Calibration cal_ShooterF = new Calibration("ShooterF", 0.0001737, main_wrangler);
	public static Calibration cal_ShooterLaunchRPM = new Calibration("ShooterLaunchRPM", 4500, main_wrangler, 3000, 5500);
	
}
