package org.usfirst.frc.team1736.robot;


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
* Single Calibration. Describes a piece of data which is usually constant, but can
* be overridden by a cal wrangler from a csv file on the RIO's filesystem. This enables
* a software team to control what a pit crew has control over (Shooter speed is a good
* candidate. Port number for left drivetrain motor A is a bad candidate). 
* USAGE:    
* <ol>   
* <li>Instantiate a CalManager first</li> 
* <li>Instantiate </li> 
* <li>Call start() method to begin background execution of algorithm.</li>    
* </ol>
* 
* 
*/


public class Calibration {
	public final double default_val;
	private final CalWrangler wrangler;
	private final String name;
	public double cur_val;
	public boolean overridden;
	
	
	Calibration(String name_in, double default_val_in, CalWrangler wrangler_in){
		
		/*default stuff and stuff*/
		default_val = default_val_in;
		wrangler = wrangler_in;
		name = name_in;
		overridden = false;
		
		wrangler.register(this);
		
	}
	
	public double get(){
		return cur_val;
	}
	
	public double getDefault(){
		return default_val;
	}
	
	

}
