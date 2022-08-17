package cliente;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import comandos.Comando;
import ventanas.JLobby;

public class Cliente  {
	
	private Socket socket;
	public final String nombre;
	private int puerto;
	private String ip;
	private ObjectOutputStream salida;
	private ObjectInputStream entrada;
	
	public Cliente(int puerto, String ip,String nombre) {
		this.puerto = puerto;
		this.ip = ip;
		this.nombre = nombre;
		try {
			socket = new Socket(this.ip, this.puerto);
			 salida = new ObjectOutputStream(socket.getOutputStream());
			 entrada = new ObjectInputStream(socket.getInputStream());
			 System.out.println("Cliente creado");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ObjectInputStream getEntrada() {
		return this.entrada;
	}
	
	public ObjectOutputStream getSalida() {
		return this.salida;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public Socket getSocket() {
		return this.socket;
	}

	public void ejecutarComando(Comando comando) {
		try {
			salida.flush();
			salida.writeObject(comando);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void inicializarHiloCliente(JLobby menu) {
			new HiloCliente(entrada, menu).start();
	}
}
