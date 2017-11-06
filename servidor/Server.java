//Criado por Fred Resende//
//Disciplina de Redes de Computadores//
//Professor Flavio Luiz Schiavoni//
//Servidor//

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;


public class Server implements Runnable{

	static Scanner reader;
	static String client, url, first, endereco, longg;
	static PrintStream send;
	static ServerSocket recept;
	static Socket socket;
	
	Server(Socket socket){
		Server.socket = socket;
	}

	static int num;
	static BufferedWriter devolve;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		//Leitura do comando
		reader = new Scanner(System.in);
		String endereco = reader.nextLine();
		String[] comby = endereco.split(" ");
		//Quebra o comando para colocar a pasta no ar
		if(comby.length == 2){
			longg = comby[0];
			num = Integer.parseInt(comby[1]);
		}else if(comby.length == 1){
			longg = comby[0];
			num = 8080;		//Caso o usuário não informe a porta, a padrão será 8080
		}
		
		System.out.println("[Servidor em execução...]");
		//Abre um socket para o servidor
		recept = new ServerSocket(num);
		while(true){
			Socket socket = recept.accept();
			new Thread(new Server(socket)).start();
		}
	}
	
	static ObjectOutputStream output;
	private static final String erro1 = "<html><head><meta charset='utf-8'></head><body><br><br><br><br><center><h3>Requisição não encontrada</h3></center></body></html>";
	private static final String ended = "\r\n\r\n";
	private static final String erro2 = "HTTP/1.1 Error 404 - Not Found\r\n" + "Content-Type: text/html\r\n" + "Content-Length: ";
	private static String first_code = "<html><head></head><body>";
			
	static public void GetReq() throws IOException, ClassNotFoundException{

		int aux2 = 0;
		String conjunc = longg + url;
		File envy = new File(conjunc);
		//Faz a requisição por get
		if(envy.isDirectory()){ 
			OutputStream ent = socket.getOutputStream();
			String lista[] = envy.list();  
            String send = "";
            ent.write(send.getBytes(Charset.forName("UTF-8")));
	        for(int i = 0; i < lista.length; i++){ 
	        	first_code += "<a style='color: #283747' href='" + lista[i] + "'> -> " + lista[i] + "</a><br/>";
	        }  
	        first_code += "</body></html>";
	        
	        PrintWriter out = new PrintWriter(socket.getOutputStream());
	        out.println("HTTP/1.1 200 OK");
	        out.println("Content-Type: text/html");
	        out.println("\r\n");
	        out.println(first_code);
	        out.flush();
	        out.close();
	        
	        ent.flush();
	    }
		
		else if(envy.exists()){
			String aux = Files.probeContentType(envy.toPath());
			System.out.println("Content Type: " + aux);
			OutputStream ent = socket.getOutputStream();
			String send = "HTTP/1.1 200 OK\r\nContent-Type: " + aux + "\r\n";
			ent.write(send.getBytes(Charset.forName("UTF-8")));
			FileInputStream fis = new FileInputStream(envy);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ent.write(("Content-Length: " + String.valueOf(envy.length()) + "\r\n\r\n").getBytes());

			long size = envy.length();
			byte[] contents;
			while(size > 0){
				if(size >= 1){
					size = size - 1;
					aux2 = 1;
				}else if(size < 1){
					aux2 = (int) size;
					size = 0;
				}
				contents = new byte[aux2];
				bis.read(contents, 0, aux2);
				ent.write(contents);
			}
			ent.flush();
			fis.close();
			bis.close();
		}
			
		else {
			devolve.write(erro2 + erro1.length() + ended + erro1);
		}
	}
	
	public void run(){
		try{
			devolve = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream()), "UTF-8"));
			Scanner reader2 = new Scanner(socket.getInputStream());
			while(reader2.hasNextLine()){
				String received = reader2.nextLine();
				System.out.println(received);
				String[] comby = received.split(" ");
				if (comby.length == 3) {
					client = comby[0];
					url = comby[1];
					first = comby[2];
				} else if (comby.length == 2) {
					client = comby[0];
					url = comby[1];
					first = "8080";
				}
				break;
			}
			GetReq();
			devolve.flush();
			devolve.close();
			socket.close();
			reader2.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
