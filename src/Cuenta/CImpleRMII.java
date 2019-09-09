package Cuenta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Cliente.Cliente;
import Cliente.Participante;
import Cliente.ProductoCarrito;
import Cliente.Transaccion;
import Coordinador.CoordinadorInterface;
import Producto.PiedraProductos;
import Producto.Producto;
import Cliente.Transaccion;

public class CImpleRMII extends UnicastRemoteObject implements CuentaRMII {

	private List<Transaccion> transaccionesActivas;
	private int portCoordinador = 3000;
	private CoordinadorInterface coordinador;
	private int numSecuencia = 0;
	
	public CImpleRMII() throws RemoteException, NotBoundException {
		super();
		// TODO Auto-generated constructor stub
		indexTX_user = 0;
		transaccionesActivas = new ArrayList<Transaccion>();
		System.setProperty("java.security.policy", "./cliente.policy");
		//Registry registry = LocateRegistry.getRegistry(portCoordinador);


		//coordinador = (CoordinadorInterface) registry.lookup("//127.0.0.1/Coordinador");

		boolean persistencia = intentarLeerPiedra();
		if(!persistencia){
			leerCuentas("iniciocuentas.txt");
		}
		
		
		imprimirCuentas();
	}

	private List<Cuenta> cuentas = new ArrayList<Cuenta>(); 
	private static final long serialVersionUID = 1L;
    Map<String, String> card_value = new HashMap<String, String>();
    
    
    
    
    
    int indexTX_user ;

	@Override
	public String saludar(String x) throws RemoteException {
		
		
		
		// TODO Auto-generated method stub
		return "Hola "+x+" desde cuenta";
	}
	
	public void leerCuentas(String x) throws RemoteException{
	try {
			FileReader fr = new FileReader(x);
			BufferedReader br = new BufferedReader(fr);
			String linea;
			linea = br.readLine();
			linea = br.readLine();
			int nCuentas=Integer.parseInt(linea);
			for(int i=0;i<nCuentas;i++) {
				linea = br.readLine();
				String[] parts = linea.split(",");
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] encodedhash = digest.digest(parts[0].getBytes(StandardCharsets.UTF_8));
	            String hash = bytesToHex(encodedhash);
				Cuenta cuenta= new Cuenta(parts[2],"",hash, Float.parseFloat(parts[1]));
				this.cuentas.add(cuenta);
			}
			imprimirCuentas();
			br.close();
		}catch(Exception e) {
			System.out.println(e);
		}	
	}

	private static String bytesToHex(byte[] hash) {
		  StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
        }
        return hexString.toString();
    }

	@Override
	public void imprimirCuentas() throws RemoteException {
		// TODO Auto-generated method stub
		for (Cuenta cuenta : cuentas) {
			System.out.println(cuenta.toString());
		}
	}

	@Override
	public List<Cuenta> getCuentas() throws RemoteException {
		return this.cuentas;
	}

	@Override
	public boolean autenticarUsuario(String c,String t) throws RemoteException {
		for (Cuenta cuenta : cuentas) {
			MessageDigest digest;
			try {
				digest = MessageDigest.getInstance("SHA-256");
	            byte[] encodedhash = digest.digest(c.getBytes(StandardCharsets.UTF_8));
	            String hash = bytesToHex(encodedhash);
	            MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
	            byte[] encodedhash2 = digest2.digest(t.getBytes(StandardCharsets.UTF_8));
	            String hash2 = bytesToHex(encodedhash2);
				System.out.println(hash+"--"+hash2);
				System.out.println(cuenta.getContrasena()+"--"+cuenta.getTarjeta());
	            if(cuenta.getContrasena().equals(hash)&&cuenta.getTarjeta().equals(hash2)) {
					return true;
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
		}
		return false;
	}

	
 
    
    public boolean validarForward(Transaccion tv) throws RemoteException{
    	boolean valida = true;
    	
    	for(int i= 0; i < transaccionesActivas.size(); i++){
    		List<Object> conjuntoEscrituraTV = tv.getConjuntoEscritura();
    		List<Object> conjuntoLecturaActual = transaccionesActivas.get(i).getConjuntoLectura();
    		
    		for (Object cuentaTV : conjuntoEscrituraTV) {
				for (Object cuentaA : conjuntoLecturaActual) {
					Cuenta cuentaT = (Cuenta) cuentaTV;
					Cuenta cuentaAA = (Cuenta) cuentaA;
					if(cuentaAA.getTarjeta().equals(cuentaT.getTarjeta())){
						return false;
					}
				}
			}
    		
    		
    	}
    	
    	return valida;
    }

	@Override
	public synchronized Cuenta getCuenta(String tarjeta)throws RemoteException  {
		try {
		MessageDigest digest;
		digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedhash = digest.digest(tarjeta.getBytes(StandardCharsets.UTF_8));
        String hash = bytesToHex(encodedhash);
		for (Cuenta cuenta : cuentas) {
				if(hash.equals(cuenta.getTarjeta())){
						return cuenta;
					}
			} 
           
		}catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public float getSaldo(String tarjeta) throws RemoteException {
		for (Cuenta cuenta : cuentas) {
			MessageDigest digest;
			try {
				digest = MessageDigest.getInstance("SHA-256");
				 byte[] encodedhash = digest.digest(tarjeta.getBytes(StandardCharsets.UTF_8));
		            String hash = bytesToHex(encodedhash);
					if(hash.equals(cuenta.getTarjeta())){
						return cuenta.getSaldo();
					}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           
		}
		return 0;
	}

	@Override
	public void setSaldo(String tarjeta, float f) throws RemoteException {
		for (Cuenta cuenta : cuentas) {
			MessageDigest digest;
			try {
				digest = MessageDigest.getInstance("SHA-256");
				 byte[] encodedhash = digest.digest(tarjeta.getBytes(StandardCharsets.UTF_8));
		            String hash = bytesToHex(encodedhash);
					if(hash.equals(cuenta.getTarjeta())){
						 cuenta.setSaldo(f);
					}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           
		}
	
		
	}

	@Override
	public Transaccion iniciarTransaccion(Transaccion tv) throws RemoteException {
		tv.setNumTransaccion(numSecuencia++);
		tv.setEstado(1);
		System.out.println("estado transacción: "+tv.getEstado());
		if(validarForward(tv)){
			tv.setEstado(2);
			transaccionesActivas.add(tv);
			escribirEnPiedra();
		}else{
			tv.setEstado(3);
		}
		System.out.println("estado transacción: "+tv.getEstado());
		return tv;
	}



	@Override
	public void finalizarTransaccion(Transaccion tv) throws RemoteException {
		for (int i =0 ; i < transaccionesActivas.size(); i++) {
			if(tv.getNumTransaccion()==transaccionesActivas.get(i).getNumTransaccion()){
				transaccionesActivas.remove(i);
				if(tv.getEstado()==2){
					escribirEnPiedra();
				}
				tv.setEstado(4);
			}
		}
	
		System.out.println("estado transacción: "+tv.getEstado()+ " no. "+ tv.getNumTransaccion());
	}

	@Override
	public Transaccion solicitarTransaccion() throws RemoteException {
		return new Transaccion();
	}

	@Override
	public boolean verificarRegistro(String tarjeta) throws RemoteException {
		for (Cuenta cuenta : cuentas) {
			MessageDigest digest;
			try {
					digest = MessageDigest.getInstance("SHA-256");
					byte[] encodedhash = digest.digest(tarjeta.getBytes(StandardCharsets.UTF_8));
		            String hash = bytesToHex(encodedhash);
					if(hash.equals(cuenta.getTarjeta())&&cuenta.getContrasena().equals("")){
						return true;
					}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void setContrasena(String tarjeta, String contra) throws RemoteException {
		try {
			MessageDigest digest,digest2;
			digest = MessageDigest.getInstance("SHA-256");
			digest2 = MessageDigest.getInstance("SHA-256");
			 byte[] encodedhash = digest.digest(tarjeta.getBytes(StandardCharsets.UTF_8));
			 byte[] encodedhash2 = digest.digest(contra.getBytes(StandardCharsets.UTF_8));
	            String hash = bytesToHex(encodedhash);
	            String hash2 = bytesToHex(encodedhash2);
	            System.out.println("TARJETA HASH "+hash);
	            for (Cuenta cuenta : cuentas) {
	    			if(hash.equals(cuenta.getTarjeta())){
	    				
	    				cuenta.setContrasena(hash2);
	    			}
	    		}
				
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	@Override
	public void recargarTarjeta(String tarj, String saldo) throws RemoteException {
		try {
			MessageDigest digest,digest2;
			digest = MessageDigest.getInstance("SHA-256");
			 byte[] encodedhash = digest.digest(tarj.getBytes(StandardCharsets.UTF_8));
	            String hash = bytesToHex(encodedhash);
	            for (Cuenta cuenta : cuentas) {
	    			if(hash.equals(cuenta.getTarjeta())){
	    				cuenta.setSaldo(cuenta.getSaldo()+Float.parseFloat(saldo));
	    			}
	    		}
				
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void escribirEnPiedra(){
		File fichero = new File("/PersistenciaServidorCuentas");
		
		PiedraCuentas piedra = new PiedraCuentas();
		piedra.setCuentas(cuentas);
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
		File fichero = new File("/PersistenciaServidorCuentas");
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(fichero));
			PiedraCuentas p =(PiedraCuentas) ois.readObject();
			if(p!=null){
				setNumSecuencia(p.getNumSecuencia());
				setCuentas(p.getCuentas());
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

	public void setCuentas(List<Cuenta> cuentas) {
		this.cuentas = cuentas;
	}
	
	

	

	
    
}
