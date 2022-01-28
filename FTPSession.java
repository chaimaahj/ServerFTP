package Server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class FTPSession {
	
	
	
	private enum Status {
		NOT_LOGGED, ENTERED_LOGIN, LOGGED
	}
	
	private Socket socket;
	private PrintWriter commandOutWriter;
	private BufferedReader commandIn;

	
	
	public String username = "chaimaa";
	public String password = "pass";
	

	public Status status = Status.NOT_LOGGED;
	
	public boolean anonymousMode = false;
	public boolean readyToExit = false;
	
	public FTPSession(Socket sock) {
		this.socket = sock;
		
	}
	
	public void start() {
		try {
			//récupérer la requête du client
			this.commandIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//formater en texte
			this.commandOutWriter = new PrintWriter(socket.getOutputStream(), true);
			
			this.sendMsgToClient("220 Service ready");
			//différent de false 
			while(!this.readyToExit) {
				//lire la commande récupéré dans le bufferedReader
				this.executeCommand(this.commandIn.readLine());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		finally {
			
			try {
				commandIn.close();
				this.commandOutWriter.close();
				this.socket.close();
				
				this.debug("Sockets fermees");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendMsgToClient(String msg) {
		this.commandOutWriter.println(msg+"\r\n");
	}
	
	private void debug(String msg) {
		System.out.println(msg);
	}

	
	private void executeCommand(String c) {
		//renvoyer l'index du premier espace
		int index = c.indexOf(" ");
		String command = ((index == -1) ? c.toUpperCase() : c.substring(0, index).toUpperCase());
		//récupérer le reste de la requête dans une autre chaine de caractère
		String args = (( index == -1) ? null : c.substring(index+1, c.length()));
		
		this.debug("Command: "+command + " Args: "+ args);
		
		switch(command) {
			case "USER": 
				handleUSER(args);
				break;
			case "PASS": 
				handlePASS(args);
				break;
			case "SYST":
				handleSYST();
				break;
			case "QUIT":
				handleQUIT();
				break;
			default: 
				this.sendMsgToClient("501 Unkown command");
				break;
		}
	}
	
	private void handleUSER(String username) {
		if(username.toLowerCase().equals("chaimaa")) {
			this.sendMsgToClient("331 User name ok, need password");
			this.status = Status.ENTERED_LOGIN;
		}
		else if(this.status == Status.LOGGED) {
			this.sendMsgToClient("530 User already logged in");
		}
		else {
			this.sendMsgToClient("530 Not logged in");
		}
	}
	
	private void handlePASS(String password) {
		if(this.status  == Status.ENTERED_LOGIN && password.equals("pass")) {
			this.status = Status.LOGGED;
			this.sendMsgToClient("230 Welcome to CHAIMAA-FTP");
			this.sendMsgToClient("230 Logged in successfully");
		}
		else if(this.status == Status.LOGGED) {
			this.sendMsgToClient("530 User already logged in");
		}
		else {
			this.sendMsgToClient("530 Not logged in");
		}
	}
	
	private void handleQUIT() {
		this.sendMsgToClient("221 disconnecting from CHAIMAA SERVER");
		this.readyToExit = true;
	}
	
	private void handleSYST() {
		this.sendMsgToClient("215 Windows Type");
	}
	
	private void handlePUT(){
		try {
			FileInputStream fis = new FileInputStream("fiche.txt");
			byte b[] = new byte[2000];
			fis.read(b, 0, b.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
