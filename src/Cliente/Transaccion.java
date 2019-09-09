package Cliente;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import Producto.Producto;

public class Transaccion implements Serializable {

	
	
	
	private List<Object> conjuntoLectura;
	private List<Object> conjuntoEscritura;
	private List<String> mensajesLog;
	
	
	private int numTransaccion;
	private int estado; //1 Activa, 2 preparadaCommit, 3 abortada, 4 commit
	
	
	public Transaccion(){
		
		super();
		estado = -1;
		conjuntoLectura = new ArrayList<Object>();
		conjuntoEscritura = new ArrayList<Object>();
		mensajesLog = new ArrayList<String>();
		numTransaccion = -1;
		
	}
	
	
	@Override
	public String toString() {
		return "Transaccion [conjuntoLectura=" + conjuntoLectura + ", conjuntoEscritura=" + conjuntoEscritura
				+ ", mensajesLog=" + mensajesLog + ", numTransaccion=" + numTransaccion + ", estado=" + estado + "]";
	}


	public synchronized List<String> getMensajesLog() {
		return mensajesLog;
	}


	public synchronized void setMensajesLog(List<String> mensajesLog) {
		this.mensajesLog = mensajesLog;
	}


	public synchronized int getEstado() {
		return estado;
	}

	public synchronized void setEstado(int estado) {
		this.estado = estado;
	}

	public synchronized  void adicionarObjetoLectura(Object producto){
		conjuntoLectura.add(producto);
		
	}
	public synchronized void adicionarObjetoEscritura(Object producto){
		conjuntoEscritura.add(producto);
		
	}
	
	public synchronized List<Object> getConjuntoLectura() {
		return conjuntoLectura;
	}


	public synchronized void setConjuntoLectura(List<Object> conjuntoLectura) {
		this.conjuntoLectura = conjuntoLectura;
	}


	public synchronized List<Object> getConjuntoEscritura() {
		return conjuntoEscritura;
	}


	public synchronized void setConjuntoEscritura(List<Object> conjuntoEscritura) {
		this.conjuntoEscritura = conjuntoEscritura;
	}


	public synchronized int getNumTransaccion() {
		return numTransaccion;
	}


	public synchronized void setNumTransaccion(int numTransaccion) {
		this.numTransaccion = numTransaccion;
	}
	
	
	

}
