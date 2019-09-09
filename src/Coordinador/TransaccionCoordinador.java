package Coordinador;


import java.util.ArrayList;
import java.util.List;

import Cliente.Transaccion;

public class TransaccionCoordinador {

	private int numTransaccion;
	private int estado; //1 activa,2 readyCommit, 3 doCommit , 4 abort, 5 haveCommit
	private List<Transaccion> transacciones;
	private List<String> mensajesLog;
	
	public TransaccionCoordinador() {
		transacciones = new ArrayList<>();
		mensajesLog = new ArrayList<>();
		estado = 1;
	}

	
	public List<String> getMensajesLog() {
		return mensajesLog;
	}


	public void setMensajesLog(List<String> mensajesLog) {
		this.mensajesLog = mensajesLog;
	}


	public int getNumTransaccion() {
		return numTransaccion;
	}

	public void setNumTransaccion(int numTransaccion) {
		this.numTransaccion = numTransaccion;
	}

	public synchronized int getEstado() {
		return estado;
	}

	public synchronized void setEstado(int estado) {
		this.estado = estado;
	}

	public List<Transaccion> getTransacciones() {
		return transacciones;
	}

	public void setTransacciones(List<Transaccion> transacciones) {
		this.transacciones = transacciones;
	}
	
	
	
}
