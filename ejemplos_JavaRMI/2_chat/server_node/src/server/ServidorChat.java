package server;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import interfaces.ServicioChat;
import interfaces.Cliente;

class ServidorChat extends UnicastRemoteObject implements ServicioChat {
    public static final long serialVersionUID=1234567890L;
    LinkedList<Cliente> l;
    ServidorChat() throws RemoteException {
        l = new LinkedList<Cliente>();
    }
    public void alta(Cliente c) throws RemoteException {
        l.add(c);
    }
    public void baja(Cliente c) throws RemoteException {
        l.remove(l.indexOf(c));
    }
    public void envio(Cliente esc, String apodo, String m)
      throws RemoteException {
        for (Cliente c: l) 
            if (!c.equals(esc))
                c.notificacion(apodo, m);
    }
    static public void main (String args[]) {
       if (args.length!=1) {
            System.err.println("Uso: ServidorChat numPuertoRegistro");
            return;
        }
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            ServidorChat srv = new ServidorChat();
            Registry registry = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
            registry.rebind("Chat", srv);
        }
        catch (Exception e) {
            System.err.println("Excepcion en ServidorChat:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
