package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class CIMCurrentEstimator {
	
	PowerDistributionPanel pdp;
	
	int numMotorsInSystem; //Number of motors driving this system
	double ESR = 0.1062; //Equivalent series resistance of CIM motor in Ohms
	double Ki = 0.1263; //Torque constant in CIM motor
	double motorEncoderRatio; //Ratio in speeds between encoder measurement and actual motor speed. Numbers bigger than 1 indicate motor rotates faster than encoder 
	double contVDrop;
	
	/**
	 * init - Sets up the current estimator with the system parameters.
	 * Input - 
     *      numMotors = Integer number of motors in the gearbox system. Usually 2 or 3, depending on your setup.
     *      motorEncRatio = ratio of motor gear teeth divided by encoder gear teeth. A number smaller than one means the motor spins slower than the encoder.
     *      controllerVDrop_V = voltage drop induced by the motor controller, in V. 
	 */
	public CIMCurrentEstimator(int numMotors, double motorEncRatio, double controllerVDrop_V, PowerDistributionPanel pdp) {
		this.numMotorsInSystem = numMotors;
		this.motorEncoderRatio = motorEncRatio;
		this.contVDrop = controllerVDrop_V;
		this.pdp = pdp;
	}
	
	/**
	 * getCurrentEstimate - Determines a unique file name, and opens a file in the data captures directory
	 *        and writes the initial lines to it. 
	 * Input - 
     *      numMotors = Integer number of motors in the gearbox system. Usually 2 or 3, depending on your setup.
     *      motorEncRatio = ratio of motor gear teeth divided by encoder gear teeth. A number smaller than one means the motor spins slower than the encoder.
	 */
	public double getCurrentEstimate(double encoderSpeed_radpersec, double motorCommand) {
		return Math.abs((double)numMotorsInSystem*((pdp.getVoltage()-contVDrop)*motorCommand-Ki*encoderSpeed_radpersec*motorEncoderRatio)/ESR);
	}

}
