package org.usfirst.frc.team1736.robot;

import java.io.File;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;


/**
 * Tracks robot runtime over its life. Records data in a special file on the USB Drive. Data is human-readable,
 * and may be merged manually if the USB drive is moved. Data is useful for determining how long various parts last,
 * 
 * @author gerthcm
 *
 */
public class CasseroleHourmeter {
	
	final String HOURMETER_FNAME = "/U/hourmeter/runtime_data.txt";
	
	final int HOURMETER_UPDATE_RATE_MS = 5000;
	
	public double minutesTotal;
	public double minutesDisabled;
	public double minutesTeleop;
	public double minutesAutonomous;
	public double minutesTest;
	public long  numTeleopEnables;
	public long  numAutonomousEnables;
	public long  numTestEnables;
	
	public double prev_call_time = 0;
	public int prev_state = 0; //0 = disabled, 1 = teleop, 2 = auto, 3 = test
	
	DriverStation ds;
	
	private java.util.Timer updater;
	
	
	/**
	 * Initialize the hourmeter. Will read from disk to get the most recent values for the hourmeter, and start updating the file periodically.
	 * If the file does not exist, it will be created initially. This is all you really need to do, unless you happen to want to read the numbers
	 * back over smartdashboard or something like that.
	 */
	CasseroleHourmeter(){
		ds = DriverStation.getInstance();
		
		prev_call_time = Timer.getFPGATimestamp();
		
		if(!checkHourmeterFileExists()){
			initNewHourmeterFile();
		} else {
			readCurrentValuesFromHourmeterFile();
		}
		
		updater.scheduleAtFixedRate(new HourmeterUpdater(), 0, HOURMETER_UPDATE_RATE_MS);
	}
	
	/**
	 * Verify the hourmeter file exists on the filesystem
	 * @return True if file exists, False if not.
	 */
	private boolean checkHourmeterFileExists(){
		File f = new File(HOURMETER_FNAME);
		if(f.exists() && !f.isDirectory()){
			return true;
		} else {
			return false;
		}
	}
	
	private void writeCurrentValuesToHourmeterFile(){
		
	}
	
	private int readCurrentValuesFromHourmeterFile(){
		
		return 0;
	}
	
	private int initNewHourmeterFile(){
		minutesTotal = 0;
		minutesDisabled = 0;
		minutesTeleop = 0;
		minutesAutonomous = 0;
		minutesTest = 0;
		numTeleopEnables = 0;
		numAutonomousEnables = 0;
		numTestEnables = 0;
		
		writeCurrentValuesToHourmeterFile();
		
		return 0;
	}
	
	private void updateHourmeterFile(){
		//Update hour & counts with previous call information
		double delta_time_min = (Timer.getFPGATimestamp() - prev_call_time)/60;
		
		minutesTotal += delta_time_min;
		
		if(ds.isEnabled()){
			if(ds.isOperatorControl()){
				minutesTeleop += delta_time_min;
			} else if (ds.isAutonomous()){
				minutesAutonomous += delta_time_min;
			} else if(ds.isTest()){
				minutesTest += delta_time_min;
			}
		}
		else{
			minutesDisabled += delta_time_min;
		}
		
		numTeleopEnables = 0;
		numAutonomousEnables = 0;
		numTestEnables = 0;
		
		//write all the updated variables to file
		writeCurrentValuesToHourmeterFile();
		
	}
	
	/**
	 * Class for timerTask which will wrapper the fixed-rate call
	 *
	 */
	private class HourmeterUpdater extends TimerTask {
		public void run() {
				updateHourmeterFile();	
		}
	}

}
