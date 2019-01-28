# -*- coding: utf-8 -*-
"""
Created on Mon Oct  1 14:03:57 2018

@author: Workstation
"""
import math
import random
#import sys

def getIMUCoordinates(count):
        x = float(250 + 250 * math.cos(count / 40.0))
        y = float(250 + 250 * math.cos(count / 30.0))
        noise = random.uniform(-1.0, 1.0)
        x = x + noise
        noise = random.uniform(-1.0, 1.0)
        y = y + noise
        position = [x, y]
        return position
    