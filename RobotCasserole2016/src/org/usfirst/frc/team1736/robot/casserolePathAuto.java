package org.usfirst.frc.team1736.robot;

import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team1736.robot.I2CGyro.GyroTask;

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
	double[][] waypoints_mode3 = new double[][]{ // do nothing
		{0,0}
	};
	
	double totalTime_mode0 = 2;
	double totalTime_mode1 = 5;
	double totalTime_mode2 = 10;
	double totalTime_mode3 = 1;
	
	double timeStep = 0.02; //20ms update rate 
	double robotTrackWidth = 1.9; //1.9ft wide tracks
	
	int timestep = 0;
	
	FalconPathPlanner path;
	
	//Output Device - the drivetrain
	DriveTrain dt;
	
	//Playback thread
	Timer timerThread;
	
	
	/**
	 * Constructor
	 * 
	 */
	casserolePathAuto(DriveTrain dt_in){
		dt = dt_in;
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
			path = new FalconPathPlanner(waypoints_mode3);
			path.calculate(totalTime_mode3, timeStep, robotTrackWidth);
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
		timerThread.schedule(new PathPlanningPlayback(this), 0L, (long) (timeStep));
		return 0;
	}
	
	/**
	 * Forcibly stops any background playback ocurring
	 * @return
	 */
	public int stopPlatback(){
		timerThread.cancel();
		timestep = 0;
		return 0;
	}
	
	/**
	 * Returns true if playback is currently running, false if not.
	 * @return
	 */
	public boolean isPlaybackActive(){
		
		return false;
	}

	public void plannerStep(){
		
	}
	
	//Java multithreading magic. Do not touch.
	//Touching will incour the wrath of Cthulhu, god of java and path planning.
	//May the oceans of 1's and 0's rise to praise him.
    private class PathPlanningPlayback extends TimerTask 
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
