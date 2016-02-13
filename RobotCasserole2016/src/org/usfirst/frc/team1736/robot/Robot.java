
package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
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
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS AND TUNE
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
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
	final static int XBOX_BACK_BUTTON = 7;
	final static int XBOX_START_BUTTON = 8;
	final static int XBOX_LSTICK_BUTTON = 9;
	final static int XBOX_RSTICK_BUTTON = 10;
	
	// I'm a silly boy and Idk what I'm doing so I'm just gonna put these variables here, whoop whoop
	int autoMode = 0;
	SendableChooser autoChooser;
	Timer autoTimer = new Timer();
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
	final static int DT_LF_MOTOR_PWM_CH = 0; //CMG - Confirmed 2-2-2016
	final static int DT_LB_MOTOR_PWM_CH = 1;
	final static int DT_RF_MOTOR_PWM_CH = 2;
	final static int DT_RB_MOTOR_PWM_CH = 3;
	
	//-Motor PDP channel hookups for measuring current draw
	final static int DT_RF_PDP_CH = 0;
	final static int DT_RB_PDP_CH = 1;
	final static int DT_LF_PDP_CH = 15;
	final static int DT_LB_PDP_CH = 14;
	final static int INTAKE_PDP_CH = 2;
	final static int SHOOTER_PDP_CH = 3;
	final static int TAPE_PDP_CH = 4;
	final static int WINCH_1_PDP_CH = 13;
	final static int WINCH_2_PDP_CH = 12;
	final static int SP_DB_ARM_PDP_CH = 11;
	
	//-Square joystick input?
	final static boolean squaredInputs = true;
	
	//Battery Param Est 
	final static int BPE_length = 200; //Window length
	final static double BPE_confidenceThresh_A = 10.0;
	
	
	//Data Logging
	static final boolean enable_logging = true; //Set to false to disable logging
	static final String[] logger_fields = {"TIME", //First field must always be "TIME"
            "MatchTime", 
            "BrownedOut", 
            "FMSAttached", 
            "SysActive", 
            "MeasPDPVoltage",
            "MeasRIOVoltage",
            "MeasBattDrawCurrent",
            "MeasDT_LF_PDP_DrawCurrent",
            "MeasDT_LB_PDP_DrawCurrent",
            "MeasDT_RF_PDP_DrawCurrent",
            "MeasDT_RB_PDP_DrawCurrent",
            "MeasIntakeMotorPDPDrawCurrent",
            "MeasShooterMotorPDPDrawCurrent",
            "MeasTapeMotorPDPDrawCurrent",
            "MeasWinchMotor1PDPDrawCurrent",
            "MeasWinchMotor2PDPDrawCurrent",
            "MeasSpDbMotorPDPDrawCurrent",
            "PDPTemperature",
            "ActLeftDTCurrent",
            "ActRightDTCurrent",
            "EstLeftDTCurrent",
            "EstRightDTCurrent",
            "DTCmdLimitFactor",
            "EstBattESR",
            "EstBatVoc",
            "EstBatConfidence",
            "EstVsys",
            "DriverFwdRevCmd",
            "DriverLftRtCmd",
            "LeftDTCmd",
            "RightDTCmd",
            "LeftDTMotorSpeed",
            "RightDTMotorSpeed",
            "LeftDTWheelSpeed",
            "RightDTWheelSpeed",
            "LeftDTWheelDistance",
            "RightDTWheelDistance",
            "AccelX",
            "AccelY",
            "AccelZ",
            "TaskExecTime",
            "CommandedCameraPos",
            "ClimbEnable",
            "TapeMeasureMotorCmd",
            "WinchMotorCmd",
            "TapeMeasureLimitSw",
            "GyroMeasAngle",
            "GyroStatus",
            "CompressorCurrent",
            "LaunchWheelCurrent",
            "LaunchWheelMotorCmd",
            "LaunchWheelActSpeed",
            "LaunchWheelDesSpeed",
            "AutoDnShftWheelSpeedTrigger",
            "AutoDnShftWheelAccelTrigger",
            "AutoDnShftBodyAccelTrigger",
            "AutoDnShftCurrentDrawTrigger",
            "ActualGear",
            "DriverCmdInvertedControls",
            "PneumaticPress",
            "SquishSensorReading",
            "AutonomousStep",
            "ShooterMotorStalled",
            "IntakeMotorCmd"};

    static final String[] units_fields = {"sec", //TIME must always be in sec
           "sec",
           "bit",
           "bit",
           "bit",
           "V",
           "V",
           "A",
           "A",
           "A",
           "A",
           "A",
           "A",
           "A",
           "A",
           "A",
           "A",
           "A",
           "degC",
           "A",
           "A",
           "A",
           "A",
           "Pct",
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
           "RPM",
           "RPM",
           "ft",
           "ft",
           "G",
           "G",
           "G",
           "mS",
           "Index",
           "bit",
           "cmd",
           "cmd",
           "bit",
           "deg",
           "bit",
           "A",
           "cmd",
           "A",
           "RPM",
           "RPM",
           "bit",
           "bit",
           "bit",
           "bit",
           "T-High/F-Low",
           "bit",
           "PSI",
           "val",
           "val",
           "bit",
           "cmd"};
		
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
	//Variable for metric logging
	private double prev_loop_start_timestamp = 0;
	private double loop_time_elapsed = 0;
	
	//Joysticks
	Joystick joy1;
	Joystick joy2;
	//Drive Train
	DriveTrain driveTrain;
	boolean cmdInvCtrls = false;
	//climber mechanism
	Climb Climber;
	//Launch Motor
	Shooter launchMotor;
	MotorDiagnostic shooterDiagnostics;
	//Intake/Launch state machine
	StateMachine intakeLauncherSM;
	//Drawbridge arm
	DrawbridgeArmControls DBAC;
	//Shifting Algorithm
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
    	//Initialize each peripheral

    	//Stuff for Autonomous
    	autoChooser = new SendableChooser();
    	autoChooser.addObject("Drive Up To Defense (no cross)", 0);
    	autoChooser.addObject("Cross Low Bar (encoders)", 1);
    	autoChooser.addDefault("Cross Uneven Defense (timer+gyro)",2);
    	autoChooser.addDefault("Do Nothing",-1);
    	SmartDashboard.putData("Auto Mode Chooser", autoChooser);
    	
    	//Other Peripherials
    	ds = DriverStation.getInstance();
    	pdp = new PowerDistributionPanel();
    	bpe = new BatteryParamEstimator(BPE_length);
    	bpe.setConfidenceThresh(BPE_confidenceThresh_A);
    	accel_RIO = new BuiltInAccelerometer();    	csm = new CameraServoMount();
    	gyro = new I2CGyro(); //this will cal the gyro - don't touch robot which this happens!
    	
    	//Motors - Drivetrain
    	L_Motor_1 = new VictorSP(DT_LF_MOTOR_PWM_CH);
    	L_Motor_2 = new VictorSP(DT_LB_MOTOR_PWM_CH);
    	R_Motor_1 = new VictorSP(DT_RF_MOTOR_PWM_CH);
    	R_Motor_2 = new VictorSP(DT_RB_MOTOR_PWM_CH);
    	
    	//Drivetrain
    	driveTrain = new DriveTrain(L_Motor_1, L_Motor_2, R_Motor_1, R_Motor_2, pdp, bpe);
    	shifter = new OttoShifter();
    	wheel_speed = new DerivativeCalculator();
    	
    	//Peripherals
    	Climber=new Climb();
    	launchMotor = new Shooter();
    	intakeLauncherSM = new StateMachine(launchMotor);
    	DBAC = new DrawbridgeArmControls ();
    	shooterDiagnostics = new MotorDiagnostic();
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
    	
    	if(enable_logging){
	    	//Ensure any open file gets closed
	    	logger.close();
    	}
 

    }
    
    
    /**
     * This function is called once right before the start of autonomous
     */
    public void autonomousInit() {
    	if(enable_logging){
	    	//Initialize the new log file for autonomous
	    	logger.init(logger_fields, units_fields);
    	}

    	//compressor starts automatically, but just in case...
    	Pneumatics.startCompressor();

    	SmartDashboard.getNumber("Autonomous Mode:");
    	autoMode = (int) autoChooser.getSelected();
    	
    	//reset gyro angle to 0
    	gyro.reset_gyro_angle();
    	
    	//reset encoders to 0
    	driveTrain.leftEncoder.reset();
		driveTrain.rightEncoder.reset();

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
    	
    	//Execution time metric - this must be first!
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	
    
	    switch(autoMode){
	    case -1: //Do squat
	    	//nobody here but us chickens
	    	break;
	    case 0: //Just move to in front of the defense
	    	driveTrain.drive(0.8, 0);
	    	if (driveTrain.getRightDistanceFt() > 1.5 && driveTrain.getRightDistanceFt() < 1.9) {
	    		driveTrain.drive(.2, 0);    		
	    	}
	    	if (driveTrain.getRightDistanceFt() >= 1.9) {
	    		driveTrain.drive(0, 0);
	    	}   	
	    	break;    	
	    case 1: //Case 0 + go over low bar
	    	{		
	        	driveTrain.drive(0.8, 0);
	        	if (driveTrain.getRightDistanceFt() > 14.5 && driveTrain.getRightDistanceFt() < 14.9) {
	        		driveTrain.drive(.2, 0);    		
	        	}
	        	if (driveTrain.getRightDistanceFt() >= 14.9) {
	        		driveTrain.drive(0, 0);
	        	}   	
	    	}    	
	    	break;     	
	    case 2: //Case 0 + go over rugged terrain
	    	if (currentStep == 1)
	    	{
	        	driveTrain.drive(0.8, 0);
	        	if (driveTrain.getRightDistanceFt() > 1.5 && driveTrain.getRightDistanceFt() < 1.9) {
	        		driveTrain.drive(.2, 0);    		
	        	}
	        	if (driveTrain.getRightDistanceFt() >= 1.9) {
	        		driveTrain.drive(0, 0);
	        	} 
	        	currentStep = 2;
	    	}    	
	    	if (currentStep == 2)
	    	{
				autoTimer.reset();
				autoTimer.start();
				currentStep = 3;
	    	}
	    	if (currentStep == 3)
	    	{
	    		driveTrain.drive(0.8, 0);
	    		if (autoTimer.get() >= 10) {
	    			driveTrain.drive(0, 0);
	    		}
	    
	    		
	    		
	    		
	    		//FINISH IMPLEMENTING TIME+GYROSCOPE
	    		
	    	}
	    	break; 
	    }	
    	
    	//Add autonomous code here
    	//Estimate battery Parameters
    	bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
    		
    	
    	//Log data from this timestep
    	log_data();
    	
    	//Execution time metric - this must be last!
    	loop_time_elapsed = Timer.getFPGATimestamp() - prev_loop_start_timestamp;
    }

    
    
    /**
     * This function is called once right before the start of teleop
     */
    public void teleopInit() {
    	if(enable_logging){
    		//Initialize the new log file for Teleop
    		logger.init(logger_fields, units_fields);
    	}

    	//compressor starts automatically, but just in case...
    	Pneumatics.startCompressor();
    	
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
    	
    	//Estimate battery Parameters
    	bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
    	
        //Run Drivetrain with reversing
    	if(joy1.getRawAxis(XBOX_LTRIGGER_AXIS) > 0.5){ //reverse control
    		cmdInvCtrls = true;
        	driveTrain.arcadeDrive(-1 * joy1.getRawAxis(XBOX_LSTICK_YAXIS), -1 * joy1.getRawAxis(XBOX_RSTICK_XAXIS), squaredInputs);
    	}
    	else{ //regular control
    		cmdInvCtrls = false;
    		driveTrain.arcadeDrive(joy1.getRawAxis(XBOX_LSTICK_YAXIS), joy1.getRawAxis(XBOX_RSTICK_XAXIS), squaredInputs);
    	}

    	//Evaluate upshift/downshift need
    	double left_speed = Math.abs(driveTrain.getLeftWheelSpeedRPM());
    	double right_speed = Math.abs(driveTrain.getLeftWheelSpeedRPM());
    	double net_speed = Math.max(left_speed,right_speed);
    	shifter.OttoShifterPeriodic(net_speed, wheel_speed.calcDeriv(net_speed), Math.abs(accel_RIO.getY()), pdp.getTotalCurrent(), joy1.getRawButton(XBOX_LEFT_BUTTON), joy1.getRawButton(XBOX_RIGHT_BUTTON));
    	//Set pneumatics to select gear and activate driver 1 rumble if needed
    	if(shifter.gear){
    		System.out.println("high_gear");
    		Pneumatics.shiftToHighGear();
    		joy1.setRumble(RumbleType.kLeftRumble, 0f);
    	}
    	else{
    		System.out.println("low_gear");
    		Pneumatics.shiftToLowGear();
    		joy1.setRumble(RumbleType.kLeftRumble, 0.25f);
    	}
    	
    	//Update camera position
    	processCameraAngle();
    	
    	//Run climber
    	Climber.periodicClimb(joy2.getRawButton(XBOX_START_BUTTON), joy2.getRawAxis(XBOX_LSTICK_YAXIS), joy2.getRawAxis(XBOX_RTRIGGER_AXIS));
    	
    	//Drawbridge Arm controls algorithm
    	DBAC.periodUptade(joy2.getRawAxis(XBOX_RSTICK_XAXIS), (joy2.getRawAxis(XBOX_LTRIGGER_AXIS)> 0.5));
    	
    	//Intake/shooter controls
    	intakeLauncherSM.processInputs(joy2);
    	
    	//Override stalled shooter motor to zero
    	shooterDiagnostics.eval(launchMotor.getActSpeed(), launchMotor.getCurrent(), launchMotor.getMotorCmd());
    	if(shooterDiagnostics.motorStalled){
    		launchMotor.shooterController.set(0);
    	}
    	
    	
    	//Adjust intake position based on driver commands
    	if(joy2.getRawButton(XBOX_A_BUTTON))
    		Pneumatics.intakeDown();
    	if(joy2.getRawButton(XBOX_Y_BUTTON))
    		Pneumatics.intakeUp();
    	
    	//Enable/Disable compressor based on driver commands
    	if(joy1.getRawButton(XBOX_BACK_BUTTON))
    	{
    		Pneumatics.startCompressor();
    	}
    	if(joy1.getRawButton(XBOX_START_BUTTON))
    	{
    		Pneumatics.stopCompressor();
    	}
    	
    	//Log data from this timestep
    	log_data();
    	updateSmartDashboard();
    	System.out.println(driveTrain.getMemes());
    	
    	//Execution time metric - this must be last! Even after memes!
    	loop_time_elapsed = Timer.getFPGATimestamp() - prev_loop_start_timestamp;
    }
    
    /**
     * This function is called just before the start of test mode
     * This is a random comment
     */
    public void testInit() {
    	if(enable_logging){
	    	//Initialize the new log file for Test
	    	logger.init(logger_fields, units_fields);
    	}
    	
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
    	if(enable_logging){
	    	int ret_val_1 = 0;
	    	int ret_val_2 = 0;
	    	
	    	//Log proper data to file. Order must match that of the variable "logger fields"
	    	ret_val_1 = logger.writeData( Timer.getFPGATimestamp(),
	    								  ds.getMatchTime(), 
				    				     (ds.isBrownedOut()?1.0:0.0),
				    				     (ds.isFMSAttached()?1.0:0.0),
				    				     (ds.isSysActive()?1.0:0.0),
				    				      pdp.getVoltage(),
				    				      ds.getBatteryVoltage(),
				    			          pdp.getTotalCurrent(),
				    			          pdp.getCurrent(DT_LF_PDP_CH),
				    			          pdp.getCurrent(DT_LB_PDP_CH),
				    			          pdp.getCurrent(DT_RF_PDP_CH),
				    			          pdp.getCurrent(DT_RB_PDP_CH),
				    			          pdp.getCurrent(INTAKE_PDP_CH),
				    			          pdp.getCurrent(SHOOTER_PDP_CH),
				    			          pdp.getCurrent(TAPE_PDP_CH),
				    			          pdp.getCurrent(WINCH_1_PDP_CH),
				    			          pdp.getCurrent(WINCH_2_PDP_CH),
				    			          pdp.getCurrent(SP_DB_ARM_PDP_CH),
				    			          pdp.getTemperature(),
				    			          pdp.getCurrent(DT_LF_PDP_CH) + pdp.getCurrent(DT_LB_PDP_CH),
				    			          pdp.getCurrent(DT_RF_PDP_CH) + pdp.getCurrent(DT_RB_PDP_CH),
				    			          driveTrain.getLeftCurrent(),
				    			          driveTrain.getRightCurrent(),
				    			          driveTrain.reductionFactor,
				    			          bpe.getEstESR(),
	    								  bpe.getEstVoc(),
	    								 (bpe.getConfidence()?1.0:0.0),
	    								  bpe.getEstVsys(calcEstTotCurrentDraw()),
				    			          joy1.getRawAxis(XBOX_LSTICK_YAXIS),
				    			          joy1.getRawAxis(XBOX_RSTICK_XAXIS),
				    			          driveTrain.leftMotor_1.get(),
				    			         -driveTrain.rightMotor_1.get(),
				    			          driveTrain.getLeftMotorSpeedRadPerS()*9.5492, //report rate in RPM
				    			          driveTrain.getRightMotorSpeedRadPerS()*9.5492,
				    			          driveTrain.getLeftWheelSpeedRPM(),
				    			          driveTrain.getRightWheelSpeedRPM(),
				    			          driveTrain.getLeftDistanceFt(),
				    			          driveTrain.getRightDistanceFt(),
				    			          accel_RIO.getX(),
				    					  accel_RIO.getY(),
				    					  accel_RIO.getZ(),
				    				      loop_time_elapsed*1000.0,
				    					  csm.curCamPos.ordinal(),
				    					 (joy2.getRawButton(XBOX_START_BUTTON)?1.0:0.0),
				    					  Climber.tapemotor.get(),
				    					  Climber.winchmotor1.get(),
				    					 (Climber.tapetrigger.get()?1.0:0.0),
				    					  gyro.get_gyro_angle()%360,
				    					 (gyro.get_gyro_read_status()?1.0:0.0),
				    					  Pneumatics.getCurrent(),
				    					  launchMotor.getCurrent(),
				    					  launchMotor.getMotorCmd(),
				    					  launchMotor.getActSpeed(),
				    					  launchMotor.getDesSpeed(),
				    					  shifter.VelDebounceState?1.0:0.0,
				    					  shifter.WheelAccelDebounceState?1.0:0.0,
				    					  shifter.VertAccelDebounceState?1.0:0.0,
				    					  shifter.CurrentDebounceState?1.0:0.0,
				    					  Pneumatics.isHighGear()?1.0:0.0,
		    							  cmdInvCtrls?1.0:0.0,
    									  Pneumatics.getPressurePsi(),
    									  launchMotor.getSquishSensorVal(),
    									  currentStep,
    									  shooterDiagnostics.motorStalled?1.0:0.0,
										  intakeLauncherSM.intake.get()
				    					 );
	    	//Check for brownout. If browned out, force write data to log. Just in case we
	    	//lose power and nasty things happen, at least we'll know how we died...
	    	if(ds.isBrownedOut()) {
	    		ret_val_2 = logger.forceSync();
	    		System.out.println("Warning - brownout condition detetcted, flushing log buffers...");
	    	}
	    	
	    	return Math.min(ret_val_1, ret_val_2);
    	}
    	return -1;
    }
    
    /**
     * Sets camera to the right position based on driver inputs
     */  
    private void processCameraAngle(){
    	
    	if(joy1.getPOV(XBOX_DPAD_POV) == 0 || joy2.getPOV(XBOX_DPAD_POV) == 0){
    		csm.setCameraPos(CamPos.DRIVE_FWD);
    		System.out.println("Set Cam Fwd");
    	}
    	else if(joy1.getPOV(XBOX_DPAD_POV) == 180 || joy2.getPOV(XBOX_DPAD_POV) == 180){
    		csm.setCameraPos(CamPos.DRIVE_REV);
    		System.out.println("Set Cam Rev");
    	}
    	else if(joy1.getPOV(XBOX_DPAD_POV) == 90 || joy2.getPOV(XBOX_DPAD_POV) == 90){
    		csm.setCameraPos(CamPos.SHOOT);
    		System.out.println("Set Cam Shoot");
    	}
    	else if(joy1.getPOV(XBOX_DPAD_POV) == 270 || joy2.getPOV(XBOX_DPAD_POV) == 270){
    		csm.setCameraPos(CamPos.CLIMB);
    		System.out.println("Set Cam Climb");
    	}
    	
    }
    
    /**
     * Gets the best-guess at present current draw based on some measured, some estimated values.
     * @return
     */
    private double calcEstTotCurrentDraw(){
    	double totalCurrent = driveTrain.getRightCurrent() +  // Estimated DT current
    			              driveTrain.getLeftCurrent()  + 
    			              Pneumatics.getCurrent() +       // Compressor current draw
    			              launchMotor.getCurrent() +      // Launch motor current (at SRX)
	    			          pdp.getCurrent(TAPE_PDP_CH) +   // Other Peripheral currents (at PDP)
	    			          pdp.getCurrent(WINCH_1_PDP_CH) +
	    			          pdp.getCurrent(WINCH_2_PDP_CH) +
	    			          pdp.getCurrent(SP_DB_ARM_PDP_CH) +
	    			          pdp.getCurrent(INTAKE_PDP_CH) +
    			              4;                              // Fudge-factor 4A draw from un-instrumented devices
    	return totalCurrent;
    }
    
    /**
     * Send things to smart dashboard which ought to be snet
     * 
     */
    private void updateSmartDashboard(){
    	SmartDashboard.putNumber("Pneumatic System Pressure (PSI)", Math.round(Pneumatics.getPressurePsi()));
    	if(shifter.gear)
    		SmartDashboard.putString("Gear", "HIGH GEAR");
    	else
    		SmartDashboard.putString("Gear", "!!!LOW GEAR");
    	SmartDashboard.putNumber("Match Time", ds.getMatchTime());
    	SmartDashboard.putNumber("Total Current Draw", calcEstTotCurrentDraw());
    	SmartDashboard.putNumber("Launch Motor Speed (RPM)", launchMotor.getActSpeed());
    	SmartDashboard.putBoolean("Launch Motor Stalled", shooterDiagnostics.motorStalled);
    	SmartDashboard.putString("IntakeShooter State", intakeLauncherSM.getState());
    	
    }
}
