# -*- coding: utf-8 -*-
"""
Created on Mon Oct  1 16:11:53 2018
Program:        classes.py
Author:         Jeffrey Noe
Description:    contains the classes used for IMU, TDOA, and Combiner
@author: Workstation
"""

class Position:
    def __init__(self, myX, myY, myTime):
        self.x = myX
        self.y = myY
        self.time = myTime

