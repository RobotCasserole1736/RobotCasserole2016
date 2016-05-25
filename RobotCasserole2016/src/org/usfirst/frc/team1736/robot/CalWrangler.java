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
	
	ArrayList<Calibration> registeredCals;
	final String calFile = "/U/calibration/present_cal.csv";
	
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
		
		/*Load file, checking for errors*/
		try {
			br = new BufferedReader(new FileReader(calFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//If we dont' have a valid bufferedReader at this point, we have nothing else to try.
		if(br == null){
			System.out.println("Calibration Wrangler Error: Cannot open file " + calFile + " for reading. Leaving all calibrations at default values.");
			resetAllCalsToDefault();
			return -1;
		}
		
		//For lines in cal file
			//for all registered cals
				//If registered cal name matches name in cal file, override it.
		
		//close cal file
		
		
		return 0;
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
		//TODO: Check the cal is not already registered
		registeredCals.add(cal_in);
		return 0;
	}

}
