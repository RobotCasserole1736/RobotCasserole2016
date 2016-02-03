package org.usfirst.frc.team1736.robot;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Talon;
public class Climb {
	
	
	public static final int WINCHMOTOR1_CHANNEL = 5;
	public static final int WINCHMOTOR2_CHANNEL = 6;
	public static final int TAPEMOTOR_CHANNEL = 4;
	public static final int TAPETRIGGER_CHANNEL=10;
	Talon winchmotor1;
	Talon winchmotor2;
	VictorSP tapemotor;
	DigitalInput tapetrigger;
	
	Climb(){
		winchmotor1 = new Talon(WINCHMOTOR1_CHANNEL);
		winchmotor2 = new Talon(WINCHMOTOR2_CHANNEL);
		tapemotor = new VictorSP(TAPEMOTOR_CHANNEL);
		tapetrigger = new DigitalInput(TAPETRIGGER_CHANNEL);
		
	}

	void periodicClimb(boolean climbEnable, double tapeExtend, double winch){
		float cEn;
		boolean sign;
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
		}
		
		else{
			 cEn = 0;
		}
		
		if(cEn==1 && tapetrigger.get()==false){
			tapemotor.set(tapeExtend);
			winchmotor1.set(Math.pow(winch, 2));
			winchmotor2.set(Math.pow(winch, 2));
		}
		
		if(cEn==0 && tapetrigger.get()==false){
			tapemotor.set(0);
			winchmotor1.set(0);
			winchmotor2.set(0);
		}
		
		if(cEn==0 && tapetrigger.get()==true){
			tapemotor.set(0);
			winchmotor1.set(0);
			winchmotor2.set(0);
		}
		
		if(cEn==1 && tapetrigger.get()==true){
			if(tapeExtend>0){
				tapeExtend=tapeExtend*0;
			}
			tapemotor.set(tapeExtend);
			winchmotor1.set(Math.pow(winch, 2));
			winchmotor2.set(Math.pow(winch, 2));
		}
		
		
		
	}
	
	

	
}
