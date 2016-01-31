/**
 * 
 */
package org.usfirst.frc.team1736.robot;

/**
 * @author Chris Gerth
 * Enumeration for the states of the intake/laucher state machine
 */
public enum IntLncState {
	STOPPED_NO_BALL, INTAKE, EJECT, INTAKE_OVD, CARRY_BALL, 
	CARRY_BALL_OVD, RETRACT, WAIT_FOR_SPOOLUP, WAIT_FOR_LAUNCH,
	LAUNCH, WAIT_FOR_SPOOLDOWN
}
