package Producto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import Cliente.Transaccion;
import Coordinador.CoordinadorInterface;
import Cuenta.Cuenta;




public class PImpleRMII extends UnicastRemoteObject implements ProductoRMII {

	private List<Transaccion> transaccionesActivas;
	private int portCoordinador = 3000;
	private CoordinadorInterface coordinador;
	private int numSecuencia = 0;
	
	
	public PImpleRMII() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		
		
		
		transaccionesActivas = Collections.synchronizedList(new ArrayList<>());;
		boolean persistencia = intentarLeerPiedra();
		if(!persistencia){
			leerProductos("inicioproductos.txt");
		}
		
		imprimirProductos();	
	}

	private static final long serialVersionUID = 1L;
	private List<Producto> productos = new ArrayList<Producto>(); 

	@Override
	public String saludar(String x) throws RemoteException {
		// TODO Auto-generated method stub
		return "Hola "+x+" desde producto";
	}

	@Override
	public void leerProductos(String x) throws RemoteException {
		// TODO Auto-generated method stub
		
		
		String linea;
		try {
			FileReader fr = new FileReader(x);
			BufferedReader br = new BufferedReader(fr);
			linea = br.readLine();
			linea = br.readLine();
			int nProductos=Integer.parseInt(linea);
			for(int i=0;i<nProductos;i++) {
				linea = br.readLine();
				String[] parts = linea.split(",");
				Producto producto= new Producto(Integer.parseInt(parts[0]),parts[1],Integer.parseInt(parts[3]), Float.parseFloat(parts[2]));		
				this.productos.add(producto);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void imprimirProductos() throws RemoteException {
		// TODO Auto-generated method stub
		for (Producto producto : this.productos) {
			System.out.println(producto.toString());
		}
	}

	@Override
	public synchronized  ArrayList<Producto> getProductos() throws RemoteException {
		ArrayList<Producto> productosTemporales = new ArrayList<>();
		for (Producto producto : productos) {
			productosTemporales.add(new Producto(producto.getID(), producto.getNombre(), producto.getCantidadDisponible(), producto.getPrecio()));
		}
		
		return productosTemporales;
	}
	
	

	@Override
	public synchronized  Transaccion iniciarTransaccion(Transaccion tv) throws RemoteException {
		tv.setNumTransaccion(numSecuencia++);
		tv.setEstado(1);
		System.out.println("Estado transacción: "+tv.getEstado());
		if(validarForward(tv)){
			tv.setEstado(2);
			transaccionesActivas.add(tv);
			escribirEnPiedra();
		}else{
			tv.setEstado(3);
		}
		System.out.println("Estado transacción: "+tv.getEstado());
		return tv;
	}

	@Override
	public synchronized  void finalizarTransaccion(Transaccion tv) throws RemoteException {
		
		

		for (int i =0 ; i < transaccionesActivas.size(); i++) {
			if(tv.getNumTransaccion()==transaccionesActivas.get(i).getNumTransaccion()){
				transaccionesActivas.remove(i);
				if(tv.getEstado()==2){
					escribirEnPiedra();
				}
				tv.setEstado(4);
			}
		}
	
		
		System.out.println("Estado transacción: "+tv.getEstado()+ " no. "+ tv.getNumTransaccion());
	}
	
	public synchronized List<Transaccion> getTransaccionesActivas() {
		return transaccionesActivas;
	}

	public synchronized void setTransaccionesActivas(ArrayList<Transaccion> transaccionesActivas) {
		this.transaccionesActivas = transaccionesActivas;
	}

	@Override
	public Transaccion solicitarTransaccion() throws RemoteException {
		return new Transaccion();
	}
	 public synchronized  boolean validarForward(Transaccion tv) throws RemoteException{
	    	boolean valida = true;
	    	
	    	for(int i= 0; i < getTransaccionesActivas().size(); i++){
	    		List<Object> conjuntoEscrituraTV = tv.getConjuntoEscritura();
	    		List<Object> conjuntoLecturaActual = getTransaccionesActivas().get(i).getConjuntoLectura();
	    		
	    		for (Object cuentaTV : conjuntoEscrituraTV) {
					for (Object cuentaA : conjuntoLecturaActual) {
						Producto p = (Producto)cuentaTV;
						Producto p2 = (Producto) cuentaA;
						if(p.getID()==p2.getID()){
							return false;
						}
					}
				}
	    		
	    		
	    	}
	    	
	    	return valida;
	    }

	@Override
	public Producto getProducto(int ID) throws RemoteException {
		for (Producto producto : productos) {
			if(producto.getID()==ID){
				return producto;
			}
		}
		return null;
	}

	@Override
	public void disminuirCantidadDisponible(int id, int cantidad) throws RemoteException {
		for (Producto producto : productos) {
			if(producto.getID()==id){
				producto.setCantidadDisponible(producto.getCantidadDisponible()-cantidad);
			}
		}
	}
	@Override
	public void agregarProducto(Producto p) {
		productos.add(p);
	}

	@Override
	public Producto getProductoNombre(String nombre) {
		for (Producto producto : productos) {
			if(producto.getNombre().equals(nombre)){
				return producto;
			}
		}
		return null;
	}

	@Override
	public void aumentarCantidadProducto(int id, int parseInt) throws RemoteException {
		
		Producto p = getProducto(id);
		p.setCantidadDisponible(p.getCantidadDisponible()+parseInt);
		
	}
	
	public void escribirEnPiedra(){
		File fichero = new File("/PersistenciaServidorProductos");
		
		PiedraProductos piedra = new PiedraProductos();
		piedra.setProductos(productos);
		piedra.setTransaccionesActivas(transaccionesActivas);
		piedra.setNumSecuencia(numSecuencia);
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichero));
			oos.writeObject(piedra);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public boolean intentarLeerPiedra(){
		File fichero = new File("/PersistenciaServidorProductos");
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(fichero));
			PiedraProductos p =(PiedraProductos) ois.readObject();
			if(p!=null){
				setNumSecuencia(p.getNumSecuencia());
				setProductos(p.getProductos());
				setTransaccionesActivas(p.getTransaccionesActivas());
				
			}else{
				ois.close();
				return false;
			}
			ois.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			try {
				ois.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public int getNumSecuencia() {
		return numSecuencia;
	}

	public void setNumSecuencia(int numSecuencia) {
		this.numSecuencia = numSecuencia;
	}

	public void setTransaccionesActivas(List<Transaccion> transaccionesActivas) {
		this.transaccionesActivas = transaccionesActivas;
	}

	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}
	
	
}
