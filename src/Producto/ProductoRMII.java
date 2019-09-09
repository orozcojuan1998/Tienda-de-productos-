package Producto;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import Cliente.Transaccion;
import Cuenta.Cuenta;

public interface ProductoRMII extends Remote{
	public String saludar(String x) throws RemoteException;
	public void leerProductos(String x) throws RemoteException;
	public void imprimirProductos() throws RemoteException;
	public ArrayList<Producto> getProductos() throws RemoteException;
	public Producto getProducto(int ID) throws RemoteException;
	
	public Transaccion iniciarTransaccion(Transaccion tv) throws RemoteException;
	public  void finalizarTransaccion(Transaccion tv)throws RemoteException;
	public Transaccion solicitarTransaccion()throws RemoteException;
	public void disminuirCantidadDisponible(int id, int cantidad) throws RemoteException;
	public void agregarProducto(Producto p) throws RemoteException;
	public Producto getProductoNombre(String nombre) throws RemoteException;
	public void aumentarCantidadProducto(int id, int parseInt)throws RemoteException;

}
