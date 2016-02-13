package org.usfirst.frc.team1736.robot;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Talon;
public class Climb {
	
	
	public static final int WINCHMOTOR1_CHANNEL = 6; //CMG - confirmed 2/10/2016
	public static final int WINCHMOTOR2_CHANNEL = 7; //CMG - confirmed 2/13/2016
	public static final int TAPEMOTOR_CHANNEL = 4; //CMG - confirmed 2/2/2016
	//public static final int TAPETRIGGER_CHANNEL=5; //CMG - still unknown if used!
	Talon winchmotor1;
	Talon winchmotor2;
	VictorSP tapemotor;
	DigitalInput tapetrigger;
	boolean tapeTriggerState;
	
	Climb(){
		winchmotor1 = new Talon(WINCHMOTOR1_CHANNEL);
		winchmotor2 = new Talon(WINCHMOTOR2_CHANNEL);
		tapemotor = new VictorSP(TAPEMOTOR_CHANNEL);
		//tapetrigger = new DigitalInput(TAPETRIGGER_CHANNEL);
		tapeTriggerState = false;
		
	}

	void periodicClimb(boolean climbEnable, double tapeExtend, double winch){
		float cEn;
		boolean sign;
		//boolean tapeTriggerState = tapetrigger.get(); //Use this one if we actually use the tapeTrigger sensor
		
		if(tapeExtend<0){
			sign=true;
		}
		else{
			sign=false;
		}
		tapeExtend=Math.pow(tapeExtend, 2);
		if(sign){
			tapeExtend=tapeExtend*(-1);
		}
		
		
		if(climbEnable){
			 cEn = 1;
			 Pneumatics.intakeUp();//as soon as climb is enabled, raise the intake to prevent a retraction causing bad things while hanging
		}
		
		else{
			 cEn = 0;
		}
		
		if(cEn==1 && tapeTriggerState==false){
			tapemotor.set(tapeExtend);
			winchmotor1.set(Math.pow(winch, 2));
			winchmotor2.set(Math.pow(winch, 2));
		}
		
		if(cEn==0 && tapeTriggerState==false){
			tapemotor.set(0);
			winchmotor1.set(0);
			winchmotor2.set(0);
		}
		
		if(cEn==0 && tapeTriggerState==true){
			tapemotor.set(0);
			winchmotor1.set(0);
			winchmotor2.set(0);
		}
		
		if(cEn==1 && tapeTriggerState==true){
			if(tapeExtend>0){
				tapeExtend=tapeExtend*0;
			}
			tapemotor.set(tapeExtend);
			winchmotor1.set(Math.pow(winch, 2));
			winchmotor2.set(Math.pow(winch, 2));
		}
		
		
		
	}
	
	

	
}
