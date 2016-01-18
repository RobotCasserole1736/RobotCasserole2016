package org.usfirst.frc.team1736.robot;

import java.util.Arrays;

public class BatteryParamEstimator {

	double VocEst = 13;
	double ESREst = 0.012;
	boolean confident = false;
	double min_spread_thresh = 7.0;
	double prev_best_spread = 0;
	double prev_best_esr = ESREst;
	
	int lms_window_length;
	double[] circ_buf_SysCurDraw_A;
	double[] circ_buf_SysVoltage_V;
	int index;
	
	
	public BatteryParamEstimator(int length){
		lms_window_length = length;
		index = 0;
		circ_buf_SysCurDraw_A = new double[lms_window_length];
		circ_buf_SysVoltage_V = new double[lms_window_length];
		Arrays.fill(circ_buf_SysCurDraw_A, 3.0);
		Arrays.fill(circ_buf_SysVoltage_V, 13.0);
		
	}
	
	public void setConfidenceThresh(double Thresh_A){
		min_spread_thresh = Thresh_A;
	}
	
	public void updateEstimate(double measSysVoltage_V, double measSysCurrent_A){
		
		//Update buffers with new inputs
		circ_buf_SysCurDraw_A[index] = measSysCurrent_A;
		circ_buf_SysVoltage_V[index] = measSysVoltage_V;
		index = (index + 1)%lms_window_length;
		
		//Perform Least Mean Squares estimation utilizing algorithm
		// outlined at http://faculty.cs.niu.edu/~hutchins/csci230/best-fit.htm
		double sumV = findSum(circ_buf_SysVoltage_V);
		double sumI = findSum(circ_buf_SysCurDraw_A);
		double sumIV = findDotProd(circ_buf_SysCurDraw_A,circ_buf_SysVoltage_V);
		double sumI2 = findDotProd(circ_buf_SysCurDraw_A,circ_buf_SysCurDraw_A);
		double meanV = sumV/lms_window_length;
		double meanI = sumI/lms_window_length;
		
		ESREst = - (sumIV - sumI * meanV)/(sumI2 - sumI * meanI);
		
		//Calculate the spread of the system current drawn
		//The Standard Deviation of the input windodw of points is used.
		double spread_I = findStdDev(circ_buf_SysCurDraw_A);
		
		//If the spread is above the preset threshold, we will be confident for this window
		if(spread_I > min_spread_thresh){
			confident = true;
			//Additionally, if this is the best spread we've seen so far, 
			//record the spread and ESR values for future use
			if(spread_I > prev_best_spread){
				prev_best_spread = spread_I;
				prev_best_esr = ESREst;
			}
		} else { //If the spread is too small, we're not confident, and reset previous best spread.
			confident = false;
			prev_best_spread = 0;
		}
		
		//If we weren't confident in that ESR we just calculated, pick the
		//last known best value instead.
		if(!confident){
			ESREst = prev_best_esr;
		}
		
		//From the ESR, calculate the open-circuit voltage
		VocEst = meanV + ESREst * meanI;
		
		return; //nothing to return, params are gotten with other "getter" functions
		
	}
	
	public double getEstESR(){
		return ESREst;
	}
	
	public double getEstVoc(){
		return VocEst;
	}
	
	public boolean getConfidence(){
		return confident;
	}
	
	public double getEstVsys(double Idraw_A){
		return VocEst - Idraw_A * ESREst;
	}
	
	
	
	private double findSum(double[] input){
		double sum = 0;
		for(double i : input){
			sum += i;
		}
		return sum;
	}
	
	private double findDotProd(double[] A, double[] B){
		double sum = 0;
		if(A.length != B.length){
			System.out.println("Error - A and B vectors are not the same length! Cannot compute dot product.");
			return Double.NaN;
		}
		
		for(int i = 0; i < A.length ; i++ ){
			sum += A[i] * B[i];
		}
		
		return sum;
	}
	
	private double findStdDev(double[] input){
		double avg_input = findSum(input)/input.length;
		double sum = 0;
		
		for(int i = 0; i < input.length; i++){
			sum += Math.pow((input[i] - avg_input), 2);
		}
		return Math.sqrt(sum/input.length);
	}
}
