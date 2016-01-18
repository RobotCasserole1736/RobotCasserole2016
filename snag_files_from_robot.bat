@setlocal enableextensions enabledelayedexpansion
@echo off
set rioPath=roboRIO-1736.local
set rioCaptureFilePath=U\data_captures_2016\
set output_path=C:\RobotLogs2016\

echo ****************************************************************
echo *** File Snag Tool - FRC1736 Robot Casserole - 2016
echo ****************************************************************

echo Starting file grab
echo Connecting to roboRIO on ftp:\\!rioPath!...


::Test pinging first to ensure the FTP _should_ proceed
set state=0
ping -n 1 !rioPath! >nul: 2>nul:

if errorlevel 1 (
echo ERROR Cannot ping RoboRIO
echo Make sure PC is connected to robot, and get someone from Programming Team ASAP
echo Sad Day :(
pause 
exit -1
)

echo Starting file transfer...

::Use xcopy to leverage the anonymous FTP enabled on the roboRIO. Copy to somewhere easy on the C drive.
:: Verify each file as it's copied, list out filenames copied, copy all files from source folder to destination
:: without prompting, use network copy mode, always prompt for overwrite
xcopy ftp:\\!rioPath!\!rioCaptureFilePath! !output_path! /v /l /i /z /-y
if errorlevel 0 echo New files were copied to !output_path!.

echo File Copy Complete! 
pause 
endlocal
