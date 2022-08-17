package ventanas;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import cliente.*;
import comandos.AbandonarSala;
import comandos.EnviarMsj;
import comandos.EnviarMsjPrivado;

public class JChatCliente extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Cliente cliente;
	private String nombreSala;
	private JTextField textField;
	private JButton btnEnviar;
	private JTextPane textArea;
	private JScrollPane scrollPane;
	private String historialChat = "";
	private JList<String> list;
	private JScrollPane listScroller;
	private JButton btnDecargar;
	private JButton btnMsjPrivado;

	public JChatCliente(Cliente cliente, String nombreSala) {
		this.cliente = cliente;
		this.nombreSala = nombreSala;
		iniFrame();
		iniBotonEnviar();
		iniBotonMensajePrivado();
		iniJTextField();
		iniTextArea();
		iniBotonDescargar();
		iniList();
		setResizable(false);
	}
	
	private void iniFrame() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cliente.ejecutarComando(new AbandonarSala(nombreSala));
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 633, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
	}
	
	private void iniBotonMensajePrivado() {
		btnMsjPrivado = new JButton("Private MSJ");
		btnMsjPrivado.setBounds(445, 219, 150, 31);
		contentPane.add(btnMsjPrivado);
		btnMsjPrivado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enviarMsjPrivado();
				String message = textField.getText();
				if (!message.isEmpty()) {
					LocalDateTime now = LocalDateTime.now();
					DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
					String formatDateTime = now.format(format);
					escribirMsjPrivaEnTextArea(
							"[" + formatDateTime + "] " + cliente.getNombre() + ": " + message + "\n");
					textField.setText("");
				}
			}
		});
	}
	
	private void iniList() {
		list = new JList<String>();
		listScroller = new JScrollPane(list);
		listScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		listScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listScroller.setBounds(445, 33, 150, 175);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		contentPane.add(listScroller);
	}
	
	private void iniBotonDescargar() {
		btnDecargar = new JButton(new ImageIcon("icon.png"));
		btnDecargar.setBounds(10, 3, 36, 30);
		contentPane.add(btnDecargar);
		btnDecargar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss--");
					PrintWriter salida = new PrintWriter(new File("Descargas/" + dtf.format(LocalDateTime.now())
							+ nombreSala + "-" + cliente.getNombre() + ".txt"));
					salida.println(historialChat);
					salida.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	private void iniTextArea() {
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 33, 414, 175);
		contentPane.add(scrollPane);
		textArea = new JTextPane();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
	}
	
	private void iniJTextField() {
		textField = new JTextField();
		textField.setBounds(10, 219, 312, 31);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enviarMsj();
				textField.setText("");
			}
		});
	}

	private void iniBotonEnviar() {
		btnEnviar = new JButton("Enviar");
		btnEnviar.setBounds(335, 219, 89, 31);
		contentPane.add(btnEnviar);
		btnEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enviarMsj();
				textField.setText("");
			}
		});
	}

	public void escribirMensajeEnTextArea(String mensaje) {
		historialChat += mensaje;
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setForeground(attrs, Color.BLACK);
		try {
			textArea.getStyledDocument().insertString(textArea.getStyledDocument().getLength(), mensaje, attrs);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		try {
			sonidoMsj();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	public void escribirMsjPrivaEnTextArea(String mensaje) {
		historialChat += mensaje;
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setForeground(attrs, Color.RED);
		try {
			textArea.getStyledDocument().insertString(textArea.getStyledDocument().getLength(), mensaje, attrs);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		try {
			sonidoMsj();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	public void enviarMsj() {
		String message = textField.getText();
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formatDateTime = now.format(format);
		if (!message.isEmpty())
			cliente.ejecutarComando(new EnviarMsj(nombreSala,
					"[" + formatDateTime + "] " + cliente.getNombre() + ": " + message + "\n"));
		return;
	}

	public void enviarMsjPrivado() {
		String message = textField.getText();
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formatDateTime = now.format(format);
		String nombre = list.getSelectedValue();
		if (!message.isEmpty() && nombre != null) {
			nombre = nombre.substring(0, nombre.indexOf(" ("));
			if (!nombre.equals(cliente.getNombre()))
				cliente.ejecutarComando(new EnviarMsjPrivado(nombreSala, nombre,
						"[" + formatDateTime + "] " + cliente.getNombre() + ": " + message + "\n"));
		}
		return;
	}

	public void sonidoMsj() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		String soundName = "sonidos/sonido_msn.wav";
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
		Clip clip = AudioSystem.getClip();
		clip.open(audioInputStream);
		clip.start();
	}

	public String getSala() {
		return this.nombreSala;
	}

	public JChatCliente iniciar() {
		return this;
	}

	public void run() {
		this.setTitle("Sala: " + nombreSala + " | Usuario: " + cliente.getNombre());
		this.setVisible(true);
	}

	public void actualziar_ons(DefaultListModel<String> model) {
		list.setModel(model);
	}
}
