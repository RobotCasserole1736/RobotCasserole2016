package org.usfirst.frc.team1736.robot;

import java.util.Random;

public class LEDSequencer {
	
	//Params for LED configurations
	private final int NUM_LEDS_R = 19;
	private final int NUM_LEDS_L = 19;
	private final int NUM_LEDS_TOTAL = NUM_LEDS_R + NUM_LEDS_L;
	
	//Effect Params
	private final double PULSE_FREQ_HZ = 0.2;
	
	private final int STRIPE_WIDTH = 5;
	
	private final double TWINKLE_FREQ1_HZ = 0.125;
	private final double TWINKLE_FREQ2_HZ = 0.131;
	private final double TWINKLE_FREQ3_HZ = 0.1;
	private final double TWINKLE_FREQ4_HZ = 0.09;
	private final double TWINKLE_DEPTH1 = 0.5;
	private final double TWINKLE_DEPTH2 = 0.4;
	private final double TWINKLE_DEPTH3 = 0.3;
	private final double TWINKLE_DEPTH4 = 0.5;
	private final int NUM_TWINKLES = 4; //at this point, "Twinkle" no longer looks like a word to this developer.
	
	//Color Defs
	private final double CASSEROLE_RED_R = 1.0;
	private final double CASSEROLE_RED_G = 0.00;
	private final double CASSEROLE_RED_B = 0.00;
	
	private final double CASSEROLE_WHITE_R = 1.0;
	private final double CASSEROLE_WHITE_G = 1.0;
	private final double CASSEROLE_WHITE_B = 0.98;
	
	//Counters
	private int callCounter;
	
	//Randomizers
	private Random rn;
	
	
	DotStarsLEDStrip ledStrips;
	
	LEDSequencer(){	
		ledStrips = new DotStarsLEDStrip(NUM_LEDS_TOTAL);
		rn = new Random();
		callCounter = 0;
	}
	
	void sequencerPeriodic(LEDPatterns cur_pattern){
		int i; //internal iteration index
		
		double redIntensity;
		double whiteIntensity;
		double rval, gval, bval;
		
		switch(cur_pattern){
			
		
			//Red is the best color.
			case SOLID_RED:
				for(i = 0; i < NUM_LEDS_TOTAL; i++)
					ledStrips.setLEDColor(i, CASSEROLE_RED_R, CASSEROLE_RED_G, CASSEROLE_RED_B);
				break;
				
			//Darkness covers the earth.
			case OFF:
				for(i = 0; i < NUM_LEDS_TOTAL; i++)
					ledStrips.setLEDColor(i, 0, 0, 0);
				break;
				
			//RED ALERT! RED ALERT!
			case PULSE_RED:
				double intensity = (0.75 + 0.25*Math.sin((double)callCounter*0.02*2*Math.PI*PULSE_FREQ_HZ));
				for(i = 0; i < NUM_LEDS_TOTAL; i++)
					ledStrips.setLEDColor(i, CASSEROLE_RED_R*intensity, CASSEROLE_RED_G*intensity, CASSEROLE_RED_B*intensity);
				break;
				
			//Standard casserole banner
			case STRIPES_FWD:
				for(i = 0; i < NUM_LEDS_R; i++){
					if((i % (2*STRIPE_WIDTH)) < STRIPE_WIDTH){
						ledStrips.setLEDColor(i, CASSEROLE_RED_R, CASSEROLE_RED_G, CASSEROLE_RED_B);
						ledStrips.setLEDColor(NUM_LEDS_TOTAL-i-1, CASSEROLE_RED_R, CASSEROLE_RED_G, CASSEROLE_RED_B);
					}
					else{
						ledStrips.setLEDColor(i, CASSEROLE_WHITE_R, CASSEROLE_WHITE_G, CASSEROLE_WHITE_B);
						ledStrips.setLEDColor(NUM_LEDS_TOTAL-i-1, CASSEROLE_WHITE_R, CASSEROLE_WHITE_G, CASSEROLE_WHITE_B);
					}
				}
				break;
			
			//Standard casserole banner with swapped red/white stripes
			case STRIPES_REV:
				for(i = 0; i < NUM_LEDS_R; i++){
					if((i % (2*STRIPE_WIDTH)) < STRIPE_WIDTH){
						ledStrips.setLEDColor(i, CASSEROLE_WHITE_R, CASSEROLE_WHITE_G, CASSEROLE_WHITE_B);
						ledStrips.setLEDColor(NUM_LEDS_TOTAL-(i)-1, CASSEROLE_WHITE_R, CASSEROLE_WHITE_G, CASSEROLE_WHITE_B);
					}
					else{
						ledStrips.setLEDColor(i, CASSEROLE_RED_R, CASSEROLE_RED_G, CASSEROLE_RED_B);
						ledStrips.setLEDColor(NUM_LEDS_TOTAL-(i)-1, CASSEROLE_RED_R, CASSEROLE_RED_G, CASSEROLE_RED_B);
					}
				}
				break;
			
			//Classy refinement.
			case TWINKLE_WEIRD:
				double intensity1 = ((1-TWINKLE_DEPTH1) + TWINKLE_DEPTH1*Math.sin((double)callCounter*0.02*2*Math.PI*TWINKLE_FREQ1_HZ));
				double intensity2 = ((1-TWINKLE_DEPTH2) + TWINKLE_DEPTH2*Math.sin((double)callCounter*0.02*2*Math.PI*TWINKLE_FREQ2_HZ));
				double intensity3 = ((1-TWINKLE_DEPTH3) + TWINKLE_DEPTH3*Math.sin((double)callCounter*0.02*2*Math.PI*TWINKLE_FREQ3_HZ));
				double intensity4 = ((1-TWINKLE_DEPTH4) + TWINKLE_DEPTH4*Math.sin((double)callCounter*0.02*2*Math.PI*TWINKLE_FREQ4_HZ));
				for(i = 0; i < NUM_LEDS_TOTAL; i++){
					if(i%2 == 1){ //only twinkle ever other LED
						ledStrips.setLEDColor(i, CASSEROLE_WHITE_R, CASSEROLE_WHITE_G, CASSEROLE_WHITE_B);
					}
					else{
						switch((i/2)%NUM_TWINKLES){
						case 0:
							ledStrips.setLEDColor(i, CASSEROLE_WHITE_R*intensity1, CASSEROLE_WHITE_G*intensity1, CASSEROLE_WHITE_B*intensity1);
							break;
						case 1:
							ledStrips.setLEDColor(i, CASSEROLE_WHITE_R*intensity2, CASSEROLE_WHITE_G*intensity2, CASSEROLE_WHITE_B*intensity2);
							break;
						case 2:
							ledStrips.setLEDColor(i, CASSEROLE_WHITE_R*intensity3, CASSEROLE_WHITE_G*intensity3, CASSEROLE_WHITE_B*intensity3);
							break;
						case 3:
							ledStrips.setLEDColor(i, CASSEROLE_WHITE_R*intensity4, CASSEROLE_WHITE_G*intensity4, CASSEROLE_WHITE_B*intensity4);
							break;
						default:
							ledStrips.setLEDColor(i, 0, 0, 0);
							break;
							
						
						}
					}
				}
				break;
				
			//PUMP UP THE JAMS.
			case VOLUME_CTRL1:

				for(i = 0; i < NUM_LEDS_R; i++){
					if( ((double)i/(double)NUM_LEDS_R) < ledStrips.getAudioLevel() ){
						ledStrips.setLEDColor(i, CASSEROLE_WHITE_R, CASSEROLE_WHITE_G, CASSEROLE_WHITE_B);
						ledStrips.setLEDColor(NUM_LEDS_TOTAL-(i)-1, CASSEROLE_WHITE_R, CASSEROLE_WHITE_G, CASSEROLE_WHITE_B);
					}
					else{
						ledStrips.setLEDColor(i, CASSEROLE_RED_R, CASSEROLE_RED_G, CASSEROLE_RED_B);
						ledStrips.setLEDColor(NUM_LEDS_TOTAL-(i)-1, CASSEROLE_RED_R, CASSEROLE_RED_G, CASSEROLE_RED_B);
					}
				}
				break;
				
			//Smooth operator.
			case GRADIENT:
				for(i = 0; i < NUM_LEDS_R; i++){
					redIntensity = Math.pow(((double)i)/((double)NUM_LEDS_R-1.0),3);
					whiteIntensity = Math.pow(((double)(NUM_LEDS_R-i))/((double)NUM_LEDS_R-1.0),3);
					rval = (CASSEROLE_WHITE_R*whiteIntensity + CASSEROLE_RED_R*redIntensity)/2; //Assumes linear something something...
					gval = (CASSEROLE_WHITE_G*whiteIntensity + CASSEROLE_RED_G*redIntensity)/2;
					bval = (CASSEROLE_WHITE_B*whiteIntensity + CASSEROLE_RED_B*redIntensity)/2;
					ledStrips.setLEDColor(i, rval, gval, bval);
					ledStrips.setLEDColor(NUM_LEDS_TOTAL-(i)-1, rval, gval, bval);
				}
				break;
				
			//We might as well be giving birth to a unicorn.
			case RAINBOW:
				int rand;
				for(i = 0; i < NUM_LEDS_TOTAL; i++){
					rand = rn.nextInt(10)+1; //random int between 1 and 10
					switch(rand){
						case 1: //Ten vibrant colors for eye-catchiness
							ledStrips.setLEDColor(i, 1, 0, 0);
							break;
						case 2:
							ledStrips.setLEDColor(i, 0, 1, 0);
							break;
						case 3:
							ledStrips.setLEDColor(i, 0, 0, 1);
							break;
						case 4:
							ledStrips.setLEDColor(i, 1, 1, 0);
							break;
						case 5:
							ledStrips.setLEDColor(i, 1, 0, 1);
							break;
						case 6:
							ledStrips.setLEDColor(i, 0, 1, 1);
							break;
						case 7:
							ledStrips.setLEDColor(i, 1, 0, 0.5);
							break;
						case 8:
							ledStrips.setLEDColor(i, 0.5, 0, 1);
							break;
						case 9:
							ledStrips.setLEDColor(i, 0, 0.5, 1);
							break;
						case 10:
							ledStrips.setLEDColor(i, 0, 1, 0.5);
							break;
						default:
							ledStrips.setLEDColor(i, 0, 0, 0);
							break;		
					}
				}
				break;
				
			//If we get here, something bad has happened and software team is having a bad day.
			default:
				for(i = 0; i < NUM_LEDS_TOTAL; i++)
					ledStrips.setLEDColor(i, 0, 0, 0);
				System.out.println("AAAAAAAAUUUUGGGHHHGHGHGHHHHblargabarbhbsd!!!!!! :( ");
				break;
				
		}
		
		callCounter++;
		System.out.println(ledStrips.getAudioLevel() + "  |  " + ledStrips.audioIn.getVoltage());
	}
	

}
