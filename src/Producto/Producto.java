package Producto;

import java.io.Serializable;

public class Producto implements Serializable{
	
	
	private int ID;
	private String nombre;
	private int cantidadDisponible;
	private float precio;
	
	public Producto(int ID, String nombre, int cantidadDisponible, float precio) {
		super();
		this.ID = ID;
		this.nombre = nombre;
		this.cantidadDisponible = cantidadDisponible;
		this.precio = precio;
	}
	
	
	public int getID() {
		return ID;
	}


	public void setID(int iD) {
		ID = iD;
	}


	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getCantidadDisponible() {
		return cantidadDisponible;
	}
	public void setCantidadDisponible(int cantidadDisponible) {
		this.cantidadDisponible = cantidadDisponible;
	}
	public float getPrecio() {
		return precio;
	}
	public void setPrecio(float precio) {
		this.precio = precio;
	}
	@Override
	public String toString() {
		return "Producto [nombre=" + nombre + ", cantidadDisponible=" + cantidadDisponible + ", precio=" + precio + "]";
	}
	
}
