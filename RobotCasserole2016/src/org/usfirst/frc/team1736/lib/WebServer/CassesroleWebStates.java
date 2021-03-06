package org.usfirst.frc.team1736.lib.WebServer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.simple.JSONObject;
import org.usfirst.frc.team1736.lib.Calibration.CalWrangler;
/**
 * DESCRIPTION:
 * <br>
 * State View webpage definition class. Allows the user to send a variable set of state variables to the webpage.
 * The intent is a slower update rate in exchange for displaying large amounts of data. Any and all internal variables
 * in the software can be broadcast to the webpage.
 * <br>
 * ASSUMPTIONS:
 * <br>
 * Be sure the casserole webserver is started at some point, otherwise the webpage won't be displayed. Note that states can only be added (even at runtime), but never removed. At least not yet...  
 * <br>
 * USAGE:    
 * <ol>   
 * <li>Instantiate class</li> 
 * <li>On init, call setCalWrangler with the calibration wrangler, if needed.</li> 
 * <li>During runtime, call the put* methods to assign a new state value to be displayed.</li>    
 * </ol>
 * 
 *
 */
public class CassesroleWebStates {
	/** The set of objects which are broadcast. Must be volatile to ensure atomic accesses */
	static volatile Hashtable<String, JSONObject> data_array_elements = new Hashtable<String, JSONObject>();
	static volatile List<String> ordered_state_name_list = new ArrayList<String>(); //Used to help preserve the order the user creates the state displays in, since the hash table destroys this info
	
	/** 
	 * Put a new state to the web interface, or update an existing one with the same name 
	 * @param name Name for the state to display.
	 * @param value Double Floating-point value to display
	 */
	public static void putDouble(String name, double value){
		putGeneric(name, Double.toString(value));
	}
	
	/** 
	 * Put a new state to the web interface, or update an existing one with the same name 
	 * @param name Name for the state to display.
	 * @param value Boolean value to display
	 */
	public static void putBoolean(String name, boolean value){
		putGeneric(name, Boolean.toString(value));
	}
	
	/** 
	 * Put a new state to the web interface, or update an existing one with the same name 
	 * @param name Name for the state to display.
	 * @param value Integer value to display
	 */
	public static void putInteger(String name, int value){
		putGeneric(name, Integer.toString(value));
	}
	
	/** 
	 * Put a new state to the web interface, or update an existing one with the same name 
	 * @param name Name for the state to display.
	 * @param value String value to display
	 */
	public static void putString(String name, String value){
		putGeneric(name, value);	
	}
	
	private static void putGeneric(String name, String value){
		boolean is_new = true;
		
		if(data_array_elements.containsKey(name)){
			data_array_elements.get(name).put("value", value);

		} else {
			JSONObject new_obj = new JSONObject();
			new_obj.put("name", name);
			new_obj.put("value", value);
			data_array_elements.put(name, new_obj);
			ordered_state_name_list.add(name);
		}

	}

}
