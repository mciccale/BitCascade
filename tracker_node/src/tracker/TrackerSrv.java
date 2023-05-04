// Clase que implementa la interfaz remota Tracker
// Actúa como un servidor que gestiona la información de los ficheros publicados
// TODA LA FUNCIONALIDAD DE ESTA CLASE SE COMPLETA EN LA FASE 1 (TODO 1)
// EXCEPTO AÑADIR LEECHES QUE SE INCLUYE EN LA FASE 3 (TODO 3)
//

package tracker;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import interfaces.Tracker;
import interfaces.Seed;
import interfaces.Leech;
import interfaces.FileInfo;

// guarda la información de los ficheros publicados asociando el nombre
// del fichero con un objeto de la clase FileInfo que contiene esa información
// usando para ello algún tipo de mapa (p.e. HashMap)
class TrackerSrv extends UnicastRemoteObject implements Tracker  {
    public static final long serialVersionUID=1234567890L;
    String name;
    // TODO 1: añadir los campos que se requieran
    
    public TrackerSrv(String n) throws RemoteException {
        name = n;
        // TODO 1: inicializar campos adicionales
    }
    // NO MODIFICAR: solo para depurar
    public String getName() throws RemoteException {
        return name;
    }
    // se publica fichero: debe ser sincronizado para asegurar exclusión mutua;
    // devuelve falso si ya estaba publicado un fichero con el mismo nombre
    public synchronized boolean announceFile(Seed publisher, String fileName, int blockSize, int numBlocks) throws RemoteException {
        // TODO 1: se crea un objeto FileInfo con la información del fichero
	// y se inserta en el mapa
        System.out.println(publisher.getName() + " ha publicado " + fileName);
        return false;
    }
    // TODO 1: obtiene acceso a la metainformación de un fichero
    public synchronized FileInfo lookupFile(String fileName) throws RemoteException {
        return null;
    }
    // TODO 3: se añade un nuevo leech a ese fichero (tercera fase)
    public boolean addLeech(Leech leech, String fileName) throws RemoteException {
        return false;
    }
    static public void main (String args[])  {
        if (args.length!=2) {
            System.err.println("Usage: TrackerSrv registryPortNumber name");
            return;
        }
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try {
            TrackerSrv srv = new TrackerSrv(args[1]);
            // TODO 1: localiza el registry en el puerto recibido en args[0]]
	    // y da de alta el servicio bajo el nombre "BitCascade"
        }
        catch (Exception e) {
            System.err.println("Tracker exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
