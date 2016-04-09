
package org.usfirst.frc.team1736.robot;

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
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS AND TUNE
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
	final static int JOY1_INT = 0;
	final static int JOY2_INT = 1;
	//soon(tm)
	final static boolean SINGLE_JOYSTICK_IS_BEST_JOYSTICK = false;
	
	// I'm a silly boy and Idk what I'm doing so I'm just gonna put these variables here, whoop whoop
	int autoMode = 0;
	SendableChooser autoChooser;
	Timer autoTimer = new Timer();
	int currentStep = 0;
	
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
	final static int INTAKE_PDP_CH = 13;
	final static int SHOOTER_PDP_CH = 4;
	final static int TAPE_PDP_CH = 5;
	final static int WINCH_1_PDP_CH = 12;
	final static int WINCH_2_PDP_CH = 3;
	final static int SP_DB_ARM_PDP_CH = 11;
	//unused pdb channels
	final static int UNUSED_2 = 2;
	final static int UNUSED_6 = 6;
	final static int UNUSED_7 = 7;
	final static int UNUSED_8 = 8;
	final static int UNUSED_9 = 9;
	final static int UNUSED_10 =10;
	
	
	//-Square joystick input?
	final static boolean squaredInputs = true;
	
	//Battery Param Est 
	final static int BPE_length = 200; //Window length
	final static double BPE_confidenceThresh_A = 10.0;
	
	//Shooter Watchdog
	final static int WDOG_LIMIT = 20;
	boolean wdog_timeout = false;
	final static int RESET_ATTEMPT_LIMIT = 5;
	int resets_attempted =0;
	
	//Path Planner
	boolean alreadyStarted = false;
	
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
            "GyroMeasRotation",
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
            "IntakeMotorCmd",
            "AudioInVoltage",
            "BallInCarry",
            "IntakeShooterState",
            "PathPlannerLDesSpd",
            "PathPlannerRDesSpd",
            "LeftDTGroundSpeed",
            "RightDTGroundSpeed",
            "PathPlannerDesHeading",
            "HeadingErr",
            "ShooterPIDWatchdogCtr",
            "ShooterWatchdogTimeout",
            "PdbUnused2Current",
            "PdbUnused6Current",
            "PdbUnused7Current",
            "PdbUnused8Current",
            "PdbUnused9Current",
            "PdbUnsued10Current",
            };
			
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
           "deg/s",
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
           "cmd",
           "V",
           "bit",
           "state",
           "ft/s",
           "ft/s",
           "ft/s",
           "ft/s",
           "deg",
           "deg",
           "count",
           "bit",
           "A",
           "A",
           "A",
           "A",
           "A",
           "A"
           };
		
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CLASS OBJECTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
	//Devices on the Robot we will query
	DriverStation ds;
	PowerDistributionPanel pdp;
	BatteryParamEstimator bpe;
	BuiltInAccelerometer accel_RIO;
	ADXRS450_Gyro gyro;
	
	//Data Logger
	CsvLogger logger = new CsvLogger();
	//Variable for metric logging
	private double prev_loop_start_timestamp = 0;
	private double loop_time_elapsed = 0;
	
	//Joysticks
	Xbox360Controller joy1;
	Xbox360Controller joy2;
	//Slow Turn Button
	boolean lastJoy1RightStickPressed = false;
	double driverTurnMultiplier = 1;
	//Drive Train
	DriveTrain driveTrain;
	boolean cmdInvCtrls = false;
	//climber mechanism
	Climb Climber;
	//Launch Motor
	Shooter launchMotor;
	//Intake/Launch state machine
	IntakeLauncherStateMachine intakeLauncherSM;
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
	//LED's
	LEDSequencer leds;
	SendableChooser colorChooser;
	//Auto PathPlanner
	casserolePathAuto autopp;
	
	//SDB Read counter in Disabled
	int disabled_sbd_counter;
	
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
    	autoChooser.addObject("Cross Defense", 1);
    	autoChooser.addObject("PathPlanner LowGoal",2);
    	autoChooser.addObject("PathPlanner HighGoal - TURN ROBOT BACKWARD",3);
    	autoChooser.addObject("PathPlanner GyroHighGoal",4);
    	autoChooser.addDefault("Do Nothing",-1);
    	SmartDashboard.putData("Auto Mode Chooser", autoChooser);
    	
    	/* This will be disabled for competition to prevent anything freaky from happening, 
    	 * but may be rolled back in after competition so the kids can demo awesome colors
    	//LED Color Choose
    	colorChooser = new SendableChooser();
    	colorChooser.addObject("Solid Red", LEDPatterns.SOLID_RED);
    	colorChooser.addObject("Stripes", LEDPatterns.STRIPES_FWD);
    	colorChooser.addObject("Stripes (Inv)", LEDPatterns.STRIPES_REV);
    	colorChooser.addObject("Twinkle", LEDPatterns.TWINKLE);
    	colorChooser.addObject("Pulse Red", LEDPatterns.PULSE_RED);
    	colorChooser.addObject("Volume Controlled", LEDPatterns.VOLUME_CTRL1);
    	colorChooser.addObject("Gradiant", LEDPatterns.GRADIENT);
    	colorChooser.addObject("Rainbow", LEDPatterns.RAINBOW);
    			//Tom's code (awesome stuff)
    	colorChooser.addObject("Tom's Rainbow", LEDPatterns.ROYGBIV);
    	colorChooser.addObject("Tom's Twinkle", LEDPatterns.TWINKLE_TOMS);
    	colorChooser.addObject("March 17", LEDPatterns.GREEN);
    	colorChooser.addObject("Red Alliance", LEDPatterns.RED);
    	colorChooser.addObject("Blue Alliance", LEDPatterns.BLUE);
    	colorChooser.addObject("Seven", LEDPatterns.SEVEN);
    	colorChooser.addObject("Bright", LEDPatterns.BRIGHT);
    	colorChooser.addObject("Swag", LEDPatterns.SWAG);
    	colorChooser.addObject("RWB", LEDPatterns.MURICA);
    	colorChooser.addObject("Robot Casserole", LEDPatterns.CASS);
    	colorChooser.addObject("Randy", LEDPatterns.RANDY);
    			//default
    	colorChooser.addDefault("Off", LEDPatterns.OFF);
    	SmartDashboard.putData("LED Pattern Chooser", colorChooser);
    	*/
    	
    	//Other Peripherals
    	ds = DriverStation.getInstance();
    	pdp = new PowerDistributionPanel();
    	bpe = new BatteryParamEstimator(BPE_length);
    	bpe.setConfidenceThresh(BPE_confidenceThresh_A);
    	accel_RIO = new BuiltInAccelerometer();    	
    	csm = new CameraServoMount();
    	
    	//Motors - Drivetrain
    	L_Motor_1 = new VictorSP(DT_LF_MOTOR_PWM_CH);
    	L_Motor_2 = new VictorSP(DT_LB_MOTOR_PWM_CH);
    	R_Motor_1 = new VictorSP(DT_RF_MOTOR_PWM_CH);
    	R_Motor_2 = new VictorSP(DT_RB_MOTOR_PWM_CH);
    	
    	//Drivetrain
    	driveTrain = new DriveTrain(L_Motor_1, L_Motor_2, R_Motor_1, R_Motor_2, pdp, bpe);
    	System.out.println(driveTrain.getMemes());
    	shifter = new OttoShifter();
    	wheel_speed = new DerivativeCalculator();
    	
    	//Peripherals
    	Climber=new Climb();
    	launchMotor = new Shooter();
    	intakeLauncherSM = new IntakeLauncherStateMachine(launchMotor);
    	DBAC = new DrawbridgeArmControls();
    	leds = new LEDSequencer();
    	gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
    	
    	//Cal Gyro at startup
    	calGyro();

    	//Joysticks
    	joy1 = new Xbox360Controller(JOY1_INT);
    	joy2 = new Xbox360Controller(JOY2_INT);
    	
    	//Ensure intake starts in proper position
    	Pneumatics.intakeUp();
    	
    	
    	//init pathPlanner
    	autopp = new casserolePathAuto(driveTrain, intakeLauncherSM, gyro);
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
 
    	//Set intake state to up - intake automatically goes up, this is just a safety so it stays up when re-enabled
    	Pneumatics.intakeUp();
    	
    	//Turn rumble off for both controllers
    	joy1.rumbleOff();
    	joy2.rumbleOff();
    	
    	//Kill off any autonomous that may have been running
    	autopp.stopPlayback();
    	
    	disabled_sbd_counter = 0;
    	driverTurnMultiplier = 1.0;

    }
    
    public void disabledPeriodic() {

    	//set LED's to proper color
    	//leds.sequencerPeriodic(LEDPatterns.VOLUME_CTRL1); //makes dustin happy
    	leds.sequencerPeriodic(LEDPatterns.RED); 
    	
    	
    	//Keep SDB up to date even in disabled
    	updateSmartDashboard();
    	
    	//keep polling auto mode from the driver station
    	if(disabled_sbd_counter == 25){
    		autoMode = (int) autoChooser.getSelected();
    		disabled_sbd_counter = 0;
    	}
    	else{
    		disabled_sbd_counter = disabled_sbd_counter + 1;
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

    	autoMode = (int) autoChooser.getSelected();
    	
    	//reset encoders to 0
    	driveTrain.leftEncoder.reset();
		driveTrain.rightEncoder.reset();
		
		//Reset gyro to forward
		gyro.reset();

    	//init the task timing things
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	loop_time_elapsed = 0;
    	
    	//Display rear camera if doing high goal
    	if(autoMode == 3)
    		SmartDashboard.putBoolean("useCamera1", true);
    	
    	//Raise intake to prevent damage in auto.
    	Pneumatics.intakeUp();
    	
    	//Shift to low gear
    	Pneumatics.shiftToLowGear();
    	
    	//Kill off any autonomous that may have been running
    	autopp.stopPlayback();
    	//Calc a path
    	if(autoMode != -1){
    		autopp.calcPath(autoMode);
    	}
    	
    	//path planner one-time call guard boolean
    	alreadyStarted = false;
    	
    }

    
    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	
    	//Execution time metric - this must be first!
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	
    	//Keep SDB up to date
    	updateSmartDashboard();
    	
    	//Indicate auto w/ led's
    	leds.sequencerPeriodic(LEDPatterns.CASS);
    	
    	if(autoMode == -1){
	    	//Ensure safe state while not running
	    	if(!autopp.isPlaybackActive()){
	    		intakeLauncherSM.periodicStateMach(false, false, false, false, false);
	    	}
    	}
    	else{
	    	//call the start function once
	    	if(!alreadyStarted){
	    		autopp.startPlayback();
	    		alreadyStarted = true;
	    	}
	    	//Ensure safe state while not running
	    	if(!autopp.isPlaybackActive()){
	    		intakeLauncherSM.periodicStateMach(false, false, false, false, false);
	    	}
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

    	//Kill off any autonomous that may have been running
    	autopp.stopPlayback();
    	
    	//Turn the watchdog back on
		driveTrain.setSafetyEnabled(true);
    	
    	//compressor starts automatically, but just in case...
    	Pneumatics.startCompressor();
    	
    	//Default to low gear
    	Pneumatics.shiftToLowGear();
    	
    	//init the task timing things
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	loop_time_elapsed = 0;
    	
    	//Kill off the motor PID's so they don't fight with the regular driving
    	autopp.motors.lmpid.disable(); 
    	autopp.motors.rmpid.disable();
    	
    	//get all the memes
    	System.out.println(driveTrain.getMemes());
    }

    /** 
     * This function is called periodically during operator control (teleop)
     */
    public void teleopPeriodic() {
    	//Execution time metric - this must be first!
    	prev_loop_start_timestamp = Timer.getFPGATimestamp();
    	
    	//increment the shooter watchdog in an attempt to catch if its PID thread dies.
    	launchMotor.wdog_ctr = launchMotor.wdog_ctr + 1;
    	if(launchMotor.wdog_ctr > WDOG_LIMIT && resets_attempted < RESET_ATTEMPT_LIMIT){ //don't reset too many times, just in case so we don't lock out processor on this
    		launchMotor.disable();
    		launchMotor.enable(); //If the watchdog gets too loud, reset the launch motor in a last-ditch attempt...
    		wdog_timeout = true;
    		resets_attempted = resets_attempted + 1;
    	}
    	else{
    		wdog_timeout = false;
    	}
    	
    	//Estimate battery Parameters
    	bpe.updateEstimate(pdp.getVoltage(), pdp.getTotalCurrent());
    	
        //Run Drivetrain with reversing
    	if(joy1.LTrigger() > 0.5){ //reverse control
    		cmdInvCtrls = true;
        	driveTrain.arcadeDrive(-1 * joy1.LStick_Y(), joy1.RStick_X() * driverTurnMultiplier, squaredInputs);
    	}
    	else{ //regular control
    		cmdInvCtrls = false;
    		driveTrain.arcadeDrive(joy1.LStick_Y(), joy1.RStick_X() * driverTurnMultiplier, squaredInputs);
    	}
    	if (joy1.RStickButton() && !lastJoy1RightStickPressed)
    	{
    		if (driverTurnMultiplier < 1)
    		{
    			driverTurnMultiplier = 1;
    			joy1.setRightRumble(0f);
    		}
    		else 
    		{
    			driverTurnMultiplier = 0.5;
    			joy1.setRightRumble(0.5f);
    			
    		}    		
    	}
    	lastJoy1RightStickPressed = joy1.RStickButton();
    	//Evaluate upshift/downshift need
    	double left_speed = Math.abs(driveTrain.getLeftWheelSpeedRPM());
    	double right_speed = Math.abs(driveTrain.getRightWheelSpeedRPM());
    	double net_speed = Math.min(left_speed,right_speed);
    	shifter.OttoShifterPeriodic(net_speed, wheel_speed.calcDeriv(net_speed), Math.abs(accel_RIO.getX()), pdp.getTotalCurrent(), 
    			joy1.LB(), joy1.RB());
    	//Set pneumatics to select gear and activate driver 1 rumble if needed
    	if(shifter.gear){
    		Pneumatics.shiftToHighGear();
    		joy1.setLeftRumble(0f);
    	}
    	else{
    		Pneumatics.shiftToLowGear();
    		joy1.setLeftRumble(0.25f);
    	}
    	
    	//Set joy2 rumble and LEDPattern based on ball carry sensor
    	if(intakeLauncherSM.ballSensorState)
    	{
    		joy2.setLeftRumble(0.25f);
    		//Set LED's to indicate current driver fwd direction while carrying a ball
    		if(cmdInvCtrls)
    		{
    			leds.sequencerPeriodic(LEDPatterns.BALLCARRY_BACK);
    		}
    		else
    		{
    			leds.sequencerPeriodic(LEDPatterns.BALLCARRY_FWD);
    		}
    	}
    	else
    	{
    		joy2.setLeftRumble(0f);
    		//Set LED's to indicate current driver fwd direction
        	if(cmdInvCtrls){
        		leds.sequencerPeriodic(LEDPatterns.STRIPES_REV);
        	}
        	else{
        		leds.sequencerPeriodic(LEDPatterns.STRIPES_FWD);
        	}
    	}
    	
    	//Update camera position
    	processCameraAngle();
    	
    	//Run climber
    	Climber.periodicClimb(joy2.StartButton(), joy2.LStick_Y(), joy2.RTrigger());
    	
    	//Drawbridge Arm controls algorithm
    	DBAC.periodUptade(joy2.RStick_X(), (joy2.LTrigger()> 0.5));
    	
    	//Intake/shooter controls
    	intakeLauncherSM.periodicStateMach(joy2.LB(), 
    									   joy2.RB(), 
    									   joy2.X(), 
    									   joy2.B(), 
    									   joy2.BackButton());
    	
    	
    	
    	//Adjust intake position based on driver commands
    	//Default to up, unless the driver commands down
    	if(joy2.A())
    		Pneumatics.intakeDown();
    	else
    		Pneumatics.intakeUp();
    	
    	//Enable/Disable compressor based on driver commands
    	if(joy1.BackButton())
    	{
    		Pneumatics.startCompressor();
    	}
    	if(joy1.StartButton())
    	{
    		Pneumatics.stopCompressor();
    	}
    	
    	//Update SmDB cam pos.
		SmartDashboard.putBoolean("useCamera1", cmdInvCtrls);
    	
    	//Log data from this timestep
    	log_data();
    	updateSmartDashboard();
    	
    	//Turn rumble off
    	joy1.setRightRumble((float) Math.min(1, Math.abs(accel_RIO.getZ() - 1)));
    	
    	//Execution time metric - this must be last! Even after memes!
    	loop_time_elapsed = Timer.getFPGATimestamp() - prev_loop_start_timestamp;
    }
    
    /**
     * This function is called just before the start of test mode
     * This is a random comment
     */
    public void testInit() {
    
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {

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
				    			          joy1.LStick_Y(),
				    			          joy1.RStick_X(),
				    			         -driveTrain.leftMotor_1.get(),
				    			          driveTrain.rightMotor_1.get(),
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
				    					 (joy2.StartButton()?1.0:0.0),
				    					  Climber.tapemotor.get(),
				    					  Climber.winchmotor1.get(),
				    					 (Climber.tapeTriggerState?1.0:0.0),
				    					  gyro.getAngle(),
				    					  gyro.getRate(),
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
    									  intakeLauncherSM.shooterDiagnostics.motorStalled?1.0:0.0,
										  intakeLauncherSM.intake.get(),
										  leds.ledStrips.audioIn.getVoltage(),
										  intakeLauncherSM.ballSensorState?1.0:0.0,
										  intakeLauncherSM.curState.ordinal(),
										  autopp.motors.lmpid.getSetpoint(),
										  autopp.motors.rmpid.getSetpoint(),
										  driveTrain.getLeftSpdFtPerSec(),
										  driveTrain.getRightSpdFtPerSec(),
										  autopp.pp_des_heading,
										  autopp.angle_err_deg,
										  launchMotor.wdog_ctr,
										  wdog_timeout?1.0:0.0,
										  pdp.getCurrent(UNUSED_2),
										  pdp.getCurrent(UNUSED_6),
										  pdp.getCurrent(UNUSED_7),
										  pdp.getCurrent(UNUSED_8),
										  pdp.getCurrent(UNUSED_9),
										  pdp.getCurrent(UNUSED_10)
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
    	
    	if(joy1.DPad() == 0 || joy2.DPad() == 0){
    		csm.setCameraPos(CamPos.DRIVE_FWD);
    		System.out.println("Set Cam Fwd");
    	}
    	else if(joy1.DPad() == 180 || joy2.DPad() == 180){
    		csm.setCameraPos(CamPos.DRIVE_REV);
    		System.out.println("Set Cam Rev");
    	}
    	else if(joy1.DPad() == 90 || joy2.DPad() == 90){
    		csm.setCameraPos(CamPos.SHOOT);
    		System.out.println("Set Cam Shoot");
    	}
    	else if(joy1.DPad() == 270 || joy2.DPad() == 270){
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
     * Send things to smart dashboard which ought to be sent
     * 
     */
    private void updateSmartDashboard(){
    	SmartDashboard.putNumber("Pneumatic System Pressure (PSI)", Math.round(Pneumatics.getPressurePsi()));
    	if(shifter.gear)
    		SmartDashboard.putString("Gear", "HIGH GEAR");
    	else
    		SmartDashboard.putString("Gear", "!!!LOW GEAR");
    	SmartDashboard.putNumber("Match Time", ds.getMatchTime());
    	SmartDashboard.putNumber("Launch Motor Speed (RPM)", launchMotor.getActSpeed());
    	SmartDashboard.putBoolean("Launch Motor Stalled",  intakeLauncherSM.shooterDiagnostics.motorStalled);
    	SmartDashboard.putString("IntakeShooter State", intakeLauncherSM.curState.toString());
    	SmartDashboard.putNumber("DT Limiting Factor", driveTrain.reductionFactor);
    	SmartDashboard.putNumber("Current Draw", pdp.getTotalCurrent());
    	SmartDashboard.putNumber("Avg Speed FTpS", Math.abs((driveTrain.getRightSpdFtPerSec() + driveTrain.getLeftSpdFtPerSec())/2.0));
    	SmartDashboard.putBoolean("Ball In CarryPos", intakeLauncherSM.ballSensorState);
    	SmartDashboard.putNumber("Selected Auto Mode", autoMode);
    	SmartDashboard.putNumber("Measured Robot Pose Angle", Math.round(gyro.getAngle()) % 360);
    	
    }
    
    /*Call the gyro calibration routine, and change LED colors to ensure people know not to touch the robot.
     * 
     */
    private void calGyro(){
    	leds.sequencerPeriodic(LEDPatterns.RAINBOW);
    	System.out.println("Calibrating gyro, DO NOT TOUCH ROBOT!!!");
    	gyro.calibrate();
    	System.out.println("Done calibrating. woot!");
    	leds.sequencerPeriodic(LEDPatterns.OFF);
    	
    }
}
