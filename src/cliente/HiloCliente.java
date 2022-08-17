package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;

import comandos.Comando;
import ventanas.JChatCliente;
import ventanas.JLobby;

public class HiloCliente extends Thread {

	private boolean ejecutar = true;
	private ObjectInputStream entrada;
	private JLobby menu;
	private List<JChatCliente> chats;
	private int salasConectadas = 0;

	public HiloCliente(ObjectInputStream entrada, JLobby menu) {
		this.entrada = entrada;
		this.menu = menu;
		this.chats = new ArrayList<JChatCliente>();
	}

	public void run() {
		int caso;
		try {
			while (ejecutar) {
				caso = entrada.readInt();
				switch (caso) {
				case Comando.CONECTARSE: {
					conectarse();
					break;
				}
				case Comando.CREAR_SALA: {
					crear_sala();
					break;
				}
				case Comando.UNIRSE_SALA: {
					unirse_sala();
					break;
				}
				case Comando.ABANDONAR_SALA: {
					abandonar_sala();
					break;
				}
				case Comando.ACTUALIZAR_SALAS: {
					actualizar_sala();
					break;
				}
				case Comando.ENVIAR_MSJ: {
					enviar_msj();
					break;
				}
				case Comando.DESCONECTAR: {
					desconectar();
					break;
				}
				case Comando.ACTUALIZAR_ONS: {
					actualizar_ons();
					break;
				}
				case Comando.ENVIAR_MSJ_PRIV: {
					enviar_msj_priv();
					break;
				}
				default:
					break;
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

	}

	private void actualizar_sala() throws ClassNotFoundException, IOException {
		@SuppressWarnings("unchecked")
		List<String> salas = (List<String>) entrada.readObject();
		menu.actualizar_salas(salas);
	}

	private void conectarse() throws ClassNotFoundException, IOException {
		@SuppressWarnings("unchecked")
		List<String> salas = (List<String>) entrada.readObject();
		menu.actualizar_salas(salas);
	}

	private void crear_sala() throws ClassNotFoundException, IOException {
		@SuppressWarnings("unchecked")
		List<String> salas = (List<String>) entrada.readObject();
		menu.actualizar_salas(salas);
	}

	private void unirse_sala() throws IOException {
		String sala = entrada.readUTF();
		chats.add(new JChatCliente(menu.getCliente(), sala));
		chats.get(salasConectadas).run();
		salasConectadas++;
		menu.setSalasActivas(salasConectadas);
	}

	private void enviar_msj() throws IOException {
		String nombreSala = entrada.readUTF();
		String mensaje = entrada.readUTF();
		for (JChatCliente chat : chats) {
			if (chat.getSala().equals(nombreSala))
				chat.escribirMensajeEnTextArea(mensaje);
		}
	}

	private void enviar_msj_priv() throws IOException {
		String nombreSala = entrada.readUTF();
		String mensaje = entrada.readUTF();
		for (JChatCliente chat : chats) {
			if (chat.getSala().equals(nombreSala))
				chat.escribirMsjPrivaEnTextArea(mensaje);
		}
	}

	private void abandonar_sala() throws IOException {
		String sala = entrada.readUTF();
		salasConectadas--;
		menu.setSalasActivas(salasConectadas);
		for (Iterator<JChatCliente> iterator = chats.iterator(); iterator.hasNext();) {
			JChatCliente chat = (JChatCliente) iterator.next();
			if (chat.getSala().equals(sala)) {
				iterator.remove();
				return;
			}
		}
	}

	private void desconectar() throws IOException, ClassNotFoundException {
		this.ejecutar = false;
		menu.getCliente().getSalida().close();
		menu.getCliente().getSocket().close();
		menu.getCliente().getSocket().close();
	}

	private void actualizar_ons() throws IOException, ClassNotFoundException {
		String sala = entrada.readUTF();
		@SuppressWarnings("unchecked")
		DefaultListModel<String> nombres = (DefaultListModel<String>) entrada.readObject();
		for (JChatCliente chat : chats) {
			if (chat.getSala().equals(sala)) {
				chat.actualziar_ons(nombres);
				return;
			}
		}
	}

}
