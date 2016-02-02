package org.usfirst.frc.team1736.robot;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.VictorSP;
public class Climb {
	public static final int WINCHMOTOR1_CHANNEL = 5;
	public static final int WINCHMOTOR2_CHANNEL = 6;
	public static final int TAPEMOTOR_CHANNEL = 7;
	TalonSRX winchmotor1;
	TalonSRX winchmotor2;
	VictorSP tapemotor;
	
	Climb(){
		winchmotor1 = new TalonSRX(WINCHMOTOR1_CHANNEL);
		winchmotor2 = new TalonSRX(WINCHMOTOR2_CHANNEL);
		tapemotor = new VictorSP(TAPEMOTOR_CHANNEL);
		
		
	}

	void periodicClimb(boolean climbEnable, double tapeExtend, double winch){
		float cEn;
		if(climbEnable){
			 cEn = 1;
		}
		
		else{
			 cEn = 0;
		}
		
		if(cEn==1){
			tapemotor.set(Math.pow(tapeExtend,2));
			winchmotor1.set(Math.pow(winch, 2));
			winchmotor2.set(Math.pow(winch, 2));
		}
		
		if(cEn==0){
			tapemotor.set(0);
			winchmotor1.set(0);
			winchmotor2.set(0);
		}
		
		
	}
	
	

	
}
