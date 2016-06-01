package org.usfirst.frc.team1736.robot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	
	public double prev_call_time;
	public OperationState prev_state; 
	
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
		prev_state = OperationState.UNKNOWN;
		
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
		try{
			//Open File
			FileWriter fstream = new FileWriter(HOURMETER_FNAME, true);
			BufferedWriter log_file = new BufferedWriter(fstream);
			
			//Write the lines. Changes here will need corresponding updates in the read function.
			log_file.write("TOTAL_MINUTES:"+Double.toString(minutesTotal));
			log_file.write("DISABLED_MINUTES:"+Double.toString(minutesDisabled));
			log_file.write("TELEOP_MINUTES:"+Double.toString(minutesTeleop));
			log_file.write("AUTO_MINUTES:"+Double.toString(minutesAutonomous));
			log_file.write("TEST_MINUTES:"+Double.toString(minutesTest));
			log_file.write("TELEOP_ENABLES:"+Long.toString(numTeleopEnables));
			log_file.write("AUTO_ENABLES:"+Long.toString(numAutonomousEnables));
			log_file.write("TEST_ENABLES:"+Long.toString(numTestEnables));
			
		} catch	(IOException e){
			System.out.println("Error writing to hourmeter file:" + e.getMessage());
			return;
		}


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
		OperationState cur_state = OperationState.UNKNOWN;
		
		
		//Update total time
		minutesTotal += delta_time_min;
		
		//Update individual time values and present state
		if(ds.isEnabled()){
			if(ds.isOperatorControl()){
				minutesTeleop += delta_time_min;
				cur_state = OperationState.TELEOP;
			} else if (ds.isAutonomous()){
				minutesAutonomous += delta_time_min;
				cur_state = OperationState.AUTO;
			} else if(ds.isTest()){
				minutesTest += delta_time_min;
				cur_state = OperationState.TEST;
			}
		}
		else{
			minutesDisabled += delta_time_min;
			cur_state = OperationState.DISABLED;
		}
		
		//If we've changed operational state, record that
		if(cur_state != prev_state){
			if(cur_state == OperationState.TELEOP)
				numTeleopEnables++;
			else if(cur_state == OperationState.AUTO)
				numAutonomousEnables++;
			else if(cur_state == OperationState.TEST)
				numTestEnables++;	
		}
		
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
