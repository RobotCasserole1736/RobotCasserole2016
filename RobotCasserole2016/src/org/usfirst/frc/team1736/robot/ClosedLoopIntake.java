package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

public class ClosedLoopIntake extends PIDSubsystem {
	
	//Tune constants
	static double P = 0.0025;
	static double I = 0.0003;
	static double D = 0.0006;
	final double RETRACT_DEGREES = 360;
	
	public static final double INTAKE_LAUNCH_FEED_SPEED = 0.8;
	public static final double INTAKE_IN_SPEED = 1.0;
	public static final double INTAKE_EJECT_SPEED = -1.0;
	
	//Mechanism constants
	final double ENCODER_DEG_PER_TICK = 360.0/(120.0*4.0); //measure in degrees. 120 ticks per revolution, decoded as quadrature (4x)
	final int ENCODER_CH_1 = 7;
	final int ENCODER_CH_2 = 6;
	final static int INTAKE_MOTOR_ID = 5;
	
	Victor intake_motor;
	Encoder intake_encoder;
	
	public volatile IntLncState present_state;
	public volatile IntLncState next_state;
	
	public ClosedLoopIntake() {
		super("ClosedLoopIntakePID", P, I, D); //we don't need to WPILIB feed forward. we do feed fowrard ourselfs cuz they were silly with their implementation.
    	intake_motor = new Victor(INTAKE_MOTOR_ID);
    	intake_encoder = new Encoder(ENCODER_CH_1,ENCODER_CH_2);
    	intake_encoder.setReverseDirection(true);
		setOutputRange(-1,1); 
		present_state = IntLncState.STOPPED_NO_BALL;
		next_state = IntLncState.STOPPED_NO_BALL;
		enable();
	}

	@Override
	protected double returnPIDInput() {
		// TODO Auto-generated method stub
		return intake_encoder.getRaw()*ENCODER_DEG_PER_TICK;
	}

	@Override
	protected void usePIDOutput(double output) {
		if(next_state == IntLncState.INTAKE || next_state == IntLncState.INTAKE_OVD){
			intake_motor.set(INTAKE_IN_SPEED);
			this.setSetpoint(intake_encoder.getRaw()*ENCODER_DEG_PER_TICK);
		}
		else if (next_state == IntLncState.EJECT){
			intake_motor.set(INTAKE_EJECT_SPEED);
			this.setSetpoint(intake_encoder.getRaw()*ENCODER_DEG_PER_TICK);
		}
		else if (next_state == IntLncState.LAUNCH){
			intake_motor.set(INTAKE_LAUNCH_FEED_SPEED);
			this.setSetpoint(intake_encoder.getRaw()*ENCODER_DEG_PER_TICK);
		}
		else if(next_state == IntLncState.RETRACT || next_state == IntLncState.WAIT_FOR_SPOOLUP){ //Run PID while retracting or while waiting for shooter
			if(present_state != IntLncState.RETRACT && next_state == IntLncState.RETRACT){
				this.setSetpoint(intake_encoder.getRaw()*ENCODER_DEG_PER_TICK + RETRACT_DEGREES);
			}
			intake_motor.set(-output); //invert motor direction. Only place we actually use the PID
		}
		else{ //stopped, carry, etc.
			intake_motor.set(0);
			this.setSetpoint(intake_encoder.getRaw()*ENCODER_DEG_PER_TICK);
		}
			
		present_state = next_state;
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
		//memes
		
	}

}