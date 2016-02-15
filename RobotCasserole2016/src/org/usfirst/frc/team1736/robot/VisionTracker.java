package org.usfirst.frc.team1736.robot;

import java.io.IOException;
import java.util.Arrays;
import java.util.TreeMap;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class VisionTracker {
	private VisionTarget[]  sortedTargets = new VisionTarget[3];
	private VisionTarget[]  prevSortedTargets = new VisionTarget[3];
	private double GOAL_ASPECT_RATIO = 1.4286;
	private double GOAL_AREA_RATIO = 0.2857;
	private TreeMap<Double, Integer> targetMap = new TreeMap<Double, Integer>();
	
	public VisionTarget bestTarget; //Target with best error
	public VisionTarget trackedTarget; //Target that might be better to track

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
//		for(int i = 0; i < 3; i++)
//			prevSortedTargets[i] = sortedTargets[i];
		double[] targetAreas = getTargetAreaArray();
		double[] targetWidths = getTargetWidthArray();
		double[] targetHeights = getTargetHeightArray();
		double[] targetXCenters = getTargetCenterXArray();
		double[] targetYCenters = getTargetCenterYArray();
		
		double targetAreaError;
		double targetAspectError;
		
		for(int i = 0; i < targetAreas.length; i++)
		{
			targetAreaError = Math.abs(targetAreas[i] / (targetWidths[i] * targetHeights[i]) - GOAL_AREA_RATIO) / GOAL_AREA_RATIO;
			targetAspectError = Math.abs(targetWidths[i] * targetHeights[i] - GOAL_ASPECT_RATIO) / GOAL_ASPECT_RATIO;
			
			//Get "total" error by multiplying together, then add to the map
			targetMap.put(targetAreaError*targetAspectError, i);
		}
		
		if(targetMap.isEmpty())
		{
			bestTarget = null;
			return;
		}
		
		int bestTargetIdx = targetMap.firstEntry().getValue();
		bestTarget = new VisionTarget(targetWidths[bestTargetIdx], targetHeights[bestTargetIdx],
				targetXCenters[bestTargetIdx], targetYCenters[bestTargetIdx], targetAreas[bestTargetIdx]);
		
		//May want to do more processing later to determine tracked target.  For now we can just use best target
		
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
