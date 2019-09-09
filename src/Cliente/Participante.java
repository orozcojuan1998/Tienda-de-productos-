package Cliente;

import java.rmi.RemoteException;

import Coordinador.Coordinador;
import Coordinador.CoordinadorInterface;

public class Participante implements ParticipanteInterface {

	private Transaccion tv;
	private int num;
	private int estado;
	private Coordinador coordinador;
	
	public Participante(Transaccion tv, CoordinadorInterface coordinador, int num) throws RemoteException {	
		estado = -1;
		this.tv = tv;
		this.num = num;
		coordinador.addParticipant(this, 0);
	}

	public Transaccion getTv() {
		return tv;
	}

	public void setTv(Transaccion tv) {
		this.tv = tv;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public synchronized int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	@Override
	public void prepareCommit() throws RemoteException {
		estado = 1;
		coordinador.vote("prepareCommit", num);
	}

	@Override
	public void doAbort() throws RemoteException {
		coordinador.vote("abort", num);
		
	}

	@Override
	public void doCommit() throws RemoteException {
		coordinador.vote("doCommit", num);
	}


}
