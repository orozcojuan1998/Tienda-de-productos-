package Cliente;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Coordinador.CoordinadorInterface;

public interface ParticipanteInterface extends Remote {

	public void prepareCommit() throws RemoteException;
	public void doAbort() throws RemoteException;
	public void doCommit() throws RemoteException;
	
	
}
