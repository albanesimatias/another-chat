package servidor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Servidor {
	
	private ServerSocket server;
	private static Map<String, LinkedList<Paquete>> salas = new HashMap<String, LinkedList<Paquete>>();
	private static Map<Socket,ObjectOutputStream> clientes = new HashMap<Socket,ObjectOutputStream>();;
	
	public Servidor(int puerto) {
		try {
			server = new ServerSocket(puerto);	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void ejecutar() {
		Socket socket;
		try {
			while(true) {
				socket = server.accept();
				ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
				clientes.put(socket, salida);
				new HiloServidor(socket).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static Map<String, LinkedList<Paquete>> getSalas(){
		return Servidor.salas;
	}
	
	public static Map<Socket, ObjectOutputStream> getSalidas(){
		return Servidor.clientes;
	}
	
	public static void main(String[] args) {
		new Servidor(1200).ejecutar();
	}
}
