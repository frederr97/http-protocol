//Criado por Fred Resende//
//Disciplina de Redes de Computadores//
//Professor Flavio Luiz Schiavoni//
//Navegador//

import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;
import java.io.OutputStream;

public class Browser{
	
	static Scanner reader, reader2;
	static String browser, first, num, send;
	private static FileWriter file;
	private static Socket socket;	//Importa a biblioteca para criação dos sockets

	public static void main(String[] args) throws IOException{
		System.out.println("[Navegador em execução...]");
		//Leitura do comando
		reader = new Scanner(System.in);
		String aux = reader.nextLine();
		String[] receveid = aux.split(" ");
		//Quebra o comando para fazer a requisição
		if(receveid.length == 2){
			first = receveid[0];
			num = "80";
		}else if(receveid.length == 1){
			first = receveid[0];
			num = "80";				//Caso o usuário não informe a porta, a padrão será 80
		}
		//Adiciona os prefixos 
		String comp = first.replace("http://", "").replace("https://", "");
		String[] combinado = comp.split("/");
		int aux2 = 0;
		String follow = "";
		String rest = null;
		
		for(int i = combinado.length - 1; i > 0; i--){
			follow = combinado[i].concat("/" + follow);
		}

		follow = "/" + follow;
		follow = follow.substring(0, follow.length() - 1);
		rest = (follow.substring(follow.lastIndexOf("/") + 1));
		//Coloca como página padrão se não houver nada a mais no final da url passada
		if(rest.length() == 0){
			rest = "index";
		}
		
		int porta = Integer.parseInt(num);
		socket = new Socket(combinado[0], porta);

		if(socket.isConnected()){
			System.out.println("[" + socket.getInetAddress() + " acaba de conectar-se ao servidor.]");
			System.out.println("[Fazendo requisição...]");
			//Faz a verificação novamente para saber se é HTTP ou HTTPS
			if(comp.substring(comp.length() - 1).equals("/")){
				send = "GET " + follow + "/ HTTP/1.1\r\n" + "Host: " + combinado[0] + "\r\n" + "\r\n";
				rest = rest + ".html";
			}else{
				send = "GET " + follow + " HTTP/1.1\r\n" + "Host: " + combinado[0] + "\r\n" + "\r\n";
			}
			//Realiza o envio do GET
			System.out.println("[Enviando requisição...]");
			OutputStream msg = socket.getOutputStream();
			byte[] separated = send.getBytes();
			msg.write(separated);
			msg.flush();
			reader2 = new Scanner(socket.getInputStream());
			file = new FileWriter(new File(rest));
			ArrayList<String> armaz = new ArrayList<String>();

			System.out.println("[Enviando arquivo...]");
			while(reader2.hasNext()){
				String line = reader2.nextLine();
				System.out.println(line);
				armaz.add(line);
			}

			for(int i = 0; i < armaz.size(); i++){
				if(armaz.get(i).contains("Content-Type")){
					aux2 = i + 2;
					break;

				}
			}
			//Manda o arquivo para o cliente
			for(int i = aux2; i < armaz.size(); i++){
				file.write(armaz.get(i));
				file.write("\n");
			}
			
			System.out.println("[Arquivo enviado.]");
			file.flush();
			file.close();
		}
	}
}
