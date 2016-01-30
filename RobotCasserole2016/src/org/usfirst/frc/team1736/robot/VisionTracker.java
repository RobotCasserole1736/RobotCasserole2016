package org.usfirst.frc.team1736.robot;
import java.io.IOException;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.lang.Math

public class VisionTracker {
    private VisionTarget[]  sorted_tgts;
    private VisionTarget[]  prev_sorted_tgts;

    //Class Constructor
    public VisionTracker(){
        sorted_tgts = new VisionTarget[3];
        prev_sorted_tgts = new VisionTarget[3];

        //Set all elements of both arrays to Null

    }
	
	public void readTargets(){
		  //Copy sorted_tgts into prev_sorted_tgts for safekeeping

        for(all obj identified by GRIP){
            //make a new array to hold all the Area errors

            //Calculate error in area for all objects identified by GRIP
        	ratio = (obj.height/obj.width)  ;
            Object error = Math.abs(obj.area - obj.height*obj.width*RATIO);
                //RATIO must be replaced by ratio of filled in area to total area on target

        }

        //create new array valid_objs with the three lowest
        //errors. If less than three objects are identified,
        //the remaining entries should be Null.

        for(non-null entries in prev_sorted_tgts){
            //Find index of element in valid_objs which is closest 
            //in x/y center distance to the element from prev_sorted_tgts
            //and store it into sorted_tgts at the same index
        }

        //put remaining entries from valid_objs into sorted_tgts array
        //in numerical order.

	}
	
	public void beginTracking(){
		
	}
	
	public void trackingIterate(){
		
	}

}
