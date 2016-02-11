package org.usfirst.frc.team1736.robot;

public class casserolePathAuto {

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
	
	FalconPathPlanner path;
	
	/**
	 * Constructor
	 * 
	 */
	casserolePathAuto(){

		
		
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
	
	
}
