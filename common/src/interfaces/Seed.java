// Interfaz remota Seed
// NO MODIFICAR

package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Seed extends Remote {
    // solo para depurar
    public String getName() throws RemoteException;

    // petición de lectura del bloque indicado
    public byte [] read(int numBl) throws RemoteException;
}
