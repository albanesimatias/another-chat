package comandos;

import java.io.Serializable;

public class UnirseSala implements Comando, Serializable {
	
	private static final long serialVersionUID = 1L;
	public final String nombreSala;
	
	public UnirseSala(String nombreSala) {
		this.nombreSala = nombreSala;
	}
	
	public int procesar_comando() {
		return Comando.UNIRSE_SALA;
	}

}
