package org.usfirst.frc.team1736.robot;

public class OttoShifter {
	
	public static final double VAL_DEBOUNCE_THRESH_RPM = 1; //Shift when wheel speed is below this RPM val for 
	public static final int VAL_DEBOUNCE_TIME_LOOPS = 1000; // this number of loops
	public static final double WHEEL_ACCEL_DEBOUNCE_THRESH_RPMperS = -10000; // Shift when signed rotational acceleration is below this value
	public static final int WHEEL_ACCEL_DEBOUNCE_TIME_LOOPS = 1000; // for this number of loops
	public static final double VERT_ACCEL_DEBOUNCE_THRESH_G = 30; // Shift when the acceleration is above this G force (input the abs val)
	public static final int VERT_ACCEL_DEBOUNCE_TIME_LOOPS = 1000; //for this number of loops
	public static final double CURRENT_DEBOUNCE_THRESH_A = 1000; //Shift when the current draw from the battery is above this Amperage
	public static final int CURRENT_DEBOUNCE_TIME_LOOPS = 1000; //for this many loops

	boolean VelDebounceState;
	boolean WheelAccelDebounceState;
	boolean VertAccelDebounceState;
	boolean CurrentDebounceState;

	
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
		
		VelDebounce.threshold = VAL_DEBOUNCE_THRESH_RPM;
		VelDebounce.dbnc = VAL_DEBOUNCE_TIME_LOOPS;
		WheelAccelDebounce.threshold = WHEEL_ACCEL_DEBOUNCE_THRESH_RPMperS;
		WheelAccelDebounce.dbnc = WHEEL_ACCEL_DEBOUNCE_TIME_LOOPS;
		VertAccelDebounce.threshold  = VERT_ACCEL_DEBOUNCE_THRESH_G;
		VertAccelDebounce.dbnc = VERT_ACCEL_DEBOUNCE_TIME_LOOPS; 
		CurrentDebounce.threshold = CURRENT_DEBOUNCE_THRESH_A;
		CurrentDebounce.dbnc = CURRENT_DEBOUNCE_TIME_LOOPS; 
	}
	 public void OttoShifterPeriodic(
			 double VelocityValue_RPM,
			 double WheelAceelValue_RPMperS,
			 double VertAccelValue_G,
			 double CurrentValue_A,
			 boolean DriverUpshiftCmd,
			 boolean DriverDownshiftCmd){
		/* switch (gear){
		 case true:
			 
			 break;
			 
		 case false;
		 
		 break;
	Didn't work- Riperino in Pepperino
			*/ 
		 
		 VelDebounceState = VelDebounce.BelowDebounce(VelocityValue_RPM); //rinse wash repeat for the other four
		 WheelAccelDebounceState = WheelAccelDebounce.BelowDebounce(WheelAceelValue_RPMperS);
		 VertAccelDebounceState = VertAccelDebounce.AboveDebounce(VertAccelValue_G);
		 CurrentDebounceState = CurrentDebounce.AboveDebounce(CurrentValue_A);
	 		 
		 if (gear == true){ //Top Gear
			 if (DriverDownshiftCmd == true|| (VelDebounceState == true && WheelAccelDebounceState == true && VertAccelDebounceState == true && CurrentDebounceState == true)){
				 gear = false;
			 }
			 else{
				 gear = true;
			 }
			 			 
		 }
		 else{ //gear == false (low gear)
			 
			if (DriverUpshiftCmd == true){
				gear = true;
			}
			
		 }
	 }
		 
}
	
