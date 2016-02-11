package org.usfirst.frc.team1736.robot;

import java.util.Timer;
import java.util.TimerTask;

public class casserolePathAuto {

	//Path Planner Constants
	double[][] waypoints_mode0 = new double[][]{ // go up to defense
		{0,0},
		{2,0}		
	};
	
	double[][] waypoints_mode1 = new double[][]{ //cross defense
		{0,0},
		{5,0}		
	};
	
	double[][] waypoints_mode2 = new double[][]{ //cross and cross back
		{0,0},
		{5,0},
		{0,0}
	};
	double[][] waypoints_modeNothing = new double[][]{ // do nothing
		{0,0}
	};
	
	double totalTime_mode0 = 2;
	double totalTime_mode1 = 5;
	double totalTime_mode2 = 10;
	double totalTime_modeNothing = 1;
	
	double timeStep = 0.1; //100ms update rate 
	double robotTrackWidth = 1.9; //1.9ft wide tracks
	
	int timestep = 0;
	
	FalconPathPlanner path;
	
	//Output Device - the drivetrain
	DriveTrain dt;
	DriveMotorsPIDVelocity motors;
	
	//Playback thread
	Timer timerThread;
	boolean playbackActive = false;
	
	
	/**
	 * Constructor
	 * 
	 */
	casserolePathAuto(DriveTrain dt_in){
		dt = dt_in;
		motors = new DriveMotorsPIDVelocity(dt);
	}
	
	
	/**
	 * Calc Path - must be run in auto_init to calculate a path based on 
	 * the selected auto mode.
	 * @param auto_mode
	 */
	public void calcPath(int auto_mode){
		if(auto_mode == 0){
			path = new FalconPathPlanner(waypoints_mode0);
			path.calculate(totalTime_mode0, timeStep, robotTrackWidth);
		}
		else if(auto_mode == 1){
			path = new FalconPathPlanner(waypoints_mode1);
			path.calculate(totalTime_mode1, timeStep, robotTrackWidth);
		}
		else if(auto_mode == 2){
			path = new FalconPathPlanner(waypoints_mode2);
			path.calculate(totalTime_mode2, timeStep, robotTrackWidth);
		}
		else{
			path = new FalconPathPlanner(waypoints_modeNothing);
			path.calculate(totalTime_modeNothing, timeStep, robotTrackWidth);
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
		timerThread.schedule(new PathPlanningPlayback(this), 0L, (long) (timeStep));
		return 0;
	}
	
	/**
	 * Forcibly stops any background playback occurring
	 * @return
	 */
	public int stopPlayback(){
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
	 * Playback function = should 
	 */
	public void plannerStep(){
		//detect end condition
		if(timestep > path.numFinalPoints){
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
