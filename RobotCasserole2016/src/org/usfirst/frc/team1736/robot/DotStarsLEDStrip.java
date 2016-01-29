/**
 * 
 */
package org.usfirst.frc.team1736.robot;

import java.util.Arrays;

import edu.wpi.first.wpilibj.SPI;

/**
 * @author gerthcm
 *
 */
public class DotStarsLEDStrip {
	//Datasheet - https://www.adafruit.com/datasheets/APA102.pdf
	
	//Constant values for fixed things in the serial data stream
	static byte[] startFrame = {0x00, 0x00, 0x00, 0x00};
	static byte[] endFrame = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF}; //Java is stupid and doesn't like unsigned things. 
	static byte globalBrighness = (byte)0xFF;
	
	//Offsets within the stream
	static int blueOffset = 1;
	static int greenOffset = 2;
	static int redOffset = 3;	
	static int bytesPerLED = 4;
	static int led0Offset = startFrame.length;
	static int endFrameOffset; //dependent on number of LED's

	//SPI coms object from FRC
	SPI spi;
	static final int SPI_CLK_RATE = 512000; //Total guess at max clock rate - 512KHz is called out in an example in the datasheet, so I used that
	
	//Color Buffer - all bytes to send out from the 
	byte[] ledBuffer;
	
	//byte restrictions
	int ledMaxVal = 255;
	
	//State variables
	boolean newBuffer; //true when the ledBuffer has been updated since the last time it was written to the LEDs
	
	/**
	 * Constructor for led strip class
	 * @param numLEDs - number of LED's in the total strip.
	 */
	DotStarsLEDStrip(int numLEDs){
		//Number of bytes in color buffer needed - each LED has 4 bytes (1 brightness, then 1 for RGB each),
		// plus the start and end frame.
		int num_bytes_for_strip = 4*numLEDs + startFrame.length + endFrame.length;
		endFrameOffset = 4*numLEDs + startFrame.length;
		
		//Initialize color buffer
		ledBuffer = new byte[num_bytes_for_strip];
		
		//Write in the start/end buffers
		for(int i = 0; i < startFrame.length; i++)
			ledBuffer[i] = startFrame[i];
		for(int i = 0; i < endFrame.length; i++)
			ledBuffer[i+endFrameOffset] = endFrame[i];
		
		//mark buffer as not-yet-written-to-the-LEDs
		newBuffer = true;
		
		//Initialize SPI coms on the onboard, chip-select 0. No chip select used, though.
		spi = new SPI(SPI.Port.kOnboardCS0);
		spi.setMSBFirst();
		spi.setClockActiveLow();
		spi.setClockRate(SPI_CLK_RATE); 
		spi.setSampleDataOnRising();
		
	}
	
	
	/**
	 * Send the current ledBuffer to the string IF it needs to be sent
	 * @return 0 on successful write, -1 on failure
	 */
	public int updateColors(){
		int ret_val = 0;
		
		//If we need to write something, attempt to put it on the SPI port
		if(newBuffer){
			ret_val =  spi.write(ledBuffer, ledBuffer.length);
		}
		
		//If we were successful in writing, mark the buffer as written to the LEDs 
		if(ret_val == 0){
			newBuffer = false;
		}

		return ret_val;
	}
	
	
	/**
	 * Clears all contents in the color buffer. This turns off all LED's. Be sure to call the updateColors() class 
	 * some time after this one to actually send the commanded colors to the actual strip.
	 */
	
	public void clearColorBuffer(){
		Arrays.fill(ledBuffer, (byte)0x00);
		//Write in the start/end buffers
		for(int i = 0; i < startFrame.length; i++)
			ledBuffer[i] = startFrame[i];
		for(int i = 0; i < endFrame.length; i++)
			ledBuffer[i+endFrameOffset] = endFrame[i];
		//Mark the buffer as updated
		newBuffer = true;
		//we're done!
		return;
	}
	
	
	/**
	 * sets a particular LED in the string to a certain color
	 * @param index - index in the LED to set. 0 is the furthest from the roboRIO, N is the closest.
	 * @param r - red value for the color. Provide as a double in the range of 0 (off) to 1 (full on)
	 * @param g - green value for the color. Provide as a double in the range of 0 (off) to 1 (full on)
	 * @param b - blue value for the color. Provide as a double in the range of 0 (off) to 1 (full on)
	 */
	
	public void setLEDColor(int index, double r, double g, double b){
		
		ledBuffer[index*bytesPerLED + startFrame.length + blueOffset] = convDoubletoByte(b);
		ledBuffer[index*bytesPerLED + startFrame.length + greenOffset] = convDoubletoByte(g);
		ledBuffer[index*bytesPerLED + startFrame.length + redOffset] = convDoubletoByte(r);
		//Mark the buffer as updated
		newBuffer = true;
		//we're done!
		return;
	}
	
	
	/**
	 * convert a double in the range 0-1 to a byte of value 0x00 to 0xFF. This normalizes the full range of 
	 * the LED brightness to the 0-1 range, hiding the implementation from the users.
	 * @param in
	 * @return
	 */
	private byte convDoubletoByte(double in){
		//Constrain the input to the defined [0,1] input range
		in = Math.min(Math.max(in, 0.0), 1.0);
		//Scale and round
		in = Math.round(in * 255.0);
		//Stupid offsetting b/c java doesn't support unsigned operations
		//This is 2's complement sign conversion. If you don't know what that
		//means, please don't touch this logic.
		if(in > 126.0)
			in = in - (ledMaxVal + 1);
		return (byte)in;
	}
	
	
	
	
	
	

}
