package Cuenta;

import java.io.Serializable;

public class Cuenta implements Serializable{
	public String usuario;
	public String contrasena;
	public String tarjeta;
	public float saldo;
	
	
	
	public Cuenta() {
		super();
	}
	
	public Cuenta(String usuario, String contrasena, String tarjeta, float saldo) {
		super();
		this.usuario = usuario;
		this.contrasena = contrasena;
		this.tarjeta = tarjeta;
		this.saldo = saldo;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getContrasena() {
		return contrasena;
	}
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	public String getTarjeta() {
		return tarjeta;
	}
	public void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
	}
	public float getSaldo() {
		return saldo;
	}
	public void setSaldo(float saldo) {
		this.saldo = saldo;
	}
	@Override
	public String toString() {
		return "Cuenta [usuario=" + usuario + ", contrasena=" + contrasena + ", tarjeta=" + tarjeta + ", saldo=" + saldo
				+ "]";
	}
	
}
