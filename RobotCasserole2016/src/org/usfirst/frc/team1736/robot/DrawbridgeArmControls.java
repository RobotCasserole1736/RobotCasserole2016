package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.CANTalon;

public class DrawbridgeArmControls {
	CANTalon  RMotorController;
int DrawbridgeArmMotor_Channel = 2;


public DrawbridgeArmControls() {
	RMotorController = new CANTalon(DrawbridgeArmMotor_Channel );
}

}
