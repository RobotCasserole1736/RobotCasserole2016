package org.usfirst.frc.team1736.robot;

public class OttoShifter {
	
	public static final double VAL_DEBOUNCE_THRESH_RPM = 100; //Shift when wheel speed is below this RPM val for 
	public static final int VAL_DEBOUNCE_TIME_LOOPS = 30; // this number of loops
	public static final double WHEEL_ACCEL_DEBOUNCE_THRESH_RPMperS = -1000; // Shift when signed rotational acceleration is below this value
	public static final int WHEEL_ACCEL_DEBOUNCE_TIME_LOOPS = 1; // for this number of loops
	public static final double VERT_ACCEL_DEBOUNCE_THRESH_G = 0.25; // Shift when the acceleration is above this G force (input the abs val)
	public static final int VERT_ACCEL_DEBOUNCE_TIME_LOOPS = 1; //for this number of loops
	public static final double CURRENT_DEBOUNCE_THRESH_A = 100; //Shift when the current draw from the battery is above this Amperage
	public static final int CURRENT_DEBOUNCE_TIME_LOOPS = 2; //for this many loops
	
	public static final double STALLED_CURRENT_DEBOUNCE_THRESH_A = 180;
	public static final int STALLED_CURRENT_DEBOUNCE_TIME_LOOPS = 15;
	
	public static final boolean AUTO_DNSHIFT_ENABLE = true;

	boolean VelDebounceState;
	boolean WheelAccelDebounceState;
	boolean VertAccelDebounceState;
	boolean CurrentDebounceState;
	boolean StallCurrentDebounceState;

	
	public boolean gear; //True is high and false is low
	public DaBouncer VelDebounce;
	public DaBouncer WheelAccelDebounce;
	public DaBouncer VertAccelDebounce;
	public DaBouncer CurrentDebounce;
	
	public DaBouncer StallCurrentDebounce;
	
	
	OttoShifter(){
		VelDebounce = new DaBouncer();
		WheelAccelDebounce = new DaBouncer();
		VertAccelDebounce = new DaBouncer();
		CurrentDebounce = new DaBouncer();
		
		StallCurrentDebounce = new DaBouncer();
		
		gear = false; //start in low gear
		
		VelDebounce.threshold = VAL_DEBOUNCE_THRESH_RPM;
		VelDebounce.dbnc = VAL_DEBOUNCE_TIME_LOOPS;
		WheelAccelDebounce.threshold = WHEEL_ACCEL_DEBOUNCE_THRESH_RPMperS;
		WheelAccelDebounce.dbnc = WHEEL_ACCEL_DEBOUNCE_TIME_LOOPS;
		VertAccelDebounce.threshold  = VERT_ACCEL_DEBOUNCE_THRESH_G;
		VertAccelDebounce.dbnc = VERT_ACCEL_DEBOUNCE_TIME_LOOPS; 
		CurrentDebounce.threshold = CURRENT_DEBOUNCE_THRESH_A;
		CurrentDebounce.dbnc = CURRENT_DEBOUNCE_TIME_LOOPS; 
		
		StallCurrentDebounce.threshold = STALLED_CURRENT_DEBOUNCE_THRESH_A;
		StallCurrentDebounce.dbnc = STALLED_CURRENT_DEBOUNCE_TIME_LOOPS; 
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
		 StallCurrentDebounceState = StallCurrentDebounce.AboveDebounce(CurrentValue_A);
	 		 
		 if (gear == true){ //Top Gear
			 if (DriverUpshiftCmd == false || (VelDebounceState == true &&  //"ran into a wall" downshift
					                           WheelAccelDebounceState == true && 
					                           VertAccelDebounceState == true && 
					                           CurrentDebounceState == true && 
					                           AUTO_DNSHIFT_ENABLE == true)
					                       || (StallCurrentDebounceState == true && //"Been pushing in high gear too long" downshift
					                           AUTO_DNSHIFT_ENABLE == true)){
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
	
