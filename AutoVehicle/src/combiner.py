import socket
 
HOST = "localhost"
PORT = 9875
 
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((HOST, PORT))

loop = True
i = 1
while(i <= 10):
    #msg = raw_input("Enter a message: ")
    #msg = input("Enter a message: ")
    msg = "position:300:400:0"
 
    sock.sendall((msg+'\n').encode('utf-8'))
    data = sock.recv(1024)


    print ("== Server responded. ==")

    if(data == "Clear IMU"):
        pass
    if(msg == "exit"):
        loop = False
    i = i + 1

 
sock.close()
