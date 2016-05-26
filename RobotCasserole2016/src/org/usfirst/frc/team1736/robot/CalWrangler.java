package org.usfirst.frc.team1736.robot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


///////////////////////////////////////////////////////////////////////////////
//Copyright (c) FRC Team 1736 2016. See the License file. 
//
//Can you use this code? Sure! We're releasing this under GNUV3, which 
//basically says you can take, modify, share, publish this as much as you
//want, as long as you don't make it closed source.
//
//If you do find it useful, we'd love to hear about it! Check us out at
//http://robotcasserole.org/ and leave us a message!
///////////////////////////////////////////////////////////////////////////////

/**
* DESCRIPTION:
* <br>
* Calibration Wrangler. Manages the full set of calibrations in the software. Can override calibration values
* based on a .csv file at a specific location on the RIO's filesystem
* <br>
* <br>
* Calibration file format:
* <br>
* calibrationName,value
* <br>
* <br>
* Ex:
* ShooterSpeed,3200.5 <br>
* Pgain,0.125 <br>
* <br>
* <br>
* USAGE:    
* <ol>   
* <li>Instantiate a CalManager first</li> 
* <li>Each calibration will register itself with this wrangler upon instantiation</li> 
* <li>At the start of teleop or autonomous, call the loadCalValues() method to update cal values based on .csv file values </li>    
* </ol>
* 
* 
* 
*/

public class CalWrangler {
	
	private final int CAL_NAME_COL = 0;
	private final int CAL_VAL_COL = 1;
	private final int NUM_COLUMNS = 2;
	
	ArrayList<Calibration> registeredCals;
	final String calFile = "/U/calibration/present_cal.csv";
	
	/**
	 * Constructor. Just does init on internal values.
	 */
	
	CalWrangler(){
		registeredCals = new ArrayList<Calibration>(0);
		return;
	}
	
	/**
	 * Reads from the calibration .csv file and overwrites present calibration values
	 * specified. Prints warnings to screen if odd things happen. Will attempt to override
	 * any values possible, but on failure will just leave the values at default.
	 * @return 0 on success, -1 if cal file not found, 
	 */
	public int loadCalValues(){
		BufferedReader br = null;
		String str_line;
		boolean errors_present = false;
		boolean match_found = false;
		
		resetAllCalsToDefault();
		
		/*Load file, checking for errors*/
		try {
			br = new BufferedReader(new FileReader(calFile));
			
			//For lines in cal file
			while((str_line = br.readLine()) != null){
				//Split line into each tokenized part
				String[] line_parts = str_line.trim().split(",");
				
				//Check that the line is the right size
				if(line_parts.length != NUM_COLUMNS){
					System.out.println("Warning: Calibration Wrangler: line does not have correct number of columns. Got " + Integer.toString(line_parts.length) + ", but expected " + Integer.toString(NUM_COLUMNS) + ". Do not know how to process " + str_line);
					continue;
				}
				
				match_found = false;
				//for all registered cals...
				for(Calibration cal :  registeredCals){
					//Skip empty lines
					if(line_parts[CAL_NAME_COL].trim().equalsIgnoreCase("")){
						continue;
					}
					
					//If registered cal name matches name in cal file, override it.
					if(cal.name.equals(line_parts[CAL_NAME_COL].trim())){
						if(match_found == false){
							match_found = true;
							try{
								double override_val = Double.parseDouble(line_parts[CAL_VAL_COL].trim());
								if(override_val < cal.min_cal){
									System.out.println("Warning: Calibration Wrangler: " + line_parts[CAL_NAME_COL] + " was overridden to " + Double.toString(override_val) + ", but that override value is smaller than the minimum. Overriding to minimum value of " + Double.toString(cal.min_cal));
									cal.cur_val = cal.min_cal;
								} else if (override_val > cal.max_cal){
									System.out.println("Warning: Calibration Wrangler: " + line_parts[CAL_NAME_COL] + " was overridden to " + Double.toString(override_val) + ", but that override value is larger than the maximum. Overriding to maximum value of " + Double.toString(cal.max_cal));
									cal.cur_val = cal.max_cal;
								} else{
									cal.cur_val = override_val;
									System.out.println("Info: Calibration Wrangler: " + cal.name + " was overridden to " + Double.toString(cal.cur_val));
								}
								cal.overridden = true;
								
							}catch(NumberFormatException e){
								System.out.println("Warning: Calibration Wrangler: " + line_parts[CAL_NAME_COL] + " was overridden to " + line_parts[CAL_VAL_COL] + ", but that override value is not recognized as a number. No override applied.");
								cal.overridden = false;
							}	
						} else {
							System.out.println("Warning: Calibration Wrangler: " + line_parts[CAL_NAME_COL].trim() + " has been overriden more than once. Only first override will apply.");
						}
					}
				}
				
				if(match_found == false){
					System.out.println("Warning: Calibration Wrangler: Override was specified for " + line_parts[CAL_NAME_COL] + " but this calibration is not registered with the wrangler. No value overriden.");
				}
			}
			
			//close cal file
			if(br != null){
				br.close();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			errors_present = true;
		} catch (IOException e) {
			e.printStackTrace();
			errors_present = true;
		} finally {
			if (br != null) {
				System.out.println("Error: Calibration Wrangler: Cannot open file " + calFile + " for reading. Leaving all calibrations at default values.");
				resetAllCalsToDefault();
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//Indicate any errors
		if(errors_present){
			return -1;
		}else{
			return 0;
		}

	}
	
	/**
	 * Resets all registered calibrations back to default values
	 * @return 0 on success, nonzero on failure
	 */
	
	public int resetAllCalsToDefault(){
		for(Calibration cal :  registeredCals){
			cal.overridden = false;
		}
		return 0;
	}
	
	/**
	 * Register a calibration with the wrangler. Registration adds a reference to the calibration to the wrangler so
	 * when the wrangler is called upon to update calibration values, it knows which values it should be changing.
	 * This function is called automatically by the constructor for calibrations. Unless something very intersting is happening,
	 * the user should never have to call it.
	 * @param cal_in The calibration to add to this wrangler.
	 * @return 0 on success, nonzero on failure
	 */
	public int register(Calibration cal_in){
		int ret_val = 0;
		if(registeredCals.contains(cal_in)){
			System.out.println("Warning: Calibration Wrangler: " + cal_in.name + " has already been added to the cal wrangler. Nothing done.");
			ret_val = -1;
		} else {
			registeredCals.add(cal_in);
			ret_val = 0;
		}
		return ret_val;
	}

}
