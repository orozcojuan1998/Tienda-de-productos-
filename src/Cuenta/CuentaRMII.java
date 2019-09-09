package Cuenta;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import Cliente.ProductoCarrito;
import Cliente.Transaccion;

public interface CuentaRMII extends Remote{
	public String saludar(String x) throws RemoteException;
	public void leerCuentas(String x) throws RemoteException;
	public void imprimirCuentas() throws RemoteException;
	public List<Cuenta> getCuentas() throws RemoteException;
	public boolean autenticarUsuario(String c,String t)throws RemoteException;

    public boolean verificarRegistro(String tarjeta) throws RemoteException;
	public Cuenta getCuenta(String tarjeta)throws RemoteException;
	public float getSaldo(String tarjeta)throws RemoteException;
	public void setSaldo(String tarjeta, float f)throws RemoteException;
	public void setContrasena(String tarjeta,String contra)throws RemoteException;
	public Transaccion iniciarTransaccion(Transaccion tv) throws RemoteException;
	public void finalizarTransaccion(Transaccion tv)throws RemoteException;
	public Transaccion solicitarTransaccion() throws RemoteException;
	public void recargarTarjeta(String tarj,String saldo) throws RemoteException;
    
   
    

}
