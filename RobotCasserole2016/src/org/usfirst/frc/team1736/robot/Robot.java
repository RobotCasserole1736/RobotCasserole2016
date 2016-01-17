
package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CLASS OBJECTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
	//Devices on the Robot we will querey
	DriverStation ds = DriverStation.getInstance();
	PowerDistributionPanel pdp = new PowerDistributionPanel();
	BuiltInAccelerometer accel_RIO = new BuiltInAccelerometer();
	
	//Data Logger
	CsvLogger logger = new CsvLogger();
	static final String[] logger_fields = {"MatchTime", 
			                               "ProcessorTime",
			                               "BrownedOut", 
			                               "FMSAttached", 
			                               "SysActive", 
			                               "MeasuredPDPVoltage",
			                               "MeasuredRIOVoltage",
			                               "MeasuredCurrent",
			                               "AccelX",
			                               "AccelY",
			                               "AccelZ"};
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS 
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	//Add overall initialization code here

    }
    
    /**
     * This function is run when the robot enters disabled state. This happens once
     * on startup (since default state is disabled), and then once after the match is done
     */
    public void disabledInit() {
    	
    	//Ensure any open file gets closed
    	logger.close();
 

    }
    
    
    /**
     * This function is called once right before the start of autonomous
     */
    public void autonomousInit() {
    	//Initialize the new log file for autonomous
    	logger.init(logger_fields);

    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	//Add autonomous code here

    	
    	//Log data from this timestep
    	log_data();
    }
    
    /**
     * This function is called once right before the start of teleop
     */
    
    public void teleopInit() {
    	//Initialize the new log file for Teleop
    	logger.init(logger_fields);


    }

    /**
     * This function is called periodically during operator control (teleop)
     */
    public void teleopPeriodic() {
        //Add teleop code here
    	
    	//Log data from this timestep
    	log_data();
    }
    
    /**
     * This function is called just before the start of test mode
     * This is a random comment
     */
    public void testInit() {
    	//Initialize the new log file for Test
    	logger.init(logger_fields);
    
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	//Add test code here
    	
    	
    	//Log data from this timestep
    	log_data();
    
    }
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS 
    ///////////////////////////////////////////////////////////////////////////////////////////////    
    
    /**
     * log_data - call this to write a new line to the .csv log file, assuming it is open.
     * Will also force-write the log file to disk if we're browned out.
     */
    private int log_data() {
    	int ret_val_1 = 0;
    	int ret_val_2 = 0;
    	
    	//Log proper data to file. Order must match that of the variable "logger fields"
    	ret_val_1 = logger.writeData( ds.getMatchTime(),
			    					  Timer.getFPGATimestamp(),
			    				     (ds.isBrownedOut()?1.0:0.0),
			    				     (ds.isFMSAttached()?1.0:0.0),
			    				     (ds.isSysActive()?1.0:0.0),
			    				      pdp.getVoltage(),
			    				      ds.getBatteryVoltage(),
			    			          pdp.getTotalCurrent(),
			    			          accel_RIO.getX(),
			    					  accel_RIO.getY(),
			    					  accel_RIO.getZ()
			    					 );
    	//Check for brownout. If browned out, force write data to log. Just in case we
    	//lose power and nasty things happen, at least we'll know how we died...
    	if(ds.isBrownedOut()) {
    		ret_val_2 = logger.forceSync();
    		System.out.println("Warning - brownout condition detetcted, flushing log buffers...");
    	}
    	
    	return Math.min(ret_val_1, ret_val_2);
    }
    
}
