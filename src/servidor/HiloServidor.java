package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;

import comandos.*;

public class HiloServidor extends Thread {

	private boolean ejecutar = true;
	private Socket cliente;
	private Comando comando;
	private Paquete pcliente;
	ObjectInputStream entrada;
	public HiloServidor(Socket cliente) {
		this.cliente = cliente;
	}

	public void run() {
		
		int caso;
		try {
			entrada = new ObjectInputStream(this.cliente.getInputStream());
			while (ejecutar) {

				comando = (Comando) entrada.readObject();
				caso = comando.procesar_comando();

				switch (caso) {
				case Comando.CONECTARSE: {
					Conectarse cmd = (Conectarse) comando;
					comando_conectarse(cmd.pcliente);
					break;
				}
				case Comando.CREAR_SALA: {
					CrearSala cmd = (CrearSala) comando;
					comando_crear_sala(cmd.nombreSala);
					break;
				}
				case Comando.UNIRSE_SALA: {
					UnirseSala cmd = (UnirseSala) comando;
					comando_unirse_sala(cmd.nombreSala);
					break;
				}
				case Comando.ENVIAR_MSJ: {
					EnviarMsj cmd = (EnviarMsj) comando;
					comando_enviar_msj(cmd.nombreSala, cmd.msj);
					break;
				}
				case Comando.ABANDONAR_SALA: {
					AbandonarSala cmd = (AbandonarSala) comando;
					comando_abandonar_sala(cmd.sala);
					break;
				}
				case Comando.ENVIAR_MSJ_PRIV: {
					EnviarMsjPrivado cmd = (EnviarMsjPrivado) comando;
					comando_enviar_msj_priv(cmd.mensaje,cmd.nombreSala, cmd.nombre);
					break;
				}
				case Comando.DESCONECTAR: {
					ejecutar = false;
					comando_desconectar();
					break;
				}
				default:
					break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void comando_actualizar_salas() throws IOException {
		Servidor.getSalidas().get(cliente).flush();
		Set<String> keySalas = Servidor.getSalas().keySet();
		List<String> salas = new ArrayList<String>();
		for (String keySala : keySalas) {
			salas.add(keySala + " (" + Servidor.getSalas().get(keySala).size() + ")");
		}
		for (Socket socket : Servidor.getSalidas().keySet()) {
			Servidor.getSalidas().get(socket).writeInt(Comando.ACTUALIZAR_SALAS);
			Servidor.getSalidas().get(socket).writeObject(salas);
			Servidor.getSalidas().get(socket).flush();
		}
	}

	private void comando_conectarse(Paquete pcliente) throws IOException {
		this.pcliente = pcliente;
		this.pcliente.setSocket(cliente);
		this.pcliente.setSalida(Servidor.getSalidas().get(cliente));
		Set<String> keySalas = Servidor.getSalas().keySet();
		List<String> salas = new ArrayList<String>();
		for (String keySala : keySalas) {
			salas.add(keySala + " (" + Servidor.getSalas().get(keySala).size() + ")");
		}
		Servidor.getSalidas().get(cliente).flush();
		Servidor.getSalidas().get(cliente).writeInt(Comando.CONECTARSE);
		Servidor.getSalidas().get(cliente).flush();
		Servidor.getSalidas().get(cliente).writeObject(salas);
		Servidor.getSalidas().get(cliente).flush();

	}

	private void comando_crear_sala(String nombreSala) throws IOException {
		if (!Servidor.getSalas().containsKey(nombreSala)) {
			Servidor.getSalas().put(nombreSala, new LinkedList<Paquete>());
			Set<String> keySalas = Servidor.getSalas().keySet();
			List<String> salas = new ArrayList<String>();
			for (String keySala : keySalas) {
				salas.add(keySala + " (" + Servidor.getSalas().get(keySala).size() + ")");
			}
			for (Socket socket : Servidor.getSalidas().keySet()) {
				Servidor.getSalidas().get(cliente).flush();
				Servidor.getSalidas().get(socket).writeInt(Comando.CREAR_SALA);
				Servidor.getSalidas().get(socket).writeObject(salas);
				Servidor.getSalidas().get(socket).flush();
			}
		}
	}

	private void comando_unirse_sala(String nombreSala) throws IOException {
		for (String sala : pcliente.getSalas()) {
			if(nombreSala.equals(sala))
				return;
		}
		if (pcliente.getSalasActivas() < 3) {
			pcliente.conectarSala(nombreSala);
			Servidor.getSalas().get(nombreSala).add(pcliente);
			Servidor.getSalidas().get(cliente).flush();
			Servidor.getSalidas().get(cliente).writeInt(Comando.UNIRSE_SALA);
			Servidor.getSalidas().get(cliente).flush();
			Servidor.getSalidas().get(cliente).writeUTF(nombreSala);
			Servidor.getSalidas().get(cliente).flush();
			actualizar_on(nombreSala);
			comando_actualizar_salas();
		}
	}

	private void comando_abandonar_sala(String nombreSala) throws IOException {
		Servidor.getSalas().get(nombreSala).remove(pcliente);
		pcliente.desconectarSala(nombreSala);
		comando_actualizar_salas();
		Servidor.getSalidas().get(cliente).flush();
		Servidor.getSalidas().get(cliente).writeInt(Comando.ABANDONAR_SALA);
		Servidor.getSalidas().get(cliente).flush();
		Servidor.getSalidas().get(cliente).writeUTF(nombreSala);
		Servidor.getSalidas().get(cliente).flush();
		actualizar_on(nombreSala);
	}

	private void comando_enviar_msj(String nombreSala, String msj) throws IOException {
		List<Paquete> receptores = Servidor.getSalas().get(nombreSala);
		for (Paquete receptor : receptores) {
			receptor.getSalida().flush();
			receptor.getSalida().writeInt(Comando.ENVIAR_MSJ);
			receptor.getSalida().flush();
			receptor.getSalida().writeUTF(nombreSala);
			receptor.getSalida().flush();
			receptor.getSalida().writeUTF(msj);
			receptor.getSalida().flush();
		}
	}
	
	private void comando_enviar_msj_priv(String mensaje, String nombreSala, String nombre) throws IOException {
		List<Paquete> receptores = Servidor.getSalas().get(nombreSala);
		for (Paquete receptor : receptores) {
			if(receptor.getNombre().equals(nombre)) {
				receptor.getSalida().writeInt(Comando.ENVIAR_MSJ_PRIV);
				receptor.getSalida().flush();
				receptor.getSalida().writeUTF(nombreSala);
				receptor.getSalida().flush();
				receptor.getSalida().writeUTF(mensaje);
				receptor.getSalida().flush();
				return;
			}
		}
	}
	
	
	private void actualizar_on(String nombreSala) throws IOException{
		List<Paquete> receptores = Servidor.getSalas().get(nombreSala);
		DefaultListModel<String> model = new DefaultListModel<>();
		long hora;
		Date horaActual = new Date();
		for (Paquete paquete : receptores) {
			hora = horaActual.getTime()-paquete.getSalasDate().get(nombreSala);
			hora = TimeUnit.MINUTES.convert(hora, TimeUnit.MILLISECONDS);
			model.addElement(paquete.getNombre()+ " ("+hora+" mins)");
		}
		for (Paquete receptor : receptores) {
			receptor.getSalida().flush();
			receptor.getSalida().writeInt(Comando.ACTUALIZAR_ONS);
			receptor.getSalida().flush();
			receptor.getSalida().writeUTF(nombreSala);
			receptor.getSalida().flush();
			receptor.getSalida().writeObject(model);
			receptor.getSalida().flush();
		}
	}
	
	private void comando_desconectar() throws IOException {
		Servidor.getSalidas().remove(cliente);
		pcliente.getSalida().writeInt(Comando.DESCONECTAR);
		pcliente.getSalida().flush();
	}
}
