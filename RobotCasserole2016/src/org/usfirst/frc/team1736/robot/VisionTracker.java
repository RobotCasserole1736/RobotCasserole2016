package org.usfirst.frc.team1736.robot;

import java.io.IOException;
import java.util.Arrays;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class VisionTracker {
	private VisionTarget[]  sortedTargets = new VisionTarget[3];
	private VisionTarget[]  prevSortedTargets = new VisionTarget[3];
	private double GOAL_ASPECT_RATIO = 1.4286;
	private double GOAL_AREA_RATIO = 0.2857;

	NetworkTable grip = NetworkTable.getTable("grip");
	
	public VisionTracker(String[] GRIPArgs)
	{
		try {
			Runtime.getRuntime().exec(GRIPArgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < 3; i++)
		{
			sortedTargets[i] = null;
			prevSortedTargets[i] = null;
		}
	}
	
	public void readTargets()
	{
		for(int i = 0; i < 3; i++)
			prevSortedTargets[i] = sortedTargets[i];
		double[] targetAreas = getTargetAreaArray();
		double[] targetWidths = getTargetWidthArray();
		double[] targetHeights = getTargetHeightArray();
		
		double[] targetAreaErrors = new double[targetAreas.length];
		double[] targetAspectErrors = new double[targetAreas.length];
		
		for(int i = 0; i < targetAreas.length; i++)
		{
			targetAreaErrors[i] = Math.abs(targetAreas[i] / (targetWidths[i] * targetHeights[i]) - GOAL_AREA_RATIO);
			targetAspectErrors[i] = Math.abs(targetWidths[i] * targetHeights[i] - GOAL_ASPECT_RATIO);
		}
		
		//Need to determine a way to figure out the minimum error across both terms
		
		//Put the best target(s) into array
	}
	
	public double[] getTargetWidthArray()
	{
		return grip.getNumberArray("targets/width", new double[0]);
	}
	
	public double[] getTargetHeightArray()
	{
		return grip.getNumberArray("targets/height", new double[0]);
	}
	
	public double[] getTargetCenterXArray()
	{
		return grip.getNumberArray("targets/center_x", new double[0]);
	}
	
	public double[] getTargetCenterYArray()
	{
		return grip.getNumberArray("targets/center_y", new double[0]);
	}
	
	public double[] getTargetAreaArray()
	{
		return grip.getNumberArray("targets/area", new double[0]);
	}
}
