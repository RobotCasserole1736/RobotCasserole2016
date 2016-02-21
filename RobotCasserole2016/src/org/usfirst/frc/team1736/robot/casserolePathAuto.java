package org.usfirst.frc.team1736.robot;

import java.util.Timer;
import java.util.TimerTask;

public class casserolePathAuto {

	//Path Planner Constants
	final double[][] waypoints_apchDfns = new double[][]{ // go up to defenses
		{0,0},
		{2,0}		
	};
	
	final double[][] waypoints_crsLwBr = new double[][]{ //cross low-bar defense
		{0,0},
		{5,0}		
	};
	
	final double[][] waypoints_crossShootHigh = new double[][]{ //cross and shoot
		{0,0},
		{5,0},
		{8,5} //Total guess for testing, we'll have to use Justin's points
	};
	final double[][] waypoints_modeNothing = new double[][]{ // do nothing
		{0,0}
	};
	
	final double totalPathPlannerTime_apchDfns = 2;
	final double totalPathPlannerTime_crsLwBr = 5;
	final double totalPathPlannerTime_crossShootHigh = 10;
	final double totalPathPlannerTime_modeNothing = 1;
	
	final double PLANNER_SAMPLE_RATE = 0.1; //100ms update rate 
	final double ROBOT_TRACK_WIDTH_FT = 1.9; //1.9ft wide tracks
	
	int timestep = 0;
	
	FalconPathPlanner path;
	
	//Output Device - the drivetrain (and sometimes the shooter)
	DriveTrain dt;
	DriveMotorsPIDVelocity motors;
	IntakeLauncherStateMachine ilsm;
	
	//Playback thread
	Timer timerThread;
	boolean playbackActive = false;
	
	//End-of-path event variables
	boolean shootHighGoal = false;
	boolean shootLowGoal = false;
	edu.wpi.first.wpilibj.Timer shotTimer;
	final double HIGH_GOAL_SHOT_TIME_S = 2;
	final double LOW_GOAL_SHOT_TIME_S = 8;
	
	
	/**
	 * Constructor
	 * 
	 */
	casserolePathAuto(DriveTrain dt_in, IntakeLauncherStateMachine ilsm_in){
		dt = dt_in;
		ilsm = ilsm_in;
		motors = new DriveMotorsPIDVelocity(dt);
		
	}
	
	
	/**
	 * Calc Path - must be run in auto_init to calculate a path based on 
	 * the selected auto mode.
	 * @param auto_mode
	 */
	public void calcPath(int auto_mode){
		if(auto_mode == 0){
			path = new FalconPathPlanner(waypoints_apchDfns);
			path.calculate(totalPathPlannerTime_apchDfns, PLANNER_SAMPLE_RATE, ROBOT_TRACK_WIDTH_FT);
		}
		else if(auto_mode == 1){
			path = new FalconPathPlanner(waypoints_crsLwBr);
			path.calculate(totalPathPlannerTime_crsLwBr, PLANNER_SAMPLE_RATE, ROBOT_TRACK_WIDTH_FT);
		}
		else if(auto_mode == 2){
			path = new FalconPathPlanner(waypoints_crossShootHigh);
			path.calculate(totalPathPlannerTime_crossShootHigh, PLANNER_SAMPLE_RATE, ROBOT_TRACK_WIDTH_FT);
		}
		else{
			path = new FalconPathPlanner(waypoints_modeNothing);
			path.calculate(totalPathPlannerTime_modeNothing, PLANNER_SAMPLE_RATE, ROBOT_TRACK_WIDTH_FT);
		}
	}
	
	/**
	 * begins background thread commanding motor values through
	 * the determined path 
	 * @return
	 */
	public int startPlayback(){
		timestep = 0;
		timerThread = new java.util.Timer();
		playbackActive = true;
		timerThread.schedule(new PathPlanningPlayback(this), 0L, (long) (PLANNER_SAMPLE_RATE));
		return 0;
	}
	
	/**
	 * Forcibly stops any background playback occurring
	 * @return
	 */
	public int stopPlayback(){
		if(timerThread != null)
			timerThread.cancel(); //kill thread
		playbackActive = false; //set status to not running
		motors.lmpid.setSetpoint(0); //zero out motor controllers
		motors.rmpid.setSetpoint(0);
		timestep = 0; //reset time (just in case)
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
		//detect end condition
		if(timestep > path.numFinalPoints){
			if()
			stopPlayback();
		}
		else{ //otherwise continue playback
			motors.lmpid.setSetpoint(path.smoothLeftVelocity[timestep][1]);
			motors.rmpid.setSetpoint(path.smoothRightVelocity[timestep][1]);
			timestep++;
		}

			
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
