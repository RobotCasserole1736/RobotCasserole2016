
package org.usfirst.frc.team1736.robot;

import org.usfirst.frc.team1736.lib.Calibration.CalWrangler;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebServer;
import org.usfirst.frc.team1736.lib.WebServer.CassesroleWebStates;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	

	//web server
	CasseroleWebServer webserver;
	
	//Counts how many control loops have been run so far
	int loopCounter;
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS 
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
	public void robotInit() {
    	//Initialize each peripheral
    	
    	//Web server init
    	webserver = new CasseroleWebServer();
    	webserver.startServer();
    	
    }
    
    
    /**
     * This function is called once right before the start of teleop
     */
    @Override
	public void teleopInit() {
    	//Reset the control loop counter
    	loopCounter = 0;
    }

    /** 
     * This function is called periodically during operator control (teleop)
     */
    @Override
	public void teleopPeriodic() {
    	
    	//Read Inputs
    	
    	//Perform periodic control update functions
    	
    	//Set outputs
    	
    	//Update website states
    	CassesroleWebStates.putDouble("Test Value (nounit)", 42.0);
    	CassesroleWebStates.putDouble("Time Since Boot (s)", Timer.getFPGATimestamp());
    	
    	//We just finished a control loop, so update total number of loops run
    	loopCounter++;
    
    }
    
    ///////////////////////////////////////////////////////////////////////////////
    // The below functions may remain empty (for now)
    ///////////////////////////////////////////////////////////////////////////////
    
    /**
     * This function is run when the robot enters disabled state. This happens once
     * on startup (since default state is disabled), and then once after the match is done
     */
    @Override
	public void disabledInit() {

    }
    
    @Override
	public void disabledPeriodic() {
    	
    }
    
    /**
     * This function is called once right before the start of autonomous
     */
    @Override
	public void autonomousInit() {
     
    	
    }

    
    /**
     * This function is called periodically during autonomous
     */
    @Override
	public void autonomousPeriodic() {

    }

    
    /**
     * This function is called just before the start of test mode
     * This is a random comment
     */
    @Override
	public void testInit() {
    
    }
    
    /**
     * This function is called periodically during test mode
     */
    @Override
	public void testPeriodic() {

    }
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS 
    ///////////////////////////////////////////////////////////////////////////////////////////////    


}
