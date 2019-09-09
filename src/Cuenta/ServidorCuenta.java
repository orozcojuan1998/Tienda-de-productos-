package Cuenta;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServidorCuenta {
	public ServidorCuenta() throws RemoteException, MalformedURLException, NotBoundException {
		CuentaRMII i = new CImpleRMII();
		System.setProperty("java.security.policy", "./cliente.policy");
		
		 
			try {
				//Registry registry = LocateRegistry.createRegistry(3001);
				//registry.rebind("//10.192.101.31/Cuentas", i);
				//System.setProperty("java.rmi.server.hostname","10.192.101.31");
				LocateRegistry.createRegistry(3001);
				Naming.rebind("rmi://localhost:3001/Cuentas",i);
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	public static void main(String args[]) throws RemoteException, MalformedURLException, NotBoundException{
		new ServidorCuenta();
	}
}
