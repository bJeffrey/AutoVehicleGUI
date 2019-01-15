# -*- coding: utf-8 -*-
"""
Created on Mon Oct  1 13:34:58 2018
Program:        threadsTest.py
Author:         Jeffrey Noe
Description:    Demonstrates ability to run IMU, TDOA, and Trajectory threads
"""

import threading 
from newIMU import getIMUCoordinates
import classes
import matlab.engine
import random
import math
import time

################################################################################
# Function:         runIMU()
# Description:      Runs the IMU and adds the positions to the position list      
################################################################################
def runIMU(num): 
    count = 0
    while count < 100000:
        position = getIMUCoordinates(count)
        currentTime = getTime()#get the time since the program has started
        POSITIONSIMU.append(classes.Position(position[0], position[1], currentTime))
        print (position, currentTime, "IMU")
        count = count + 1

################################################################################
# Function:         runTDOA()
# Description:      Runs the TDOA functions and adds the positions to the position list
################################################################################  
def runTDOA(num, testString): 
    TDOAeng = matlab.engine.start_matlab()
    count = 0
    while count < 6000:
        x = float(250 + 250 * TDOAeng.cos(count / 40.0))
        y = float(250 + 250 * TDOAeng.cos(count / 30.0))
        currentTime = getTime()#get the time since the program has started
        POSITIONSTDOA.append(classes.Position(x, y, currentTime))
        print(x, y, currentTime, "TDOA")
        count = count + 1

################################################################################
# Function:         runTrajectory()
# Description:      Runs the trajectory calculation functions            
################################################################################
def runTrajectoryCalc(arrayExample):
    pass

################################################################################
# Function:         runCombiner()
# Description:      Runs the combiner functions            
################################################################################
def runCombiner(arrayExample):
    pass

################################################################################
# Function:         getTime()
# Description:      Gets the time since the program began running            
################################################################################
def getTime():
    return time.time() -PROGRAMSTARTTIME

################################################################################
# Function:         main()
# Description:      Creates and runs the threads for the positioning, trajectory,
#                   and directional threads
################################################################################
def main(): 
    arrayExample = [1, 2, 3]

    #Create global variables (all caps by convention)
    global POSITIONSTDOA
    global POSITIONSIMU
    global TIMES
    global PROGRAMSTARTTIME
    PROGRAMSTARTTIME = time.time()
    TIMES = []
    POSITIONSTDOA = []
    POSITIONSIMU = []
    
    t1 = threading.Thread(target=runIMU, args=(10,)) 
    t2 = threading.Thread(target=runTDOA, args=(9, "Hello")) 
    t3 = threading.Thread(target=runCombiner, args=(arrayExample,))
    t4 = threading.Thread(target=runTrajectoryCalc, args=(arrayExample,))    
    
    # starting thread 1 
    t1.start() 
    # starting thread 2 
    t2.start() 
    #starting thread 3
    t3.start()
    #starting thread 4
    t4.start()
    # wait until thread 1 is completely executed 
    t1.join() 
    # wait until thread 2 is completely executed 
    t2.join() 
    # wait until thread 3 is completely executed
    t3.join()
    # wait until thread 4 is completely executed
    t4.join()
    
  
    # all threads completely executed 
    print("Done!") 

if __name__ == "__main__":
    main()
