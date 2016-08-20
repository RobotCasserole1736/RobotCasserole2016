package org.usfirst.frc.team1736.lib.AutoSequencer;

import java.util.ArrayList;

public abstract class AutoEvent {

	/** Total number of times the update method has been called. Incremented AFTER each update. */ 
	public long localUpdateCount;
	
	/**FPGA timestamp at which this event was triggered */
	public double triggerTime;
	
	/** True when this event is active, false if not. */
	public boolean isRunning;
	
	public ArrayList<AutoEvent> childEvents;
	
	AutoEvent(){
		triggerTime = 0;
		localUpdateCount = 0;
		isRunning = false;
		childEvents = new ArrayList<AutoEvent>(0);
		userInit();
	}
	
	/** Assign a child event to this event. Whenever this event is active, the child event will be checked for a trigger condition 
	 *   and will be updated in parallel with its parent event.
	 */
	
	public void addChildEvent(AutoEvent event_in){
		childEvents.add(event_in);
	}
	
	public void forceStopAllChildren(){
		for(AutoEvent child : childEvents){
			if(child.isRunning){
				child.userForceStop();
				child.isRunning = false;
			}
		}
	}
	
	void update(){
		userUpdate();
		localUpdateCount++;
	}
	
	/** Perform any actions needed to initalize this event */
	abstract void userInit();
	
	/** Perform all actions needed during periodic update for this event */
	abstract void userUpdate();
	
	/** Perform all actions needed to restore robot to a safe state if the event is
	 *  prematurely stopped.
	 */
	abstract void userForceStop();
	
	/**
	 * Determine if this event needs updating. The first time isTriggered() returns
	 * true, the AutoSequencer begins updating this event.
	 * @return True if the trigger condition has been hit, false otherwise
	 */
	abstract boolean isTriggered();
	
	
	/**
	 * Determine if this event has completed. Might be that total time has expired
	 * @return True if the event no longer needs updating, false otherwise.
	 */
	abstract boolean isDone();
	
}
