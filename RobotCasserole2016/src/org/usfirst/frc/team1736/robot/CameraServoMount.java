/**
 * 
 */
package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.Servo;

/**
 * @author gerthcm
 *
 */
public class CameraServoMount {
	
	//State variables
	public double cur_pan_angle = 0;
	public double cur_tilt_angle = 0;
	
	//Startup conditions
	private static final CamPos startupPos = CamPos.DRIVE_FWD;
	
	//Position constants (in degrees)
	private static final double DRIVE_FWD_PAN_ANGLE = 90;
	private static final double DRIVE_FWD_TILT_ANGLE = 5;
	
	private static final double DRIVE_REV_PAN_ANGLE = -90;
	private static final double DRIVE_REV_TILT_ANGLE = 5;
	
	private static final double SHOOT_PAN_ANGLE = 90;
	private static final double SHOOT_TILT_ANGLE = 25;
	
	private static final double CLIMB_PAN_ANGLE = 45;
	private static final double CLIMB_TILT_ANGLE = 65;
	
	//Hookup constants
	private static final int PAN_SERVO_PORT = 9;
	private static final int TILT_SERVO_PORT = 8;
	
	//Servo objects for mount servos
	private Servo pan_servo;
	private Servo tilt_servo;
	
	/** 
	 * Constructor - initializes all the objects for a camera servo mount. Takes nothing, returns nothing.
	 */
	CameraServoMount(){
		resolveCamPos(startupPos);
		pan_servo = new Servo(PAN_SERVO_PORT);
		tilt_servo = new Servo(TILT_SERVO_PORT);
		
	}
	
	/**
	 * Commands the servos to the right spots based on the value of camera position in
	 * @param in
	 */
	
	public void setCameraPos(CamPos in){
		resolveCamPos(in);
		pan_servo.setAngle(cur_pan_angle);
		tilt_servo.setAngle(cur_tilt_angle);
		
		
	}
	
	/**
	 * Sets the pan and tilt internal variables per the
	 * position specified in the input argument.
	 * @param in - position to set the camera to.
	 */
	private void resolveCamPos(CamPos in){
		
		switch(in){
		case DRIVE_FWD:
			cur_pan_angle  = DRIVE_FWD_PAN_ANGLE;
			cur_tilt_angle = DRIVE_FWD_TILT_ANGLE;
			break;
		case DRIVE_REV:
			cur_pan_angle =  DRIVE_REV_PAN_ANGLE;
			cur_tilt_angle = DRIVE_REV_TILT_ANGLE;
			break;			
		case SHOOT:
			cur_pan_angle =  SHOOT_PAN_ANGLE;
			cur_tilt_angle = SHOOT_TILT_ANGLE;
			break;
		case CLIMB:
			cur_pan_angle =  CLIMB_PAN_ANGLE;
			cur_tilt_angle = CLIMB_TILT_ANGLE;
			break;
		default:
			System.out.println("Warning - commanded camera position " + in.name() + " is not recognized!");
			break;
		}
		
	}

}


