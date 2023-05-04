// Interfaz remota Tracker
// NO MODIFICAR

package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Tracker extends Remote {
    // solo para depurar
    String getName() throws RemoteException;

    // se publica un fichero
    boolean announceFile (Seed publisher, String name, int blockSize, int numBlocks) throws RemoteException;

    // obtiene acceso a la metainformación de un fichero
    public FileInfo lookupFile(String name) throws RemoteException;

    // se añade un nuevo leech
    public boolean addLeech(Leech leech, String name) throws RemoteException;
}
