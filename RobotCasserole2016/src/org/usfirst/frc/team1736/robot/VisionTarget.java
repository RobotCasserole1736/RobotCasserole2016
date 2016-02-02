package org.usfirst.frc.team1736.robot;

public class VisionTarget {

	double width, height, center_X, center_Y, area;
	final static int targetStripWidthInches = 2;
	
	public VisionTarget(double width, double height, double center_X,
						double center_Y, double area)
	{
		this.width = width;
		this.height = height;
		this.center_X = center_X;
		this.center_Y = center_Y;
		this.area = area;
	}
	
	public double getExpectedArea()
	{
		return 0;
	}
}
