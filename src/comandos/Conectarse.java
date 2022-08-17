package comandos;

import java.io.Serializable;

import servidor.Paquete;

public class Conectarse implements Comando, Serializable {
	
	private static final long serialVersionUID = 1L;
	public final Paquete pcliente;
	public Conectarse(Paquete pcliente) {
		this.pcliente = pcliente;
	}
	
	@Override
	public int procesar_comando() {
		return Comando.CONECTARSE;
	}

}
