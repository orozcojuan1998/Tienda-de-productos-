package Coordinador;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import Cliente.Participante;
import Cliente.ParticipanteInterface;
import Cliente.ProductoCarrito;
import Cliente.Transaccion;
import Cuenta.CuentaRMII;
import Producto.ProductoRMII;

public class Coordinador extends UnicastRemoteObject implements CoordinadorInterface{

	
	private final String name = "Coordinador";
	private final int port = 3000;
	private final String ipLocal = "//127.0.0.1/Coordinador";
	private int numSecuencia = 0;
	private int numSecuenciaParticipantes = 0;
	private Registry registry;
	private int portCuentas = 3001;
	private int portProductos = 3002;
	private CuentaRMII j;
	private ProductoRMII i;
	private CoordinadorInterface coord;
	private ArrayList<TransaccionCoordinador> transacciones;
	private ArrayList<Participante> participantes;
	
	public Coordinador() throws RemoteException {
		super();
		transacciones = new ArrayList<>();
		 System.setProperty("java.security.policy", "./coordinador.policy");
		try {
			registry = LocateRegistry.createRegistry(port);
			registry.rebind(ipLocal, this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		j = null;
		try {
			registry = LocateRegistry.getRegistry(portCuentas);
			j = (CuentaRMII) registry.lookup("//127.0.0.1/Cuentas");
		}  catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		i = null;
		try {
			registry = LocateRegistry.getRegistry(portProductos);
			i = (ProductoRMII) registry.lookup("//127.0.0.1/Productos");
		}  catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		
		try {
			Coordinador coordinador = new Coordinador();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	@Override
	public void vote(String abortOrCommit, int participantNum) throws RemoteException {
		if(abortOrCommit.equals("doCommit")){
			for (Participante participante : participantes) {
				if(participante.getNum()==participantNum){
					participante.setEstado(4);
				}
			}
		}else if(abortOrCommit.equals("abort")){
			for (Participante participante : participantes) {
				if(participante.getNum()==participantNum){
					participante.setEstado(3);
				}
			}
		}
		else if(abortOrCommit.equals("prepareCommit")){
			for (Participante participante : participantes) {
				if(participante.getNum()==participantNum){
					participante.setEstado(2);
				}
			}
		}
	}
	@Override
	public int ack(String abortOrCommit, int participantNum) throws RemoteException {
		
		for (Participante participante : participantes) {
			if(participante.getNum()==participantNum){
				return participante.getEstado();
			}
		}
		return 0;
	}
	@Override
	public void addParticipant(Participante participant, int participantNum) throws RemoteException {
		participantes.add(participant);
	}
	
}
