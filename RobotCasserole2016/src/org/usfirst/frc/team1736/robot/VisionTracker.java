package org.usfirst.frc.team1736.robot;

import java.io.IOException;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class VisionTracker {

	NetworkTable grip = NetworkTable.getTable("grip");
	
	public VisionTracker(String[] GRIPArgs)
	{
		try {
			Runtime.getRuntime().exec(GRIPArgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readTargets()
	{
		//Assuming that the tracked target with the least error in expected area is the closest target.
		//But that assumption requires testing, as Aaron does not trust himself 100%
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
