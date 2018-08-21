import sys
import io
import traceback
import socketserver


class EvaluationHandler(socketserver.BaseRequestHandler):
    def handle(self):
        path = self.request.recv(2048).strip()
        if path == b"warmup":
            self.request.sendall(bytes("OK\n", "utf-8"))
        else:
            old_stdout = sys.stdout
            sys.stdout = io.StringIO()

            old_stderr = sys.stderr
            sys.stderr = io.StringIO()

            did_error = False

            try:
                exec(open(path).read())
            except Exception:
                traceback.print_exc()
                did_error = True

            out = sys.stdout.getvalue()
            sys.stdout = old_stdout
            err = sys.stderr.getvalue()
            sys.stderr = old_stderr

            if did_error:
                self.request.sendall(bytes(err, "utf-8"))
            else:
                self.request.sendall(bytes(out, "utf-8"))


if __name__ == "__main__":
    HOST, PORT = "localhost", 50051

    with socketserver.TCPServer((HOST, PORT), EvaluationHandler) as server:
        server.serve_forever()
