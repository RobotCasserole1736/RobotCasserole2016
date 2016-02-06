
package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	
	// I'm a silly boy and Idk what I'm doing so I'm just gonna put these variables here, whoop whoop
	int autoMode = 0;
	SendableChooser autoChooser;
	int currentStep = 0;
			
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
	final static int L_Motor_ID1 = 0; //CMG - Confirmed 2-2-2016
	final static int L_Motor_ID2 = 1;
	final static int R_Motor_ID1 = 2;
	final static int R_Motor_ID2 = 3;
	
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
	I2CGyro gyro;
	
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
			                               "AccelZ",
			                               "TaskExecTime",
			                               "CommandedCameraPos",
			                               "ClimbEnable",
			                               "TapeMeasureCmd",
			                               "WinchCmd",
			                               "TapeMeasureLimitSw",
			                               //"GyroMeasAngle",
			                               //"GyroStatus",
			                               "CompressorCurrent",
			                               "LaunchWheelCurrent",
			                               "LaunchWheelActSpeed",
			                               "LaunchWheelDesSpeed"};
	
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
			                              "G",
			                              "mS",
			                              "Index",
			                              "bit",
			                              "cmd",
			                              "cmd",
			                              "bit",
			                              //"deg",
			                              //"bit",
			                              "A",
			                              "A",
			                              "RPM",
			                              "RPM"};
	
	//Variable for metric logging
	private double prev_loop_start_timestamp = 0;
	private double loop_time_elapsed = 0;
	
	//Joysticks
	Joystick joy1;
	Joystick joy2;
	//Drive Train
	DriveTrain driveTrain;
	//climber mechanism
	Climb Climber;
	//Launch Motor
	Shooter launchMotor;
	OttoShifter shifter;
	DerivativeCalculator wheel_speed;
	//Motors
	VictorSP L_Motor_1;
	VictorSP L_Motor_2;
	VictorSP R_Motor_1;
	VictorSP R_Motor_2;
	//Camera servo mount
	CameraServoMount csm;
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS 
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	//Stuff for Autonomous
    	autoChooser = new SendableChooser();
    	autoChooser.addObject("Some Mode Name", 0);
    	autoChooser.addObject("anoter mode name", 1);
    	autoChooser.addDefault("default mode name",2);
    	SmartDashboard.putData("Auto Mode Chooser", autoChooser);
    	
    	//Add overall initialization code here
    	ds = DriverStation.getInstance();
    	pdp = new PowerDistributionPanel();
    	bpe = new BatteryParamEstimator(BPE_length);
    	bpe.setConfidenceThresh(10.0);
    	accel_RIO = new BuiltInAccelerometer();
    	csm = new CameraServoMount();
    	//gyro = new I2CGyro();
    	
    	//Motors
    	L_Motor_1 = new VictorSP(L_Motor_ID1);
    	L_Motor_2 = new VictorSP(L_Motor_ID2);
    	R_Motor_1 = new VictorSP(R_Motor_ID1);
    	R_Motor_2 = new VictorSP(R_Motor_ID2);
    	//Drivetrain
    	driveTrain = new DriveTrain(L_Motor_1, L_Motor_2, R_Motor_1, R_Motor_2, pdp, bpe);
    	shifter = new OttoShifter();
    	wheel_speed = new DerivativeCalculator();
    	//Peripherials
    	Climber=new Climb();
    	launchMotor = new Shooter();
    	//Joysticks
    	joy1 = new Joystick(JOY1_INT);
    	joy2 = new Joystick(JOY2_INT);
    	
    	//Ensure intake starts in proper position
    	Pneumatics.intakeUp();
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
    	SmartDashboard.getNumber("Autonomous Mode:");
    	autoMode = (int) autoChooser.getSelected();
    	//Compressor starts automatically
    	
    	//reset gyro angle to 0
    	//gyro.reset_gyro_angle();

    	//init the task timing things
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	loop_time_elapsed = 0;
    	
    	//Raise intake to prevent damage in auto.
    	Pneumatics.intakeUp();
    	
    	
    	
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    switch(autoMode){
    case 0: //Just move to in front of the defense
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	
    	driveTrain.drive(0.8, 0);
    	
    	bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
    	log_data();
    	loop_time_elapsed = Timer.getFPGATimestamp() - prev_loop_start_timestamp;
    	break;    	
    case 1: //Case 0 + go over low bar
    	if (currentStep == 1)
    	{		
    		prev_loop_start_timestamp = Timer.getFPGATimestamp();
    		bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
    		driveTrain.drive(0.8, 0);
    		log_data();
    		loop_time_elapsed = Timer.getFPGATimestamp() - prev_loop_start_timestamp;
    	}    	
    	if (currentStep == 2)
    	{
    		prev_loop_start_timestamp = Timer.getFPGATimestamp();
			bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
			driveTrain.drive(0.8, 0);
			log_data();
			loop_time_elapsed = Timer.getFPGATimestamp() - prev_loop_start_timestamp;
    	}
    	break;     	
    case 2: //Case 0 + go over rugged terrain
    	if (currentStep == 1)
    	{		
    		prev_loop_start_timestamp = Timer.getFPGATimestamp();
    		bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
    		log_data();
    		driveTrain.drive(0.8, 0);
    		loop_time_elapsed = Timer.getFPGATimestamp() - prev_loop_start_timestamp;
    	}    	
    	if (currentStep == 2)
    	{
    		prev_loop_start_timestamp = Timer.getFPGATimestamp();
			bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
			log_data();
			driveTrain.drive(0.8, 0);
			loop_time_elapsed = Timer.getFPGATimestamp() - prev_loop_start_timestamp;
    	}
    	break; 
    	
    	//Execution time metric - this must be first!
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	
    	//Add autonomous code here
    	//Estimate battery Parameters
    	bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
    		
    	
    	//Log data from this timestep
    	log_data();
    	
    	//Execution time metric - this must be last!
    	loop_time_elapsed = Timer.getFPGATimestamp() - prev_loop_start_timestamp
    }
    
    /**
     * This function is called once right before the start of teleop
     */
    
    public void teleopInit() {
    	//Initialize the new log file for Teleop
    	logger.init(logger_fields, units_fields);

    	//compressor starts automatically
    	
    	//init the task timing things
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	loop_time_elapsed = 0;
    }

    /**
     * This function is called periodically during operator control (teleop)
     */
    public void teleopPeriodic() {
    	//Execution time metric - this must be first!
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	
    	//Estimate battery Parmaeters
    	bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
    	
        //Run Drivetrain
    	driveTrain.arcadeDrive(joy1.getRawAxis(XBOX_LSTICK_YAXIS), joy1.getRawAxis(XBOX_RSTICK_XAXIS), squaredInputs);
    	double left_speed = Math.abs(driveTrain.leftEncoder.getRate());
    	double right_speed = Math.abs(driveTrain.rightEncoder.getRate());
    	double net_speed = Math.max(left_speed,right_speed);
    	shifter.OttoShifterPeriodic(net_speed, wheel_speed.calcDeriv(net_speed), accel_RIO.getX(), pdp.getTotalCurrent(), joy1.getRawButton(XBOX_LEFT_BUTTON), joy1.getRawButton(XBOX_RIGHT_BUTTON));
    	if(shifter.gear){
    		System.out.println("high_gear");
    		Pneumatics.shiftToHighGear();
    	}
    	else{
    		System.out.println("low_gear");
    		Pneumatics.shiftToLowGear();
    	}
    	//Update camera position
    	processCameraAngle();
    	
    	//Run climber
    	Climber.periodicClimb(joy2.getRawButton(XBOX_START_BUTTON), joy2.getRawAxis(XBOX_LSTICK_YAXIS), joy2.getRawAxis(XBOX_RTRIGGER_AXIS));
    	
    	//Adjust intake position based on driver commands
    	if(joy2.getRawButton(XBOX_A_BUTTON))
    		Pneumatics.intakeDown();
    	if(joy2.getRawButton(XBOX_Y_BUTTON))
    		Pneumatics.intakeUp();
    	
    	//Log data from this timestep
    	log_data();
    	System.out.println(driveTrain.getMemes());
    	
    	//Execution time metric - this must be last! Even after memes!
    	loop_time_elapsed = Timer.getFPGATimestamp() - prev_loop_start_timestamp;
    }
    
    /**
     * This function is called just before the start of test mode
     * This is a random comment
     */
    public void testInit() {
    	//Initialize the new log file for Test
    	logger.init(logger_fields, units_fields);
    	
    	//init the task timing things
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	loop_time_elapsed = 0;
    
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	//Execution time metric - this must be first!
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	//Add test code here
    	
    	//Log data from this timestep
    	log_data();
    	//Execution time metric - this must be last!
    	loop_time_elapsed = Timer.getFPGATimestamp() - prev_loop_start_timestamp;
    
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
			    					  accel_RIO.getZ(),
			    				      loop_time_elapsed*1000.0,
			    					  csm.curCamPos.ordinal(),
			    					  (joy2.getRawButton(XBOX_START_BUTTON)?1.0:0.0),
			    					  Climber.tapemotor.get(),
			    					  Climber.winchmotor1.get(),
			    					  (Climber.tapetrigger.get()?1.0:0.0),
			    					 // gyro.get_gyro_angle()%360,
			    					 // (gyro.get_gyro_status()?1.0:0.0),
			    					  Pneumatics.getCurrent(),
			    					  launchMotor.getCurrent(),
			    					  launchMotor.getActSpeed(),
			    					  launchMotor.getDesSpeed()
			    					 );
    	//Check for brownout. If browned out, force write data to log. Just in case we
    	//lose power and nasty things happen, at least we'll know how we died...
    	if(ds.isBrownedOut()) {
    		ret_val_2 = logger.forceSync();
    		System.out.println("Warning - brownout condition detetcted, flushing log buffers...");
    	}
    	
    	return Math.min(ret_val_1, ret_val_2);
    }
    
    /**
     * Sets camera to the right position based on driver inputs
     */  
    private void processCameraAngle(){
    	
    	if(joy1.getRawButton(XBOX_Y_BUTTON)){
    		csm.setCameraPos(CamPos.DRIVE_FWD);
    	}
    	else if(joy1.getRawButton(XBOX_A_BUTTON)){
    		csm.setCameraPos(CamPos.DRIVE_REV);
    	}
    	else if(joy1.getRawButton(XBOX_B_BUTTON)){
    		csm.setCameraPos(CamPos.SHOOT);
    	}
    	else if(joy1.getRawButton(XBOX_X_BUTTON)){
    		csm.setCameraPos(CamPos.CLIMB);
    	}
    	
    }
    
}
