while True:
    line = input()
    if line == "warmup":
        print("OK")
    else:
        exec(open(line).read())
        print("--done--")