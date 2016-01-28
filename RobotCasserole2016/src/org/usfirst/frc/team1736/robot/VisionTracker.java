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
	
	public VisionTarget getVisionTarget()
	{
		return new VisionTarget(0,0,0,0,0);
	}
}
