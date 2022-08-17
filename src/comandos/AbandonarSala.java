package comandos;

import java.io.Serializable;

public class AbandonarSala implements Comando, Serializable{

	private static final long serialVersionUID = 1L;
	public final String sala;
	
	public AbandonarSala(String sala) {
		this.sala = sala;
	}
	@Override
	public int procesar_comando() {
		return Comando.ABANDONAR_SALA;
	}

}
