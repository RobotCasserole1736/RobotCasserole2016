
package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	CsvLogger logger = new CsvLogger();
	static final String[] logger_fields = {"MatchTime", "ProcessorTime", "MatchState", "MeasuredSystemVoltage", "MeasuredSystemCurrent"};
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	//Add overall initilization code here
    	//A Second comment added by chris gerth! This can be removed in the future.
    	//test
    	//Add overall initialization code here
    	//You're an VERY NICE PERSON, Jason.

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
     * This function is called periodically during autonomous
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
    	//second comment!

    }
    
    public void teleopInit() {
    	//Initialize the new log file for Teleop
    	logger.init(logger_fields);


    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        //Add teleop code here
    	
    	
    	logger.writeData(0,1,3,0,5);
    }
    
    /**
     * This function is called periodically during test mode
     * This is a random comment
     */
    public void testInit() {
    	//Initialize the new log file for Test
    	logger.init(logger_fields);
    
    }
    
    /**
     * This function is called periodically during test mode
     * This is a random comment
     */
    public void testPeriodic() {
    	//Add teleop code here
    
    }
    
}
