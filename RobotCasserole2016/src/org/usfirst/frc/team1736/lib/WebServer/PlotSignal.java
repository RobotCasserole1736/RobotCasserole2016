package org.usfirst.frc.team1736.lib.WebServer;

import java.util.LinkedList;
import java.util.Queue;

public class PlotSignal {

	String name;
	String display_name;
	String units;
	
	boolean acq_active;
	
	Queue<PlotSample> sample_queue;
	
	/**
	 * Class which describes one line on a plot
	 * @param name_in String of what to call the signal
	 * @param units_in units the signal is in.
	 */
	public PlotSignal(String name_in, String display_name_in, String units_in){
		display_name = display_name_in;
		name = name_in;
		units = units_in;
		
		acq_active = false;
		
		sample_queue = new LinkedList<PlotSample>();
	}
	
	/**
	 * Adds a new sample to the signal queue. It is intended that
	 * the controls code would call this once per loop to add a new
	 * datapoint to the real-time graph.
	 * @param time_in
	 * @param value_in
	 */
	public void addSample(double time_in, double value_in){
		if(acq_active){
			sample_queue.add(new PlotSample(time_in, value_in));
		}
	}
	
	/**
	 * Start acquiring data on this channel. Should be called before attempting to read info.
	 */
	public void startAcq(){
		acq_active = true;
	}
	/**
	 * Stop acquiring data on this channel. Should be called when data no longer needs to be transmitted.
	 */
	public void stopAcq(){
		acq_active = false;
	}
	
	/**
	 * Returns an array of all the samples currently in the queue, and then clears it.
	 * It is intended that the weberver would call this to transmit all available 
	 * data from previous iterations. This might return null if the control code
	 * has no new data.
	 */
	public PlotSample[] getAllSamples(){
		int size = sample_queue.size();
		PlotSample[] retval;
		if(size > 0){
			retval = new PlotSample[size];
			sample_queue.toArray(retval);
			sample_queue.clear();
		} else {
			retval = null;
		}
		return retval;
	}
	
	/**
	 * Discards all samples from the buffer
	 */
	public void clearBuffer(){
		sample_queue.clear();
	}
	
	/**
	 * @return The name of the signal
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return The User-friendly name of the signal
	 */
	public String getDisplayName(){
		return display_name;
	}
	
	/**
	 * @return The name of the units the signal is measured in.
	 */
	public String getUnits(){
		return units;
	}
}
