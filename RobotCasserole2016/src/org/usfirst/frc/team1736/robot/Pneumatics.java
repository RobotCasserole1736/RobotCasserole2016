package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Pneumatics {
	private static Compressor compressor = new Compressor();
	private static DoubleSolenoid shifterSolenoid = new DoubleSolenoid(1,2);
	private static DoubleSolenoid intakeSolenoid = new DoubleSolenoid(3,4);
	
	public static void startCompressor(){
		compressor.start();
	}
	
	public static void stopCompressor(){
		compressor.stop();
	}
	
	public static boolean getPressureSwitchValue(){
		return compressor.getPressureSwitchValue();
	}
	
	public static float getCurrent(){
		return compressor.getCompressorCurrent();
	}
	
	public static void shiftToLowGear(){
		shifterSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	
	public static void shiftToHighGear(){
		shifterSolenoid.set(DoubleSolenoid.Value.kReverse);
	}
	
	public static void intakeUp(){
		intakeSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	
	public static void intakeDown(){
		intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
	}
}
 