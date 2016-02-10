from ftplib import FTP
import sys, os, time

print('*************************************************************')
print('*****         FRC 1736 Robot Log File Snagger           *****')
print('*************************************************************')


RIO_ADDRESS = 'roboRIO-1736-FRC.local'
RIO_LOG_FPATH = '/media/sda1/data_captures_2016/'
LOCAL_PATH = 'C:\\RoboRIO_data_captures\\'
LOG_LOGS_DIR = '.\\file_snagger\\logs\\'

def log_log_result(fsnag_list):
    if not os.path.isdir(LOG_LOGS_DIR):
        os.mkdir(LOG_LOGS_DIR)
    
    if(fsnag_list == None):
        fname = os.path.join(LOG_LOGS_DIR, "FAILED_"+time.strftime("%Y%m%d-%H%M%S")+"_log.txt")
        fcontents = "ERROR! Snag failed! No logs grabbed!"
    else:
        fname = os.path.join(LOG_LOGS_DIR, "SNAGGED_"+time.strftime("%Y%m%d-%H%M%S")+"_log.txt")
        fcontents = ["Files were snagged:\n\n"]
        fcontents.extend([x + " \n" for x in fsnag_list])
        fcontents = "".join(fcontents)
        
    tempf = open(fname, "w")
    tempf.write(fcontents)
    tempf.close()
    return

# Attempt an FTP connection. Bomb out if we can't.
print('Attempting to connect to roboRIO on ftp://' + RIO_ADDRESS + " ...")
try:
    ftp = FTP(RIO_ADDRESS)
    ftp.login()
except:
    print('Error - could not connect to roboRIO! Files not snagged!')
    log_log_result(None)
    sys.exit(-1)
    
# FTP Connected! Change to correct working proper working directory
# and snag the list of files
ftp.cwd(RIO_LOG_FPATH)
filenames = ftp.nlst() # get filenames within the directory

# Inform user what files were found
print("Found "+ str(len(filenames)) +" Files.")

#make local dir if doesn't exist yet.
if not os.path.isdir(LOCAL_PATH):
    os.mkdir(LOCAL_PATH)

# Copy each file over
i = 1
for filename in filenames:
    print("Copying "+ filename + " (" + str(i) + "/" + str(len(filenames)) + ") ")
    local_filename = os.path.join(LOCAL_PATH, filename)
    file = open(local_filename, 'wb')
    ftp.retrbinary('RETR '+ filename, file.write)
    file.close()
    i = i+1
    
#We're Done!
ftp.quit()
log_log_result(filenames)
sys.exit(0)