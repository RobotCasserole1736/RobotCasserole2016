@setlocal enableextensions enabledelayedexpansion
@echo off
set rioPath=10.17.36.2
set rioCaptureFilePath=//media//sda1//data_captures_2016//*.csv
set output_path_windows=C:\RobotLogs2016\
set output_path_linux=\RobotLogs2016\

echo ****************************************************************
echo *** File Snag Tool - FRC1736 Robot Casserole - 2016
echo ****************************************************************

echo Starting file grab
echo Connecting to roboRIO on !rioPath!...


::Test pinging first to ensure the FTP _should_ proceed
set state=0
ping -n 1 !rioPath! >nul: 2>nul:

if errorlevel 1 (
echo ERROR Cannot ping RoboRIO
echo Make sure PC is connected to robot, and get someone from Programming Team ASAP
echo Sad Day :(
pause 
goto EOF
)

::Assumption - git is installed. This seems valid since this script
:: is inside of a git repo.
mkdir !output_path_windows!
"C:\Program Files (x86)\Git\bin\scp.exe" -C -p -oStrictHostKeyChecking=no lvuser@!rioPath!:!rioCaptureFilePath! !output_path_linux!


echo File Copy Complete.
echo Files copied to local PC, in directory !output_path_windows!
pause 
endlocal
