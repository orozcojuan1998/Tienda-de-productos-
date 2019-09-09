package Cuenta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Cliente.Transaccion;
import Producto.Producto;

public class PiedraCuentas implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Cuenta> cuentas;
	private List<Transaccion> transaccionesActivas;
	private int numSecuencia = 0;
	
	public PiedraCuentas() {
		cuentas = new ArrayList<Cuenta>();
		transaccionesActivas = new ArrayList<Transaccion>();
	}

	public List<Cuenta> getCuentas() {
		return cuentas;
	}

	public void setCuentas(List<Cuenta> cuentas) {
		this.cuentas = cuentas;
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
