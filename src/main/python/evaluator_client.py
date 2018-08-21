import time
import datetime
import socket

HOST, PORT = "localhost", 50051
data = "C:\\Users\\Warren\\AppData\\Local\\Temp\\code_eval1222849660043245737\\arete3624003994965102469.py"

while True:
    start = datetime.datetime.now()
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.connect((HOST, PORT))
        sock.sendall(bytes(data + "\n", "utf-8"))

        received = str(sock.recv(1024*10), "utf-8")

    end = datetime.datetime.now()

    print("Received: {}".format(received))
    print((end - start).microseconds, "microseconds")
    time.sleep(1)
