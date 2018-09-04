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
        x = 250 + 250 * math.cos(count / 40.0)
        y = 250 + 250 * math.cos(count / 30.0)
        position = [x, y]
        print (position)
        if(count > 100000):
            count = 0
        count = count + 1
    
if __name__ == "__main__":
    main()