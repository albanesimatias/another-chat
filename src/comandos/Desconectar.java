package comandos;

import java.io.Serializable;

public class Desconectar implements Comando, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public int procesar_comando() {
		return Comando.DESCONECTAR;
	}

}
