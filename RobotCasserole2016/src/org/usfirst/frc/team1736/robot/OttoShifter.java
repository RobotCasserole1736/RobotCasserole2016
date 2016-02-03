package org.usfirst.frc.team1736.robot;

public class OttoShifter {
	
	public static final double VAL_DEBOUNCE_THRESH = 1000;
	public static final int VAL_DEBOUNCE_TIME = 1000;
	public static final double WHEEL_ACCEL_DEBOUNCE_THRESH = 1000;
	public static final int WHEEL_ACCEL_DEBOUNCE_TIME = 1000;
	public static final double VERT_ACCEL_DEBOUNCE_THRESH = 1000;
	public static final int VERT_ACCEL_DEBOUNCE_TIME = 1000;
	public static final double CURRENT_DEBOUNCE_THRESH = 1000;
	public static final int CURRENT_DEBOUNCE_TIME = 1000;



	
	public boolean gear; //True is high and false is low
	public DaBouncer VelDebounce;
	public DaBouncer WheelAccelDebounce;
	public DaBouncer VertAccelDebounce;
	public DaBouncer CurrentDebounce;
	
	
	OttoShifter(){
		VelDebounce = new DaBouncer();
		WheelAccelDebounce = new DaBouncer();
		VertAccelDebounce = new DaBouncer();
		CurrentDebounce = new DaBouncer();
		
		gear = true;
		
		VelDebounce.threshold = VAL_DEBOUNCE_THRESH;
		VelDebounce.dbnc = VAL_DEBOUNCE_TIME;
		WheelAccelDebounce.threshold = WHEEL_ACCEL_DEBOUNCE_THRESH;
		WheelAccelDebounce.dbnc = WHEEL_ACCEL_DEBOUNCE_TIME;
		VertAccelDebounce.threshold  = VERT_ACCEL_DEBOUNCE_THRESH;
		VertAccelDebounce.dbnc = VERT_ACCEL_DEBOUNCE_TIME; 
		CurrentDebounce.threshold = CURRENT_DEBOUNCE_THRESH;
		CurrentDebounce.dbnc = CURRENT_DEBOUNCE_TIME; 
	}
	 public void OttoShifterPeriodic(
			 double VelocityValue,
			 double WheelAceelValue,
			 double VertAccelValue,
			 double CurrentValue,
			 boolean DriverUpshiftCmd,
			 boolean DriverDownshiftCmd){
		/* switch (gear){
		 case true:
			 
			 break;
			 
		 case false;
		 
		 break;
	Didn't work- Riperino in Pepperino
			*/ 
		 
		 boolean VelDebounceState = VelDebounce.BelowDebounce(VelocityValue); //rinse wash repeat for the other four
		 boolean WheelAccelDebounceState = WheelAccelDebounce.BelowDebounce(WheelAceelValue);
		 boolean VertAccelDebounceState = VertAccelDebounce.BelowDebounce(VertAccelValue);
		 boolean CurrentDebounceState = CurrentDebounce.AboveDebounce(CurrentValue);
	 		 
		 if (gear == true){ //Top Gear
			 if (DriverDownshiftCmd == true|| (VelDebounceState == true && WheelAccelDebounceState == true && VertAccelDebounceState == true && CurrentDebounceState == true)){
				 gear = false;
			 }{
				 gear = true;
			 }{
			if (DriverUpshiftCmd == true){
				gear = true;
			}
			 }
			
			 
			 
		 }
		 else{ //gear == false
		 
		 }
		 }
		 
}
	
