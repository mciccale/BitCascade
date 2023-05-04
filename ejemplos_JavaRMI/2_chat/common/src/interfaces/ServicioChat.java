package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioChat extends Remote {
    void alta(Cliente c) throws RemoteException;
    void baja(Cliente c) throws RemoteException;
    void envio(Cliente e, String apodo, String m) throws RemoteException;
}
