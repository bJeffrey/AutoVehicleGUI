import random
import sys

def main():
    positions = []
    
    while True:
        x = sys.argv[1]
        y = sys.argv[2]
        time = str(random.randint(1,12))
        time = time + ":" + str(random.randint(0,59))
        time = time + ":" + str(random.randint(0,59))
        position = [x, y, time]
        print (position)

    #while True:
        #x = str(round(random.uniform(0.0, 99.9), 3))
        #y = str(round(random.uniform(0.0, 99.9), 3))
        #time = str(random.randint(1,12))
        #time = time + ":" + str(random.randint(0,59))
        #time = time + ":" + str(random.randint(0,59))
        #position = [x, y, time]
        #print (position)
    
if __name__ == "__main__":
    main()