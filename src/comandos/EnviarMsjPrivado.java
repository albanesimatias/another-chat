package comandos;

import java.io.Serializable;

public class EnviarMsjPrivado implements Comando, Serializable {

	private static final long serialVersionUID = 1L;
	public final String mensaje;
	public final String nombreSala;
	public final String nombre;
	public EnviarMsjPrivado(String nombreSala, String nombre,String mensaje) {
		this.mensaje = mensaje;
		this.nombre = nombre;
		this.nombreSala = nombreSala;
	}
	@Override
	public int procesar_comando() {
		return Comando.ENVIAR_MSJ_PRIV;
	}

}
