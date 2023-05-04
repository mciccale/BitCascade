package client;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import interfaces.ServicioChat;
import interfaces.Cliente;

class ClienteChat extends UnicastRemoteObject implements Cliente {
    public ClienteChat() throws RemoteException {
    }
    public static final long serialVersionUID=1234567890L;
    public void notificacion(String apodo, String m) throws RemoteException {
	System.out.println("\n" + apodo + "> " + m);
    }
    static public void main (String args[]) {
        if (args.length!=3) {
            System.err.println("Uso: ClienteChat hostregistro numPuertoRegistro apodo");
            return;
        }

       if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        try {
            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            ServicioChat srv = (ServicioChat) registry.lookup("Chat");

            ClienteChat c = new ClienteChat();
            srv.alta(c);
            Scanner ent = new Scanner(System.in);
            String apodo = args[2];
            System.out.print(apodo + "> ");
            while (ent.hasNextLine()) {
                srv.envio(c, apodo, ent.nextLine());
                System.out.print(apodo + "> ");
            }
            srv.baja(c);
            System.exit(0);
        }
        catch (Exception e) {
            System.err.println("Excepcion en ClienteChat:");
            e.printStackTrace();
        }
    }
}
