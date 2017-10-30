#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Oct 19 23:58:45 2017
@author: Christian R. F. Gomes
@title: Navegador (Cliente)
"""

import sys
import socket
import os


def url_sep(url):
    url = url.split('/', 1)
    host = url[0]
    url = url[1]

    return url, host

URL = sys.argv[1]

if URL.startswith('http') or URL.startswith('https'):
    URL = URL.split('//', 1)[1]
    URL, HOST = url_sep(URL)
else:
    URL, HOST = url_sep(URL)

TCP_IP = socket.gethostname()
BUFFER_SIZE = 1024

if len(sys.argv) == 3:
    TCP_PORT = int(sys.argv[2])
else:
    TCP_PORT = 80

tcp_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
tcp_sock.connect((TCP_IP, TCP_PORT))

HTTP_SEND_MSG = 'GET {0} HTTP/1.1\nHost: {1}'.format(URL, HOST)

tcp_sock.sendall(HTTP_SEND_MSG.encode('utf-8'))

HTTP_RCV_CODE = tcp_sock.recv(BUFFER_SIZE)
HTTP_RCV_CODE = HTTP_RCV_CODE.decode('utf-8')
COD_FRA = HTTP_RCV_CODE.split()

if int(COD_FRA[1]) == 200:
    print('\nResposta do servidor:\n{0}'.format(HTTP_RCV_CODE))

    # Criação do diretório no cliente, se não existir.
    # Python 3.2+
    FILE_PATH = '{0}/{1}'.format(HOST, URL)
    os.makedirs(os.path.dirname(FILE_PATH), exist_ok=True)

    # Python 3.2-
    # if not os.path.exists(os.path.dirname(URL)):
    #     try:
    #         os.makedirs(os.path.dirname(URL))
    #     except OSError as exc:
    #         print('Falha na criação do diretório.')

    with open(FILE_PATH, 'wb') as f:
        while True:
            # print('Recebendo dados...')
            data = tcp_sock.recv(BUFFER_SIZE)
            if not data:
                break
            f.write(data)
        f.close()
        print('\nURL solicitada obtida com sucesso.')
        tcp_sock.close()
        print('Conexão fechada.\n')
else:
    print('\nResposta do servidor:{0}\n'.format(HTTP_RCV_CODE))
    tcp_sock.close()