package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Cliente extends Remote {
    void notificacion(String apodo, String m) throws RemoteException;
}
