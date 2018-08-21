import sys
import io
import socketserver


class MyTCPHandler(socketserver.BaseRequestHandler):
    def handle(self):
        path = self.request.recv(2048).strip()
        if path == b"warmup":
            self.request.sendall(bytes("OK\n", "utf-8"))
        else:
            sys.stdout = io.StringIO()
            exec(open(path).read())
            out = sys.stdout.getvalue()
            sys.stdout.close()
            self.request.sendall(bytes(out, "utf-8"))


if __name__ == "__main__":
    HOST, PORT = "localhost", 50051

    with socketserver.TCPServer((HOST, PORT), MyTCPHandler) as server:
        server.serve_forever()
