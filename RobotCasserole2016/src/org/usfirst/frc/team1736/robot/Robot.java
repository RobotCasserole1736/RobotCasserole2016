
package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
		final static int JOY1_INT = 0;
		final static int JOY2_INT = 1;
		//soon
		final static boolean SINGLE_JOYSTICK_IS_BEST_JOYSTICK = false;
		
		//-Controller Buttons
		final static int XBOX_A_BUTTON = 1;
		final static int XBOX_B_BUTTON = 2;
		final static int XBOX_X_BUTTON = 3;
		final static int XBOX_Y_BUTTON = 4;
		final static int XBOX_LEFT_BUTTON = 5;
		final static int XBOX_RIGHT_BUTTON = 6;
		final static int XBOX_SELECT_BUTTON = 7;
		final static int XBOX_START_BUTTON = 8;
		final static int XBOX_LSTICK_BUTTON = 9;
		final static int XBOX_RSTICK_BUTTON = 10;
		
		//-Controller Axes
		final static int XBOX_LSTICK_XAXIS = 0;
		final static int XBOX_LSTICK_YAXIS = 1;
		final static int XBOX_LTRIGGER_AXIS = 2;
		final static int XBOX_RTRIGGER_AXIS = 3;
		final static int XBOX_RSTICK_XAXIS = 4;
		final static int XBOX_RSTICK_YAXIS = 5;
		
		//-Controller D-Pad POV Hat
		final static int XBOX_DPAD_POV = 0;
		
		//-Vision
		final static String[] GRIPArgs = new String[] {
		        "/usr/local/frc/JRE/bin/java", "-jar",
		        "/home/lvuser/grip.jar", "/home/lvuser/project.grip" };
		
		//-Motor IDs
		final static int L_Motor_ID1 = 2;
		final static int L_Motor_ID2 = 3;
		final static int R_Motor_ID1 = 0;
		final static int R_Motor_ID2 = 1;
		
		//-Square joystick input?
		final static boolean squaredInputs = true;
		
		//-BatteryParamEstimator length -- CHRIS MANAGES THIS CONSTANT
		final static int BPE_length = 200; 
		
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CLASS OBJECTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
	//Devices on the Robot we will query
	DriverStation ds;
	PowerDistributionPanel pdp;
	BatteryParamEstimator bpe;
	BuiltInAccelerometer accel_RIO;
	
	//Data Logger
	CsvLogger logger = new CsvLogger();
	static final String[] logger_fields = {"TIME",
			                               "MatchTime", 
			                               "BrownedOut", 
			                               "FMSAttached", 
			                               "SysActive", 
			                               "MeasuredPDPVoltage",
			                               "MeasuredRIOVoltage",
			                               "MeasuredCurrent",
			                               "EstLeftDTCurrent",
			                               "EstRightDTCurrent",
			                               "EstBattESR",
			                               "EstBatVoc",
			                               "EstBatConfidence",
			                               "EstVsys",
			                               "DriverFwdRevCmd",
			                               "DriverLftRtCmd",
			                               "LeftDTVoltage",
			                               "RightDTVoltage",
			                               "LeftDTSpeed",
			                               "RightDTSpeed",
			                               "AccelX",
			                               "AccelY",
			                               "AccelZ"};
	static final String[] units_fields = {"sec",
			                              "sec",
			                              "bit",
			                              "bit",
			                              "bit",
			                              "V",
			                              "V",
			                              "A",
			                              "A",
			                              "A",
			                              "Ohm",
			                              "V",
			                              "bit",
			                              "V",
			                              "cmd",
			                              "cmd",
			                              "V",
			                              "V",
			                              "RPM",
			                              "RPM",
			                              "G",
			                              "G",
			                              "G"};
	
	//Joysticks
	Joystick joy1;
	Joystick joy2;
	//Drive Train
	DriveTrain driveTrain;
	//climber mechanism
	Climb Climber;
	//Motors
	VictorSP L_Motor_1;
	VictorSP L_Motor_2;
	VictorSP R_Motor_1;
	VictorSP R_Motor_2;
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS 
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	//Add overall initialization code here
    	ds = DriverStation.getInstance();
    	pdp = new PowerDistributionPanel();
    	bpe = new BatteryParamEstimator(BPE_length);
    	bpe.setConfidenceThresh(10.0);
    	accel_RIO = new BuiltInAccelerometer();
    	//Motors
    	L_Motor_1 = new VictorSP(L_Motor_ID1);
    	L_Motor_2 = new VictorSP(L_Motor_ID2);
    	R_Motor_1 = new VictorSP(R_Motor_ID1);
    	R_Motor_2 = new VictorSP(R_Motor_ID2);
    	//Drivetrain
    	driveTrain = new DriveTrain(L_Motor_1, L_Motor_2, R_Motor_1, R_Motor_2, pdp, bpe);
    	Climber=new Climb();
    	//Joysticks
    	joy1 = new Joystick(JOY1_INT);
    	joy2 = new Joystick(JOY2_INT);
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
    	logger.init(logger_fields, units_fields);

    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	//Add autonomous code here
    	//Estimate battery Parmaeters
    	bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
    	
    	//Log data from this timestep
    	log_data();
    }
    
    /**
     * This function is called once right before the start of teleop
     */
    
    public void teleopInit() {
    	//Initialize the new log file for Teleop
    	logger.init(logger_fields, units_fields);

    }

    /**
     * This function is called periodically during operator control (teleop)
     */
    public void teleopPeriodic() {
    	//Estimate battery Parmaeters
    	bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
    	
        //Run Drivetrain
    	driveTrain.arcadeDrive(joy1.getRawAxis(XBOX_LSTICK_YAXIS), joy1.getRawAxis(XBOX_RSTICK_XAXIS), squaredInputs);
    	
    	
    	
    	
    	
    	Climber.periodicClimb(joy2.getRawButton(XBOX_START_BUTTON), joy2.getRawAxis(XBOX_LSTICK_YAXIS), joy2.getRawAxis(XBOX_RTRIGGER_AXIS));
    	//Log data from this timestep
    	log_data();
    	
    }
    
    /**
     * This function is called just before the start of test mode
     * This is a random comment
     */
    public void testInit() {
    	//Initialize the new log file for Test
    	logger.init(logger_fields, units_fields);
    
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
    	
    	//Sorta temp - there's no nice way to expose this yet, so i'll do the calcualtion here.
    	double dt_leftIest = driveTrain.leftCCE.getCurrentEstimate(driveTrain.leftEncoder.getRate(), driveTrain.leftMotor_1.get());
    	double dt_rightIest = driveTrain.rightCCE.getCurrentEstimate(driveTrain.rightEncoder.getRate(), driveTrain.rightMotor_1.get());
    	
    	//Log proper data to file. Order must match that of the variable "logger fields"
    	ret_val_1 = logger.writeData( Timer.getFPGATimestamp(),
    								  ds.getMatchTime(), 
			    				     (ds.isBrownedOut()?1.0:0.0),
			    				     (ds.isFMSAttached()?1.0:0.0),
			    				     (ds.isSysActive()?1.0:0.0),
			    				      pdp.getVoltage(),
			    				      ds.getBatteryVoltage(),
			    			          pdp.getTotalCurrent(),
			    			          dt_leftIest,
			    			          dt_rightIest,
			    			          bpe.getEstESR(),
    								  bpe.getEstVoc(),
    								  (bpe.getConfidence()?1.0:0.0),
    								  bpe.getEstVsys(dt_rightIest + dt_leftIest + 5), //total guess at 5A background I draw
			    			          joy1.getRawAxis(XBOX_LSTICK_YAXIS),
			    			          joy1.getRawAxis(XBOX_RSTICK_XAXIS),
			    			          driveTrain.leftMotor_1.get(),
			    			          -driveTrain.rightMotor_1.get(),
			    			          driveTrain.leftEncoder.getRate()*9.5492, //report rate in RPM
			    			          driveTrain.rightEncoder.getRate()*9.5492,
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
