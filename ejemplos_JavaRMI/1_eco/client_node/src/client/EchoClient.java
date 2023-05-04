package client;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import interfaces.EchoService;

// Cliente del servidor de eco
class EchoClient {
    static public void main (String args[]) {
        if (args.length<3) {
            System.err.println("Usage: EchoClient registryHost registryPort word...");
            return;
        }
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try {
            // localiza el registry en la máquina y puerto especificados
            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            // obtiene una referencia remota el servicio
            EchoService srv = (EchoService) registry.lookup("Echo");
    
            // usa el servicio
            for (int i=2; i<args.length; i++)
                System.out.println(srv.echo(args[i]));
        }
        catch (Exception e) {
            System.err.println("EchoClient exception:");
            e.printStackTrace();
        }
    }
}
