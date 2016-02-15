package org.usfirst.frc.team1736.robot;

public class VisionTarget {

	double width, height, center_X, center_Y, area;
	final static int targetStripWidthInches = 2;
	private int TARGET_HEIGHT = 14; //actual height of target in inches
	private int TARGET_WIDTH = 20; //actual width of target in inches
	private int CAM_X_RES = 320; //camera image width in pixels
	private int CAM_Y_RES = 240; //camera image height in pixels
	private double CAM_VIEW_ANGLE = 37.4; //Lens viewing angle - Axis M1011. Axis 206: 41.7. Axis 1013: 49
	private double CAM_LR_ANGLE_SCALE = 1; //Left/right camera angle to robot angle scale offset
	private double CAM_LR_ANGLE_OFFSET = 0; //Left/right camera offset to center of shot
	
	public VisionTarget(double width, double height, double center_X,
						double center_Y, double area)
	{
		this.width = width;
		this.height = height;
		this.center_X = center_X;
		this.center_Y = center_Y;
		this.area = area;
	}
	
	public double getTargetDistance()
	{
		return CAM_X_RES * TARGET_HEIGHT / (height * 2 * Math.tan(CAM_VIEW_ANGLE*Math.PI/(180*2)));
	}
	
	public double getTargetAngleOffset()
	{
		return (center_X + CAM_LR_ANGLE_OFFSET) * CAM_LR_ANGLE_SCALE;
	}
	
	public double getExpectedArea()
	{
		return 0;
	}
}
