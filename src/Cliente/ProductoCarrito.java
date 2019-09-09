package Cliente;

import java.io.Serializable;

import Producto.Producto;

public class ProductoCarrito implements Serializable {

	
	private Producto p;
	private int cantidad;
	public ProductoCarrito(Producto p, int cantidad) {
		super();
		this.p = p;
		this.cantidad = cantidad;
	}
	public Producto getP() {
		return p;
	}
	public void setP(Producto p) {
		this.p = p;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	
	

}
