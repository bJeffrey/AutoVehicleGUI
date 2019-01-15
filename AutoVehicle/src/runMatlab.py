# -*- coding: utf-8 -*-
"""
Created on Fri Oct 19 12:53:19 2018

@author: Workstation
"""

#x = 5
#print (x)

#def myMatlabFunction():
#    eng.isprime()

import matlab.engine
eng = matlab.engine.start_matlab()

for i in range (0,100):
    x = eng.rand()
    print(x)   


tf = eng.isprime(37)
print(tf)

xMatrix = eng.rand(3)
print(xMatrix)

#myMatlabFunction()