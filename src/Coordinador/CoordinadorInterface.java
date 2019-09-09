package Coordinador;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import Cliente.Participante;
import Cliente.ParticipanteInterface;
import Cliente.ProductoCarrito;

public interface CoordinadorInterface extends Remote{
	public void vote(String abortOrCommit, int participantNum) throws RemoteException;
	public int ack(String abortOrCommit, int participantNum) throws RemoteException;
	public void addParticipant(Participante participant, int participantNum) throws RemoteException;
	
	
}
