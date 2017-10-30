#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import socket
import os


URL = sys.argv[1]
TCP_IP = socket.gethostname()
BUFFER_SIZE = 1024

if len(sys.argv) == 3:
    TCP_PORT = int(sys.argv[2])
else:
    TCP_PORT = 80

tcp_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
tcp_sock.connect((TCP_IP, TCP_PORT))
tcp_sock.sendall(URL.encode('utf-8'))
HTTP_RCV_CODE = tcp_sock.recv(BUFFER_SIZE)
HTTP_RCV_CODE = HTTP_RCV_CODE.decode('utf-8')
COD_FRA = HTTP_RCV_CODE.split()

if int(COD_FRA[0]) == 1:
    os.makedirs(os.path.dirname(URL), exist_ok=True)

    with open(URL, 'wb') as f:
        while True:
            data = tcp_sock.recv(BUFFER_SIZE)
            if not data:
                break
            f.write(data)
        f.close()
        tcp_sock.close()
        print('-=-=-=-=-=-=-=-=-=-=-=-=-');
        print('Arquivo recebido. Conexão fechada.')
else:
    tcp_sock.close()