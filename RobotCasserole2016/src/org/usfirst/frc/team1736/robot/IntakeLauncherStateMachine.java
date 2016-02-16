/**
 * 
 */
package org.usfirst.frc.team1736.robot;

/**
 * @author Chris Gerth
 *
 */
public class IntakeLauncherStateMachine {
	
	//State variables
	public IntLncState curState;
	private IntLncState nextState;
	private int launchMotorEncFailedDbncCntr;
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
	public static final double INTAKE_RETRACT_SPEED = -0.1;
	public static final double INTAKE_RETRACT_TIME_LOOPS = 10;
	public static final double LAUNCH_SPEED_RPM = 3000;
	public static final double INTAKE_LAUNCH_FEED_SPEED = 0.8;
	public static final double LAUNCH_SPEED_ERR_LMT_RPM = 300;
	public static final double MIN_LAUNCH_TIME_THRESH_LOOPS = 75;
	public static final double SPOOLDOWN_THRESH_RPM = 100;
	
	public static final double LAUNCH_MOTOR_I_MIN_THRESH_A = 5;
	public static final double LAUNCH_MOTOR_I_MAX_THRESH_A = 20;
	public static final double LAUNCH_MOTOR_ERR_DBNC_TIME_LOOPS = 200;
	
	
	
	IntakeLauncherStateMachine(){
		curState = initState;
		launchMotorEncFailedDbncCntr = 0;
		retractCounter = 0;
		launchEncoderFailed = false;
	}
	
	void iterateStateMach(boolean intakeCmded, 
			              boolean ejectCmded, 
			              boolean prepToLaunchCmded, 
			              boolean launchCmded, 
			              boolean intakeOvdCmded, 
			              boolean ballInCarryPos, 
			              double shooterError_RPM,
			              double shooterSpeed_RPM,
			              double launchMotorCurrent_A){
		
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
				if(ballInCarryPos)
					nextState = IntLncState.CARRY_BALL;
				else if(intakeCmded)
					nextState = IntLncState.INTAKE;
				
				break;
			case INTAKE:
				if(ballInCarryPos)
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
				if(!ballInCarryPos)
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
				if((shooterError_RPM < LAUNCH_SPEED_ERR_LMT_RPM) |
					calcEncoderFailed(launchMotorCurrent_A))
					nextState = IntLncState.WAIT_FOR_LAUNCH;
				
				break;
			case WAIT_FOR_LAUNCH:
				if((shooterError_RPM >= LAUNCH_SPEED_ERR_LMT_RPM) &
					!calcEncoderFailed(launchMotorCurrent_A))
						nextState = IntLncState.WAIT_FOR_SPOOLUP;
				else if(launchCmded)
						nextState = IntLncState.LAUNCH;
				
				break;
			case LAUNCH:
				if(!launchCmded & minLaunchTimeCounter > MIN_LAUNCH_TIME_THRESH_LOOPS)
					nextState = IntLncState.WAIT_FOR_SPOOLDOWN;
				
				break;
			case WAIT_FOR_SPOOLDOWN:
				if(shooterSpeed_RPM < SPOOLDOWN_THRESH_RPM)
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
			launchMotorEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = 0;
			
			break;
		case INTAKE:
			launchMotorEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = INTAKE_IN_SPEED;
			
			break;
		case EJECT:
			launchMotorEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = INTAKE_EJECT_SPEED;
			
			break;
		case INTAKE_OVD:
			launchMotorEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = INTAKE_IN_SPEED;
			
			break;
		case CARRY_BALL:
			launchMotorEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = 0;
			
			break;
		case CARRY_BALL_OVD:
			launchMotorEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = 0;
			
			break;
		case RETRACT:
			launchMotorEncFailedDbncCntr = 0;
			retractCounter++; //notice the increment!
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = 0;
			intakeCmd = INTAKE_RETRACT_SPEED;
			
			break;
		case WAIT_FOR_SPOOLUP:
			launchMotorEncFailedDbncCntr++; //notice the increment!
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = LAUNCH_SPEED_RPM;
			intakeCmd = 0;
			
			break;
		case WAIT_FOR_LAUNCH:
			//launchMotorEncFailedDbncCntr; //notice the maintain-value!
			retractCounter = 0; 
			minLaunchTimeCounter = 0;
			shooterCmd_RPM = LAUNCH_SPEED_RPM;
			intakeCmd = 0;
			
			break;
		case LAUNCH:
			launchMotorEncFailedDbncCntr = 0;
			retractCounter = 0; 
			minLaunchTimeCounter++;  //notice the increment!
			shooterCmd_RPM = LAUNCH_SPEED_RPM;
			intakeCmd = INTAKE_LAUNCH_FEED_SPEED;
			
			break;
		case WAIT_FOR_SPOOLDOWN:
			launchMotorEncFailedDbncCntr = 0;
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
		return;
	}
	
	/**
	 * Given the present debounce counter and motor current, determine if the encoder is faulted.
	 * Note the debounce counter must be incremented elsewhere
	 * @param launchMotorCurrent_A - current draw of the launch motor
	 * @return True if encoder failed, false otherwise
	 */
	private boolean calcEncoderFailed(double launchMotorCurrent_A){
	     if((launchMotorEncFailedDbncCntr > LAUNCH_MOTOR_ERR_DBNC_TIME_LOOPS) & 
	    		   launchMotorCurrent_A < LAUNCH_MOTOR_I_MAX_THRESH_A &
	    		   launchMotorCurrent_A > LAUNCH_MOTOR_I_MIN_THRESH_A)
	    	 launchEncoderFailed = true;
	     else
	    	 launchEncoderFailed = false;
	     
	     return launchEncoderFailed;
	    	 
		
	}
	
	
	
}
