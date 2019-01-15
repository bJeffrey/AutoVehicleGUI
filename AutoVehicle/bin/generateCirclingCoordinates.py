# -*- coding: utf-8 -*-
"""
Created on Fri Aug 17 15:37:56 2018

@author: Jeffrey Noe
"""
import math

def main():
    positions = []
    count = 0
    #start at coordinate (250, 250). Add or subtract 250 * any number in range [0, 1] (not just integers)
    while True:
        x = 5 + 5 * math.cos(count / 40.0)
        y = 5 + 5 * math.cos(count / 30.0)
        x = round(x, 2)
        y = round(y, 2)
        position = [x, y]
        print (position)
        if(count > 100000):
            count = 0
        count = count + 1
    
if __name__ == "__main__":
    main()