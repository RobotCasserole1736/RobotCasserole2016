package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;

public class Pneumatics {
	private static Compressor compressor = new Compressor();
	private static Solenoid shifterSolenoid = new Solenoid(1);
	private static Solenoid intakeSolenoid = new Solenoid(2);
	
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
		shifterSolenoid.set(true);
	}
	
	public static void shiftToHighGear(){
		shifterSolenoid.set(false);
	}
	
	public static void intakeUp(){
		intakeSolenoid.set(true);
	}
	
	public static void intakeDown(){
		intakeSolenoid.set(false);
	}
	
	public static boolean isHighGear()
	{
	    return !shifterSolenoid.get();   
	}
	
	public static boolean isIntakeDown(){
		return !intakeSolenoid.get();
	}
}
 