package org.usfirst.frc.team1736.robot;

import edu.wpi.first.wpilibj.CANTalon;

public class DrawbridgeArmControls {
	CANTalon  RMotorController;
int DrawbridgeArmMotor_Channel = 2;

public DrawbridgeArmControls() {
	RMotorController = new CANTalon(DrawbridgeArmMotor_Channel );
}
public void	periodUptade(double armMotorCommand , boolean assistCommand){
	RMotorController.set(armMotorCommand);
		if(assistCommand){
		Pneumatics.extendDrawbridgeArmCylinder();
		RMotorController.set(0);
	}
	else{
		Pneumatics.retractDrawbridgeArmCylinder();
	}
	
}
	 
}
