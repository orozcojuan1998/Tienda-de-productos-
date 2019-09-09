package Producto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Cliente.Transaccion;

public class PiedraProductos implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Producto> productos;
	private List<Transaccion> transaccionesActivas;
	private int numSecuencia = 0;
	
	public PiedraProductos() {
		productos = new ArrayList<>();
		transaccionesActivas = new ArrayList<>();
	}

	public List<Producto> getProductos() {
		return productos;
	}

	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}

	public List<Transaccion> getTransaccionesActivas() {
		return transaccionesActivas;
	}

	public void setTransaccionesActivas(List<Transaccion> transaccionesActivas) {
		this.transaccionesActivas = transaccionesActivas;
	}

	public int getNumSecuencia() {
		return numSecuencia;
	}

	public void setNumSecuencia(int numSecuencia) {
		this.numSecuencia = numSecuencia;
	}

}
