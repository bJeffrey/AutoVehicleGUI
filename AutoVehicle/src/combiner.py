import socket
import threading 
 
def runClient():
	HOST = "localhost"
	PORT = 9875

	sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	sock.connect((HOST, PORT))
	i = 1
	while (i <= 10):
		# msg = raw_input("Enter a message: ")
		# msg = input("Enter a message: ")
		msg = "position:300:400:0"

		sock.sendall((msg + '\n').encode('utf-8'))
		data = sock.recv(1024)

		print("== Server responded. ==")

		i = i + 1

	sock.close()

def runServer():
	#serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	#serversocket.bind(('localhost', 21564))
	#serversocket.listen(5)  # become a server socket, maximum 5 connections

	#connection, address = serversocket.accept()
	#while True:
		##connection, address = serversocket.accept()
		#buf = connection.recv(64)
		#if len(buf) > 0:
			#print(buf)
			#break
	soc = socket.socket()  # Create a socket object
	host = "localhost"  # Get local machine name
	port = 21564  # Reserve a port for your service.
	soc.bind((host, port))  # Bind to the port
	soc.listen(5)  # Now wait for client connection.

	conn, addr = soc.accept()  # Establish connection with client.
	print("Got connection from", addr)
	while True:
		msg = conn.recv(1024)
		print(msg)

def main():

	t1 = threading.Thread(target=runClient, args=())
	t2 = threading.Thread(target=runServer, args=())
	t1.start()
	t2.start()

	t1.join()
	t2.join()

if __name__ == "__main__":
    main()
