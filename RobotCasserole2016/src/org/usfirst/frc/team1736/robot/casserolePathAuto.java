package org.usfirst.frc.team1736.robot;

import java.util.Timer;
import java.util.TimerTask;

public class casserolePathAuto {

	//Path Planner Constants
	final double[][] waypoints_apchDfns = new double[][]{ // go up to defenses
		{0,0},
		{-4,0}
	};
	
	final double[][] waypoints_crsLwBr = new double[][]{ //cross low-bar defense
		{0,0},
		{-15,0}		
	};
	
	final double[][] waypoints_crossShootLow = new double[][]{ //cross and shoot
		{0,0},
		{-17.33,0},
		{-19.5666,11.20555} //Jeremey's temp numbers for lining up the robot for a low-goal shot
	};
	final double[][] waypoints_crossShootHigh = new double[][]{ //cross and shoot
		{0,0},
		{18.13,0},
		{18.9666,9.00555} //Jeremey's temp numbers for lining up the robot for a low-goal shot
	};
	
	final double totalPathPlannerTime_apchDfns = 5;
	final double totalPathPlannerTime_crsLwBr = 5;
	final double totalPathPlannerTime_crossShootLow = 10;
	final double totalPathPlannerTime_crossShootHigh = 10;
	
	final double PLANNER_SAMPLE_RATE_S = 0.02; //200ms update rate 
	final double ROBOT_TRACK_WIDTH_FT = 1.9; //1.9ft wide tracks
	
	//THESE MUST CHANGE IF THE SAMPLE TIME OF THE PLAYBACk THREAD CHANGES
	final int intakeLowerTimeStep_crossShootLow = 0;
	final int intakeRaiseTimeStep_crossShootLow = 250;
	//THESE MUST CHANGE IF THE SAMPLE TIME OF THE PLAYBACk THREAD CHANGES
	final int intakeLowerTimeStep_crossShootHigh = 15;
	final int intakeRaiseTimeStep_crossShootHigh = 250;
	
	int timestep = 0;
	
	FalconPathPlanner path;
	
	//Output Device - the drivetrain (and sometimes the shooter)
	DriveTrain dt;
	DriveMotorsPIDVelocity motors;
	IntakeLauncherStateMachine ilsm;
	
	//Constraints
	boolean invertSetpoints = false;
	boolean cycleIntakeArm = false;
	
	//Playback thread
	Timer timerThread;
	boolean playbackActive = false;
	
	//End-of-path event variables
	boolean shootHighGoal = false;
	boolean shootLowGoal = false;
	edu.wpi.first.wpilibj.Timer shotTimer;
	final double HIGH_GOAL_SHOT_TIME_S = 3.0;
	final double LOW_GOAL_SHOT_TIME_S = 4.0;
	
	
	/**
	 * Constructor
	 * 
	 */
	casserolePathAuto(DriveTrain dt_in, IntakeLauncherStateMachine ilsm_in){
		dt = dt_in;
		ilsm = ilsm_in;
		shotTimer = new edu.wpi.first.wpilibj.Timer();
		shotTimer.reset();
		motors = new DriveMotorsPIDVelocity(dt);
		
	}
	
	
	/**
	 * Calc Path - must be run in auto_init to calculate a path based on 
	 * the selected auto mode.
	 * @param auto_mode
	 */
	public void calcPath(int auto_mode){
		if(auto_mode == 0){
			System.out.println("Calculating path Approach Defense");
			path = new FalconPathPlanner(waypoints_apchDfns);
			path.calculate(totalPathPlannerTime_apchDfns, PLANNER_SAMPLE_RATE_S, ROBOT_TRACK_WIDTH_FT);
			invertSetpoints = false;
			shootHighGoal = false;
			shootLowGoal = false;
			cycleIntakeArm = false;
		}
		else if(auto_mode == 1){
			System.out.println("Calculating path CrossLowBar");
			path = new FalconPathPlanner(waypoints_crsLwBr);
			path.calculate(totalPathPlannerTime_crsLwBr, PLANNER_SAMPLE_RATE_S, ROBOT_TRACK_WIDTH_FT);
			invertSetpoints = false;
			shootHighGoal = false;
			shootLowGoal = false;
			cycleIntakeArm = false;
		}
		else if(auto_mode == 2){
			System.out.println("Calculating path CrossShootLow");
			path = new FalconPathPlanner(waypoints_crossShootLow);
			path.setPathBeta(0.6);
			path.setPathAlpha(0.3);
			path.setVelocityAlpha(0.01);
			path.setVelocityBeta(0.8);
			path.calculate(totalPathPlannerTime_crossShootLow, PLANNER_SAMPLE_RATE_S, ROBOT_TRACK_WIDTH_FT);
			invertSetpoints = false;
			shootHighGoal = false;
			shootLowGoal = true;
			cycleIntakeArm = true;
		}
		else if(auto_mode == 3){
			System.out.println("Calculating path CrossShootHigh");
			path = new FalconPathPlanner(waypoints_crossShootHigh);
			path.setPathBeta(0.6);
			path.setPathAlpha(0.3);
			path.setVelocityAlpha(0.01);
			path.setVelocityBeta(0.8);
			path.calculate(totalPathPlannerTime_crossShootHigh, PLANNER_SAMPLE_RATE_S, ROBOT_TRACK_WIDTH_FT);
			invertSetpoints = true;
			shootHighGoal = true;
			shootLowGoal = false;
			cycleIntakeArm = true;
		}
		else{
			System.out.println("ERROR: bad path selected, tell software they did something wrong!!!");
		}
	}
	
	/**
	 * begins background thread commanding motor values through
	 * the determined path 
	 * @return
	 */
	public int startPlayback(){
		System.out.println("Starting Path Planner");
		dt.setSafetyEnabled(false);
		timestep = 0; //reset timestamp
		motors.lmpid.setSetpoint(0); //zero out motor controllers
		motors.rmpid.setSetpoint(0);
		motors.lmpid.enable(); //enable both drivetrain PID's
		motors.rmpid.enable();
		timerThread = new java.util.Timer(); //create new thread for the playback function
		shotTimer.reset(); //Make sure the shot timer is ready to be used (zeroed out)
		playbackActive = true; //Mark that playback has begun (or, will begin shortly)
		timerThread.schedule(new PathPlanningPlayback(this), 0L, (long) ((double)PLANNER_SAMPLE_RATE_S*1000)); //Kick off playback thread. Here we go!
		return 0;
	}
	
	/**
	 * Forcibly stops any background playback occurring
	 * @return
	 */
	public int stopPlayback(){
		System.out.println("Stopping Path Planner");
		if(timerThread != null)
			timerThread.cancel(); //kill thread, assuming it was running
		playbackActive = false; //set status to not running
		motors.lmpid.setSetpoint(0); //zero out motor controllers
		motors.rmpid.setSetpoint(0);
		//Don't disable the PID's yet, it's important to keep them alive so they actually stop the wheels from turning.
		shotTimer.stop(); //Stop and reset whatever shot timer might be running
		shotTimer.reset();
		timestep = 0; //reset time (just in case? probably not needed)
		ilsm.periodicStateMach(false, false, false, false, false); //shut everything down
		return 0;
	}
	
	/**
	 * Returns true if playback is currently running, false if not.
	 * @return
	 */
	public boolean isPlaybackActive(){
		return playbackActive;
	}

	/**
	 * Playback function = should be called 
	 */
	public void plannerStep(){
		System.out.println("Running Planner Step " + timestep);
		//detect end condition where path planner has finished playback
		if(timestep >= path.numFinalPoints){
			if(timestep == path.numFinalPoints){
				shotTimer.start(); //only does something on the first call - make sure the shot timer is in fact running. Assumes it was reset at the start of path-planner auto
			}
			motors.lmpid.setSetpoint(0); //zero out motor controllers
			motors.rmpid.setSetpoint(0);
			if(shootHighGoal){ //high-goal end shot
				if(shotTimer.get() > HIGH_GOAL_SHOT_TIME_S){
					stopPlayback();
				} 
				else {
					System.out.println("Commanding High-Goal Shot, " + (HIGH_GOAL_SHOT_TIME_S-shotTimer.get()) + " s remaining.");
					ilsm.periodicStateMach(false, false, false, true, false); //command high-goal shot
				}
			}
			else if(shootLowGoal){ //low-goal end shot
				if(shotTimer.get() > LOW_GOAL_SHOT_TIME_S){
					stopPlayback();
				} 
				else {
					System.out.println("Commanding Low-Goal Shot, " + (LOW_GOAL_SHOT_TIME_S-shotTimer.get()) + " s remaining.");
					ilsm.periodicStateMach(false, true, false, false, false); //command low-goal shot (eject)
				}
			}
			else{ //no end shot
				stopPlayback(); 
			}
		}
		else{ //otherwise, continue playback
			if(invertSetpoints){
				motors.lmpid.setSetpoint(-path.smoothLeftVelocity[timestep][1]);
				motors.rmpid.setSetpoint(-path.smoothRightVelocity[timestep][1]);
			}
			else{
				motors.lmpid.setSetpoint(path.smoothLeftVelocity[timestep][1]);
				motors.rmpid.setSetpoint(path.smoothRightVelocity[timestep][1]);
			}
			
			if(cycleIntakeArm){
				//Handle arm raise-lower events
				if(timestep == intakeLowerTimeStep_crossShootLow){
					System.out.println("Lowering Intake Arm");
					Pneumatics.intakeDown();
				}
				if(timestep == intakeRaiseTimeStep_crossShootLow){
					System.out.println("Raising Intake Arm");
					Pneumatics.intakeUp();
				}
				if(timestep == intakeLowerTimeStep_crossShootHigh){
					System.out.println("Lowering Intake Arm");
					Pneumatics.intakeDown();
				}
				if(timestep == intakeRaiseTimeStep_crossShootHigh){
					System.out.println("Raising Intake Arm");
					Pneumatics.intakeUp();
				}
			}
		}
		
		timestep++;
	}
	
	//Java multithreading magic. Do not touch.
	//Touching will incour the wrath of Cthulhu, god of java and path planning.
	//May the oceans of 1's and 0's rise to praise him.
    public class PathPlanningPlayback extends TimerTask 
    {
        private casserolePathAuto m_planner;

        public PathPlanningPlayback(casserolePathAuto planner) 
        {
            if (planner == null) 
            {
                throw new NullPointerException("Given PathPlanner was null");
            }
            m_planner = planner;
        }

        @Override
        public void run() 
        {
        	m_planner.plannerStep();
        }
    }
}
