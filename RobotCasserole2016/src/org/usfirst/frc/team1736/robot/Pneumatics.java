package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;

public class Pneumatics {
	private static Compressor compressor = new Compressor();
	private static Solenoid shifterSolenoid = new Solenoid(1);
	private static Solenoid intakeSolenoid = new Solenoid(2);
	private static AnalogInput psensor = new AnalogInput(1);
	private static Solenoid DrawbridgeArmSolenoid = new Solenoid(3);

	
	public static void startCompressor(){
		compressor.start();
	}
	
	public static void stopCompressor(){
		compressor.stop();
	}
	
	public static double getPressurePsi(){ //per datasheet at http://www.meas-spec.com/downloads/U7100.pdf
		return ((psensor.getVoltage()/5.0)-0.1)*150.0/0.8;
	}
	
	public static boolean getPressureSwitchValue(){
		return compressor.getPressureSwitchValue();
	}
	
	public static double getCurrent(){
		return compressor.getCompressorCurrent();
	}
	
	public static void shiftToLowGear(){
		shifterSolenoid.set(false);
	}
	
	public static void shiftToHighGear(){
		shifterSolenoid.set(true);
	}
	
	public static void intakeUp(){
		intakeSolenoid.set(false);
	}
	
	public static void intakeDown(){
		intakeSolenoid.set(true);
	}
	
	public static boolean isHighGear()
	{
	    return shifterSolenoid.get();//Note this is dependent upon whether solenoid engaged means high or low gear!   
	}
	
	public static boolean isIntakeDown(){
		return !intakeSolenoid.get();
	}
	
	public static void extendDrawbridgeArmCylinder(){
		DrawbridgeArmSolenoid.set(true);
	}
	
	public static void retractDrawbridgeArmCylinder(){
		DrawbridgeArmSolenoid.set(false);
	}
}
 