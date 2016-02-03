package org.usfirst.frc.team1736.robot;

public class DaBouncer {

	public double threshold; //The point at which the the value of a variable should be less than
	public int dbnc; //The amount of time the value of a variable is over the threshold
	
	double DebounceCounter;
	
	
	
	public boolean AboveDebounce(double input){
		if (input > threshold){
			DebounceCounter++;
			}
			else 
			{ DebounceCounter = 0;
			}
		if (DebounceCounter > dbnc){
			return true;
		}
		else {
			return false;
			}
		}
		
	public boolean BelowDebounce(double input){
		if (input < threshold){
			DebounceCounter++;
			}else 
			{ DebounceCounter = 0;
			}
		if (DebounceCounter > dbnc){
			return true;
		}
		else {
			return false;
		}
				}
}