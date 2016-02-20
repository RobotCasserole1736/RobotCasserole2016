/**
 * 
 */
package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;

/**
 * @author Chris Gerth
 *
 */
public class IntakeLauncherStateMachine {
	
	//State variables
	public IntLncState curState;
	private IntLncState nextState;
	private int shooterEncFailedDbncCntr;
	private int retractCounter; 
	private int minLaunchTimeCounter;
	
	//Public Outputs - read these to set motor values
	public double shooterCmd_RPM;
	public double intakeCmd;
	public boolean launchEncoderFailed;
	
	//Tune Params
	private static final IntLncState initState = IntLncState.STOPPED_NO_BALL;
	public static final double INTAKE_IN_SPEED = 1.0;
	public static final double INTAKE_EJECT_SPEED = -1.0;
	public static final double INTAKE_RETRACT_SPEED = -0.35;
	public static final double INTAKE_RETRACT_TIME_LOOPS = 8;
	public static final double LAUNCH_SPEED_RPM = 5100; 
	public static final double INTAKE_LAUNCH_FEED_SPEED = 0.8;
	public static final double LAUNCH_SPEED_ERR_LMT_RPM = 300;
	public static final double MIN_LAUNCH_TIME_THRESH_LOOPS = 75;
	public static final double SPOOLDOWN_THRESH_RPM = 100;
	
	public static final double LAUNCH_MOTOR_I_MIN_THRESH_A = 5;
	public static final double LAUNCH_MOTOR_I_MAX_THRESH_A = 20;
	public static final double LAUNCH_MOTOR_ERR_DBNC_TIME_LOOPS = 200;
	
	//Sensor for ball detection
	protected DigitalInput ballSensor;
	DaBouncer sensorOnDebounce;
	DaBouncer sensorOffDebounce;
	public boolean ballSensorState;
	public final int BALL_SENSOR_RISING_DBNC_LOOPS = 25;
	public final int BALL_SENSOR_FALLING_DBNC_LOOPS = 60;
	
	//intake motor
	Talon intake;
	
	//launch motor object & diagnostics - from outside
	Shooter shooter;
    MotorDiagnostic shooterDiagnostics;
	
	//IDs
	final static int intake_ID = 5;
	final static int ballSensor_ID = 4;
	
	
	
	
	IntakeLauncherStateMachine(Shooter shooter_in){
		curState = initState;
		shooterEncFailedDbncCntr = 0;
		retractCounter = 0;
		launchEncoderFailed = false;
		
		intake = new Talon(intake_ID);
		ballSensor = new DigitalInput(ballSensor_ID);
		
		sensorOnDebounce = new DaBouncer();
		sensorOffDebounce = new DaBouncer();
		sensorOnDebounce.threshold = 0.5; //boolean input
		sensorOffDebounce.threshold = 0.5;
		ballSensorState = false;
		
		shooter = shooter_in;
		shooterDiagnostics = new MotorDiagnostic();
	}
	
	void periodicStateMach(boolean intakeCmded, 
			              boolean ejectCmded, 
			              boolean prepToLaunchCmded, 
			              boolean launchCmded, 
			              boolean intakeOvdCmded){
		
		//Step 0 - process inputs. Mostly these are arguments
		dbncBallSensor();
		
		//STEP 1 - Calculate Next State based on current state and Inputs
		
		//if no state transition is specified, 
		//	we should stay in the same state.
		//this assignment will be overwritten if any
		// 	of the transition conditions are true.
		nextState = curState;
		
		//from-all-state block overrides
		if(intakeOvdCmded){
			nextState = IntLncState.INTAKE_OVD;
		} 
		else if(ejectCmded) {
			nextState = IntLncState.EJECT;
		}
		else{
			//execute the rest of the state-machine
			switch(curState){
			case STOPPED_NO_BALL:
				if(ballSensorState)
					nextState = IntLncState.CARRY_BALL;
				else if(intakeCmded)
					nextState = IntLncState.INTAKE;
				
				break;
			case INTAKE:
				if(ballSensorState)
					nextState = IntLncState.CARRY_BALL;
				else if(!intakeCmded)
					nextState = IntLncState.STOPPED_NO_BALL;
				
				break;
			case EJECT:
				if(!ejectCmded)
					nextState = IntLncState.STOPPED_NO_BALL;
				
				break;
			case INTAKE_OVD:
				if(!intakeOvdCmded)
					nextState = IntLncState.CARRY_BALL_OVD;
				
				break;
			case CARRY_BALL:
				if(!ballSensorState)
					nextState = IntLncState.STOPPED_NO_BALL;
				else if(prepToLaunchCmded | launchCmded)
					nextState = IntLncState.RETRACT;
				
				break;
			case CARRY_BALL_OVD:
				if(prepToLaunchCmded | launchCmded)
					nextState = IntLncState.RETRACT;
				
				break;
			case RETRACT:
				if(retractCounter >= INTAKE_RETRACT_TIME_LOOPS)
					nextState = IntLncState.WAIT_FOR_SPOOLUP;
				break;
			case WAIT_FOR_SPOOLUP:
				if(intakeCmded)
					nextState = IntLncState.WAIT_FOR_SPOOLDOWN;
				else if((shooter.getAbsError() < LAUNCH_SPEED_ERR_LMT_RPM) |
					     calcEncoderFailed(shooter.getCurrent()))
					nextState = IntLncState.WAIT_FOR_LAUNCH;
				
				break;
			case WAIT_FOR_LAUNCH:
				if(intakeCmded)
					nextState = IntLncState.WAIT_FOR_SPOOLDOWN;
				else if((shooter.getAbsError() >= LAUNCH_SPEED_ERR_LMT_RPM) &
					     !calcEncoderFailed(shooter.getCurrent()))
					nextState = IntLncState.WAIT_FOR_SPOOLUP;
				else if(launchCmded)
					nextState = IntLncState.LAUNCH;
				
				break;
			case LAUNCH:
				if(!launchCmded & minLaunchTimeCounter > MIN_LAUNCH_TIME_THRESH_LOOPS)
					nextState = IntLncState.WAIT_FOR_SPOOLDOWN;
				
				break;
			case WAIT_FOR_SPOOLDOWN:
				if(shooter.getActSpeed() < SPOOLDOWN_THRESH_RPM)
					nextState = IntLncState.STOPPED_NO_BALL;
				
				break;
			default:
				System.out.println("Warning: IntakeLauncher state machine got unexpected state " + curState.toString());
				break;
			}
		}
		
		
		//STEP 2 - Calculate internal state updates (timers and such) and outputs
		//Note that the same variables are always set. This admittedly is a bit redundant,
		//but makes the debugging much easier. I don't recommend changing it.
		switch(curState){
		case STOPPED_NO_BALL:
			shooterEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = 0;
			
			break;
		case INTAKE:
			shooterEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = INTAKE_IN_SPEED;
			
			break;
		case EJECT:
			shooterEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = INTAKE_EJECT_SPEED;
			
			break;
		case INTAKE_OVD:
			shooterEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = INTAKE_IN_SPEED;
			
			break;
		case CARRY_BALL:
			shooterEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = 0;
			
			break;
		case CARRY_BALL_OVD:
			shooterEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = 0;
			
			break;
		case RETRACT:
			shooterEncFailedDbncCntr = 0;
			retractCounter++; //notice the increment!
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = INTAKE_RETRACT_SPEED;
			
			break;
		case WAIT_FOR_SPOOLUP:
			shooterEncFailedDbncCntr++; //notice the increment!
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = LAUNCH_SPEED_RPM;
			intakeCmd = 0;
			
			break;
		case WAIT_FOR_LAUNCH:
			//shooterEncFailedDbncCntr; //notice the maintain-value!
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = LAUNCH_SPEED_RPM;
			intakeCmd = 0;
			
			break;
		case LAUNCH:
			shooterEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter++;  //notice the increment!
			shooterCmd_RPM = LAUNCH_SPEED_RPM;
			intakeCmd = INTAKE_LAUNCH_FEED_SPEED;
			
			break;
		case WAIT_FOR_SPOOLDOWN:
			shooterEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = 0;
			
			break;
		default:
			System.out.println("Warning: IntakeLauncher state machine got unexpected state " + curState.toString());
			break;
		}
		
		
		//STEP 3 - Prep for Next Loop
		//make the next-state the current state
		curState = nextState;
		
		//Set 4 - Set Outputs
		// intake motor
		intake.set(intakeCmd);
		
    	// launch motor,but override to zero if stall detected.
    	shooterDiagnostics.eval(shooter.getActSpeed(), shooter.getCurrent(), shooter.getMotorCmd());
    	if(shooterDiagnostics.motorStalled){
    		shooter.shooterController.set(0);
    		shooter.setSpeed(0);
    	}
    	else { 
    		shooter.setSpeed(shooterCmd_RPM);
    	}
    	
		return;
	}
	
	/**
	 * Run both debouncers on the ball sensor's present value
	 * and set the ballSensorState based on that.
	 */
	private void dbncBallSensor(){
		if(sensorOnDebounce.AboveDebounce(ballSensor.get()?0.0:1.0))
			ballSensorState = true;
		else if(sensorOffDebounce.BelowDebounce(ballSensor.get()?0.0:1.0))
			ballSensorState = false;
		//Else, don't change the ballSensorState variable
	}
	
	/**
	 * Given the present debounce counter and motor current, determine if the encoder is faulted.
	 * Note the debounce counter must be incremented elsewhere
	 * @param shooterCurrent_A - current draw of the launch motor
	 * @return True if encoder failed, false otherwise
	 */
	private boolean calcEncoderFailed(double shooterCurrent_A){
	     if((shooterEncFailedDbncCntr > LAUNCH_MOTOR_ERR_DBNC_TIME_LOOPS) & 
	    		   shooterCurrent_A < LAUNCH_MOTOR_I_MAX_THRESH_A &
	    		   shooterCurrent_A > LAUNCH_MOTOR_I_MIN_THRESH_A)
	    	 launchEncoderFailed = true;
	     else
	    	 launchEncoderFailed = false;
	     
	     return launchEncoderFailed;
	    	 
		
	}
	
	
	
}
