package ventanas;

import javax.swing.JFrame;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import cliente.Cliente;
import comandos.*;
import servidor.Paquete;

import javax.swing.JLabel;
import java.awt.Font;

public class JLobby extends JFrame {

	private static final long serialVersionUID = 1L;
	private JList<String> list;
	private JScrollPane listScroller;
	private DefaultListModel<String> model;
	private JButton crearSalaButton;
	private JButton unirseSalaButton;
	private Cliente cliente;
	private int salasActivas = 0;
	private JMenuBar menuBar;
	private JMenu mnOpciones;
	private JMenuItem MenuConectar;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JLobby frame = new JLobby();
					frame.setTitle("Lobby");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public JLobby() {
		iniFrame();
		iniButtonCrearSala();
		iniLista();
		iniButtonUnirse();
		iniMenuOpciones();
		setResizable(false);
	}

	private void iniFrame() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cliente.ejecutarComando(new Desconectar());
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		setBounds(50, 50, 358, 308);
	}

	public void iniMenuOpciones() {
		menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 342, 22);
		getContentPane().add(menuBar);
		mnOpciones = new JMenu("Opciones");
		menuBar.add(mnOpciones);
		MenuConectar = new JMenuItem("Conectar");
		mnOpciones.add(MenuConectar);
		MenuConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nombre = JOptionPane.showInputDialog("Ingrese nombre de usuario");
				if(nombre != null) {
					cliente = new Cliente(1200, "localhost", nombre);
					mnOpciones.setEnabled(false);
					cliente.inicializarHiloCliente(getLobby());
					cliente.ejecutarComando(new Conectarse(new Paquete(cliente)));
					unirseSalaButton.setEnabled(true);
					crearSalaButton.setEnabled(true);
				}
			}
		});
	}

	public void iniLista() {
		model = new DefaultListModel<String>();
		list = new JList<String>();
		listScroller = new JScrollPane(list);
		listScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		listScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listScroller.setBounds(10, 50, 319, 175);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setBounds(29, 46, 300, 300);
		getContentPane().add(listScroller);
		list.setModel(model);
	}

	public void iniButtonCrearSala() {
		crearSalaButton = new JButton("Crear Sala");
		crearSalaButton.setBounds(229, 230, 100, 23);
		getContentPane().add(crearSalaButton);
		crearSalaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nombre = JOptionPane.showInputDialog("Ingrese el nombre de la sala");
				if (nombre != null && nombre != "")
					cliente.ejecutarComando(new CrearSala(nombre));
			}
		});
		crearSalaButton.setEnabled(false);
	}

	public void iniButtonUnirse() {
		unirseSalaButton = new JButton("Unirse");
		unirseSalaButton.setBounds(130, 230, 89, 23);
		getContentPane().add(unirseSalaButton);
		JLabel lblSalas = new JLabel("Salas");
		lblSalas.setFont(new Font("Arial", Font.PLAIN, 12));
		lblSalas.setBounds(10, 33, 46, 17);
		getContentPane().add(lblSalas);
		unirseSalaButton.setEnabled(false);
		unirseSalaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nombresala = list.getSelectedValue();
				if (salasActivas == 3) {
					maxConexiones();
					return;
				}
				if (nombresala != null) {
					nombresala = nombresala.substring(0, nombresala.indexOf(" ("));
					cliente.ejecutarComando(new UnirseSala(nombresala));
				}
			}
		});
	}

	public void actualizar_salas(List<String> salas) {
		model.clear();
		Collections.sort(salas);
		for (String sala : salas) {
			model.addElement(sala);
		}
		list.setModel(model);
	}

	public JList<String> getLista() {
		return this.list;
	}

	public Cliente getCliente() {
		return this.cliente;
	}

	public void maxConexiones() {
		JOptionPane.showMessageDialog(null, "No puedes estar conectado\nen mas de 3 salas");
	}

	public JLobby getLobby() {
		return this;
	}

	public void setSalasActivas(int n) {
		this.salasActivas = n;
	}

	public int getSalasActivas() {
		return this.salasActivas;
	}
}
