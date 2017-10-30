#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Oct 19 23:58:45 2017
@author: Christian R. F. Gomes
@title: Cliente (Navegador)
"""

import socket
import sys
from threading import Thread
import time
import re


def split_http_msg(msg):
    msg = msg.split('\n')
    URL = re.search('GET(.*)HTTP/1.1', msg[0])
    URL = URL.group(1)
    HOST = msg[1].split('Host: ', 1)[1]

    return URL.strip(), HOST.strip()


SERVER_URL = sys.argv[1]
TCP_IP = socket.gethostname()
BUFFER_SIZE = 1024

if len(sys.argv) == 3:
    TCP_PORT = int(sys.argv[2])
else:
    TCP_PORT = 80


class ClientThread(Thread):
    def __init__(self, ip, port, conn):
        Thread.__init__(self)
        self.ip = ip
        self.port = port
        self.conn = conn
        print('[+] Nova thread por {0} na porta {1}'.format(ip, str(port)))

    def run(self):
        HTTP_MSG_RCV = self.conn.recv(BUFFER_SIZE)
        HTTP_MSG_RCV = HTTP_MSG_RCV.decode('utf-8')
        URL, HOST = split_http_msg(HTTP_MSG_RCV)
        print('\nMensagem recebida do cliente {0}:\n{1}\n'.format(ip, HTTP_MSG_RCV))

        # URL_RCV = URL_RCV.decode('utf-8')
        FILE_PATH = SERVER_URL + '/' + HOST + '/' + URL
        # print('[debug] FILE_PATH = {0}'.format(FILE_PATH))
        time.sleep(0.1)

        try:
            f = open(FILE_PATH, 'rb')
            l = f.read(BUFFER_SIZE)
            # Envia código de estado + frase
            self.conn.sendall('HTTP/1.1 200 OK\nServer: Apache/2.2.34'.encode('utf-8'))
            time.sleep(0.2)

            while l:
                # print('Enviando arquivo solicitado...')
                self.conn.sendall(l)
                l = f.read(BUFFER_SIZE)
                if not l:
                    f.close()
                    self.conn.close()
                    print('[-] Arquivo enviado com sucesso, conexão fechada.\n')
                    break
        except (OSError, IOError) as e:
            # Envia código de estado + frase
            print('[-] Arquivo solicitado não existe; enviando mensagem de erro.\n')
            self.conn.sendall('HTTP/1.1 404 Not Found\nServer: Apache/2.2.34'.encode('utf-8'))
            self.conn.close()


tcp_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
tcp_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
tcp_sock.bind((TCP_IP, TCP_PORT))
threads = []

print('Server escutando...')

while True:
    tcp_sock.listen(5)
    print('[...] Esperando por conexões...')

    (conn, (ip, port)) = tcp_sock.accept()  # Estabelece conexao com o cliente
    print('Conexão feita por ', (ip, port))
    conn.settimeout(60)  # Cliente é desconectado depois de 60s de inatividade
    new_thread = ClientThread(ip, port, conn)
    new_thread.start()
    threads.append(new_thread)

for t in threads:
    t.join()