package org.usfirst.frc.team1736.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.io.BufferedWriter;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;

import static java.lang.invoke.MethodType.*;

/**
 * CSV Logger Class - Provides an API for FRC 1736 Robot Casserole datalogging on the robot during runs
 * Will write lines into a CSV file with a unique name between calls to init() and close(). output_dir is 
 * hardcoded to point to a specific 2016 folder on a flash drive connected to the roboRIO. 
 * @author Chris Gerth, Nick Dunne
 *
 */
public class CsvLogger {
	
	static long log_write_index;
	static String log_name = null;
	static String output_dir = "/U/data_captures_2016/"; // USB drive is mounted to /U on roboRIO
	static BufferedWriter log_file = null;
	static boolean log_open = false;
	
	static Vector<String> dataFieldNames = new Vector<String>();
	static Vector<String> unitNames = new Vector<String>();
	static Vector<MethodHandle> methodHandles = new Vector<MethodHandle>();
	static Vector<Vector<Object>> mhReferenceObjects = new Vector<Vector<Object>>();
	static Vector<Boolean> isSimpleMethods = new Vector<Boolean>();
	static double lastLeftMotorCurrent = 0;
	static double lastRightMotorCurrent = 0;
	
	/**
	 * forceSync - Clears the buffer in memory and forces things to file. Generally a 
	 * good idea to use this as infrequently as possible (because it increases logging overhead),
	 * but definitely use it before the roboRIO might crash without a proper call to the close() method
	 * (ie, during brownout)
	 * @return Returns 0 on flush success or -1 on failure.
	 */	
	public static int forceSync(){
		if(log_open == false){
			System.out.println("Error - Log is not yet opened, cannot sync!");
			return -1;
		}
		try {
			log_file.flush();
		}
		//Catch ALL the errors!!!
		catch(IOException e){
			System.out.println("Error flushing IO stream file: " + e.getMessage());
			return -1;
		}
		
		return 0;
		
	}
	
	
	
	/**
	 * close - closes the log file and ensures everything is written to disk. 
	 * init() must be called again in order to write to the file.
	 * @return -1 on failure to close, 0 on success
	 */	
	public static int close(){
		
		if(log_open == false){
			System.out.println("Warning - Log is not yet opened, nothing to close.");
			return 0;
		}
		
		try {
			log_file.close();
			log_open = false;
		}
		//Catch ALL the errors!!!
		catch(IOException e){
			System.out.println("Error Closing Log File: " + e.getMessage());
			return -1;
		}
		return 0;
		
	}
	
	private static String getDateTimeString() {
		return new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
	}
	
	/**
	 * init - Determines a unique file name, and opens a file in the data captures directory
	 *        and writes the initial lines to it. 
	 * @return 0 on successful log open, -1 on failure
	 */
	public static int init() {
		
		if(log_open){
			System.out.println("Warning - log is already open!");
			return 0;
		}
		
		log_open = false;
		System.out.println("Initalizing Log file...");
		try {
			//Reset state variables
			log_write_index = 0;
			
			//Determine a unique file name
			log_name = output_dir + "log_" + getDateTimeString() + ".csv";
			
			//Open File
			FileWriter fstream = new FileWriter(log_name, true);
			log_file = new BufferedWriter(fstream);
			
			//Write user-defined header line
			for(String header_txt : dataFieldNames){
				log_file.write(header_txt + ", ");
			}
			//End of line
			log_file.write("\n");
			
			
			//Write user-defined units line
			for(String header_txt : unitNames){
				log_file.write(header_txt + ", ");
			}
			//End of line
			log_file.write("\n");
			
		}
		//Catch ALL the errors!!!
		catch(IOException e){
			System.out.println("Error initalizing log file: " + e.getMessage());
			return -1;
		}
		System.out.println("done!");
		log_open = true;
		return 0;
		
	}
	
	/**
	 * Logs data for all stored method handles.  Methods that are not considered "simple" should be 
	 * handled accordingly within this method.  This method should be called once per loop.
	 * @param forceSync set true if a forced write is desired (i.e. brownout conditions)
	 * @return 0 if log successful
	 */
	public static int logData(boolean forceSync)
	{
		if(!log_open){
			System.out.println("Error - Log is not yet opened, cannot write!");
			return -1;
		}
		
		if(forceSync)
			forceSync();
		
		try 
		{
			log_file.write(Double.toString(log_write_index) + ", ");
			for(int i = 0; i < methodHandles.size(); i++)
			{
				MethodHandle mh = methodHandles.get(i);
				String fieldName = dataFieldNames.get(i);
				Vector<Object> mhArgs = mhReferenceObjects.get(i);
				boolean isSimple = isSimpleMethods.get(i);
				if(isSimple)
				{
					log_file.write(getStandardLogData(mh, mhArgs) + ", ");
				}
				else
				{
					if(fieldName.equals("EstLeftDTCurrent"))
					{
						lastLeftMotorCurrent = getStandardLogData(mh, mhArgs);
						log_file.write(lastLeftMotorCurrent + ", ");
					}
					else if(fieldName.equals("EstRightDTCurrent"))
					{
						lastRightMotorCurrent = getStandardLogData(mh, mhArgs);
						log_file.write(lastRightMotorCurrent + ", ");
					}
					else if(fieldName.equals("EstVsys"))
					{
						//Set the parameter to the motor current.  
						//Should have been logged before this so values should be current
						mhArgs.set(1, lastRightMotorCurrent + lastLeftMotorCurrent + 5);
						log_file.write(getStandardLogData(mh, mhArgs) + ", ");
					}
					else if(fieldName.equals("RightDTVoltage"))
					{
						log_file.write((-1 * getStandardLogData(mh, mhArgs)) + ", ");
					}
					else if(fieldName.equals("LeftDTSpeed") || fieldName.equals("RightDTSpeed"))
					{
						//Report rate in RPM
						log_file.write((9.5492 * getStandardLogData(mh, mhArgs)) + ", ");
					}
					else
						System.out.println("Error: attempting to log unknown special field. Skipped " + fieldName);
				}
			}
			log_file.write("\n");
		}
		catch(Exception ex)
		{
			System.out.println("Error writing to log file: " + ex.getMessage());
			return -2;
		}
		
		return 0;
	}
	
	/**
	 * Add a field to be logged at each loop of the robot code.  A method handle will be created and stored.
	 * This method is for methods which return a data type of double.
	 * @param dataFieldName Name of the field/column in the output data.  Also used for determining what to do for "complex" methods
	 * @param unitName Name of the units for field/column in the output data.
	 * @param classRef Class where the method is held, such as Joystick.class for getRawButton()
	 * @param methodName Actual method name to be called for this field, such as "getRawButton"
	 * @param reference A reference to the object whose method will be called.  If static method, this should be null.
	 * @param isSimple True for fields that are just logged as-is, false for when further math/scaling/etc will be done after retrieving the data
	 * @param args Optional list of arguments that are passed to the method, such as 0 in getRawButton(0)
	 */
	public static void addLoggingFieldDouble(String dataFieldName, String unitName, Class<?> classRef, String methodName, 
			Object reference, boolean isSimple, Object... args)
	{
		MethodType methodType = methodType(double.class);
		for(Object arg : args)
			methodType = methodType.appendParameterTypes(arg.getClass());
		methodType = methodType.unwrap(); //assumes primitive wrappers should be primitives
		addLoggingField(methodType, dataFieldName, unitName, classRef, methodName, isSimple, reference, args);
	}
	
	/**
	 * Add a field to be logged at each loop of the robot code.  A method handle will be created and stored.
	 * This method is for methods which return a data type of boolean.
	 * @param dataFieldName Name of the field/column in the output data.  Also used for determining what to do for "complex" methods
	 * @param unitName Name of the units for field/column in the output data.
	 * @param classRef Class where the method is held, such as Joystick.class for getRawButton()
	 * @param methodName Actual method name to be called for this field, such as "getRawButton"
	 * @param reference A reference to the object whose method will be called.  If static method, this should be null.
	 * @param isSimple True for fields that are just logged as-is, false for when further math/scaling/etc will be done after retrieving the data
	 * @param args Optional list of arguments that are passed to the method, such as 0 in getRawButton(0)
	 */
	public static void addLoggingFieldBoolean(String dataFieldName, String unitName, Class<?> classRef, String methodName, 
			Object reference, boolean isSimple, Object... args)
	{
		MethodType methodType = methodType(boolean.class);
		for(Object arg : args)
			methodType = methodType.appendParameterTypes(arg.getClass());
		methodType = methodType.unwrap(); //assumes primitive wrappers should be primitives
		addLoggingField(methodType, dataFieldName, unitName, classRef, methodName, isSimple, reference, args);
	}
	
	/*
	 * Implementation of above convenience classes.  Creates method handle and stores relevant information
	 * in class level Vector objects.
	 */
	private static void addLoggingField(MethodType methodType, String dataFieldName, String unitName, Class<?> classRef, String methodName, 
			boolean isSimple, Object reference, Object... args)
	{
		if(log_open)
		{
			System.out.println("Error: cannot add logging field while log file is open");
			return;
		}
		for(int i = 0; i < dataFieldNames.size(); i++)
		{
			String fieldName = dataFieldNames.get(i);
			if(dataFieldName.equals(fieldName))
			{
				Vector<Object> mhArgs = new Vector<Object>();
				mhArgs.add(reference);
				for(Object arg : args)
					mhArgs.add(arg);
				mhReferenceObjects.set(i, mhArgs);
				System.out.println("Warning: log field already present. Reference updated");
				return;
			}
		}
		MethodHandle methodHandle = null;
		try {
			methodHandle = MethodHandles.lookup().findVirtual(classRef, methodName, methodType);
		} catch (NoSuchMethodException e) {
			System.out.println("Error: Could not add logging field " + dataFieldName + " (no such method)");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			try{
				methodHandle = MethodHandles.lookup().findStatic(classRef, methodName, methodType);
			}
			catch (Exception ex)
			{
				System.out.println("Error: Could not add logging field " + dataFieldName);
				ex.printStackTrace();
			}
		}
		dataFieldNames.add(dataFieldName);
		unitNames.add(unitName);
		methodHandles.add(methodHandle);
		Vector<Object> mhArgs = new Vector<Object>();
		if(reference != null) //will be null for static methods
			mhArgs.add(reference);
		for(Object arg : args)
			mhArgs.add(arg);
		mhReferenceObjects.add(mhArgs);
		isSimpleMethods.add(isSimple);
	}
	
	/***
	 * This is used for any non-special logging parameters (those that don't require extra math on the output)
	 * @param methodHandle A MethodHandle stored in the class level Vector
	 * @param args Arguments for the given Method Handle
	 * @return double value for double return types, 1 or 0 for boolean return types 
	 */
	private static double getStandardLogData(MethodHandle methodHandle, Vector<Object> args)
	{
		try {
			if(methodHandle.type().returnType() == double.class)
				return (double)methodHandle.invokeWithArguments(args);
			else if(methodHandle.type().returnType() == boolean.class)
				return ((boolean)methodHandle.invokeWithArguments(args))?1.0:0.0;
		} catch (Throwable e) {
			System.out.println("Error running method for data logging");
			e.printStackTrace();
		}
		return 0;
	}

}
