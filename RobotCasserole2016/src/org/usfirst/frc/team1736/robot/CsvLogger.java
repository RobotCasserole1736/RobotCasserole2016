/**
 * CSV Logger Class - Provides an API for FRC 1736 Robot Casserole datalogging on the robot during runs
 * Will write lines into a CSV file with a unique name between calls to init() and close(). output_dir is 
 * hardcoded to point to a specific 2016 folder on a flash drive connected to the roboRIO. 
 */

package org.usfirst.frc.team1736.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

/**
 * @author Chris Gerth
 *
 */
public class CsvLogger {
	
	long log_write_index;
	File log_name = null;
	File output_dir = new File("/U/data_captures_2016/"); // USB drive is mounted to /U on roboRIO
	BufferedWriter log_file = null;
	boolean log_open = false;
	
	
	
	/**
	 * init - Determines a unique file name, and opens a file in the data captures directory
	 *        and writes the initial lines to it. 
	 * Input - A set of row titles to write into the file
	 * Output - 0 on file open success, -1 on any errors
	 */
	public int init(String[] data_fields ) {
		
		if(log_open){
			System.out.println("Warning - log is already open!");
			return 0;
		}
		
		log_open = false;
		System.out.println("Initalizing Log file...");
		try {
			//Reset state variables
			log_write_index = 0;
			
			//Determine a unique file name
			log_name = File.createTempFile("log_", ".csv", output_dir);
			
			//Open File
			FileWriter fstream = new FileWriter(log_name, true);
			log_file = new BufferedWriter(fstream);
			
			//Write fixed header data
			log_file.write("INDEX, ");
			
			//Write user-defined header line
			for(String header_txt : data_fields){
				log_file.write(header_txt + ", ");
			}
			
			//End of line
			log_file.write("\n");
		}
		//Catch ALL the errors!!!
		catch(IOException e){
			System.out.println("Error initalizing log file: " + e.getMessage());
			return -1;
		}
		System.out.println("done!");
		log_open = true;
		return 0;
		
	}
	
	
	
	/**
	 * writeData - writes a list of doubles to the output file, assuming it's open.
	 * Input - doubles to write (any number of doubles, each as its own argument)
	 * Output - new line in the output file, or an error if line not written
	 */	
	public int writeData(double... data_elements){
		if(log_open == false){
			System.out.println("Error - Log is not yet opened, cannot write!");
			return -1;
		}
			
		System.out.println("Writing to log file.");
		try {
			
			//write fixed header data
			log_file.write(Double.toString(log_write_index) + ", ");
			//Write user-defined data
			for(double data_val : data_elements){
				log_file.write(Double.toString(data_val) + ", ");
			}
			
			//End of line
			log_file.write("\n");
		}
		//Catch ALL the errors!!!
		catch(IOException e){
			System.out.println("Error writing to log file: " + e.getMessage());
			return -1;
		}

		log_write_index++;
		return 0;
	}
	
	
	
	/**
	 * forceSync - Clears the buffer in memory and forces things to file. Generally a 
	 * good idea to use this as infrequently as possible (because it increases logging overhead),
	 * but definitely use it before the roboRIO might crash without a proper call to the close() method
	 * (ie, during brownout)
	 * Returns 0 on flush success or -1 on failure.
	 */	
	public int forceSync(){
		if(log_open == false){
			System.out.println("Error - Log is not yet opened, cannot sync!");
			return -1;
		}
		try {
			log_file.flush();
		}
		//Catch ALL the errors!!!
		catch(IOException e){
			System.out.println("Error flushing IO stream file: " + e.getMessage());
			return -1;
		}
		
		return 0;
		
	}
	
	
	
	/**
	 * close - closes the log file and ensures everything is written to disk. 
	 * init() must be called again in order to write to the file.
	 */	
	public int close(){
		
		if(log_open == false){
			System.out.println("Warning - Log is not yet opened, nothing to close.");
			return 0;
		}
		
		try {
			log_file.close();
			log_open = false;
		}
		//Catch ALL the errors!!!
		catch(IOException e){
			System.out.println("Error Closing Log File: " + e.getMessage());
			return -1;
		}
		return 0;
		
	}

}
