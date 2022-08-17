package comandos;

import java.io.Serializable;

public class EnviarMsj implements Comando, Serializable {

	private static final long serialVersionUID = 1L;
	public final String nombreSala;
	public final String msj;
	
	public EnviarMsj(String nombreSala, String msj) {
		this.nombreSala = nombreSala;
		this.msj = msj;
	}
	@Override
	public int procesar_comando() {
		return Comando.ENVIAR_MSJ;
	}

	
}
