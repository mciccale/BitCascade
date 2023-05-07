// Clase que implementa la interfaz remota Seed.
// Actúa como un servidor que ofrece un método remoto para leer los bloques
// del fichero que se está descargando.
// Proporciona un método estático (init) para instanciarla.
// LA FUNCIONALIDAD SE COMPLETA EN LAS 4 FASES TODO 1, TODO 2, TODO 3 y TODO 4
// En las fases 3 y 4 se convertirá en un objeto remoto

package peers;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.rmi.RemoteException;

import interfaces.Seed;
import interfaces.Leech;
import interfaces.Tracker;
import interfaces.FileInfo;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileWriter;

// Un Leech guarda en esta clase auxiliar su conocimiento sobre cuál es el
// último bloque descargado por otro Leech.
class LeechInfo {
    Leech leech;    // de qué Leech se trata
    int lastBlock;  // último bloque que sabemos que se ha descargado
    public LeechInfo (Leech l, int nBl) {
        leech = l;
        lastBlock = nBl;
    }
    Leech getLeech() {
        return leech;
    }
    int getLastBlock() {
        return lastBlock;
    }
    void setLastBlock(int nBl) {
        lastBlock = nBl;
    }
};
// Esta clase actúa solo de cliente en las dos primeras fases, pero
// en las dos últimas ejerce también como servidor convirtiéndose en
// un objeto remoto.
public class DownloaderImpl extends UnicastRemoteObject implements Leech { 
    public static final long serialVersionUID=1234567890L;
    String name; // nombre del nodo (solo para depurar)
    String file;
    String path;
    int blockSize;
    int numBlocks;
    int lastBlock = -1; // último bloque descargado por este Leech
    int lastIndex;
    int fileSize;
    Seed seed;
    FileInfo fInfo;
    ArrayList<LeechInfo> leechList;
    ConcurrentLinkedQueue<Leech> postLeeches;
    Map<Leech, LeechInfo> leechMap;
    transient RandomAccessFile fd;

    public DownloaderImpl(String n, String f, FileInfo finf) throws RemoteException, IOException {
        name = n;
        file = f;
        path = name + "/" + file;
        blockSize = finf.getBlockSize();
        numBlocks = finf.getNumBlocks();
        lastIndex = 0;
        seed = finf.getSeed();
        fInfo = finf;
        leechList = new ArrayList<>();
        postLeeches = new ConcurrentLinkedQueue<>();
        fd = new RandomAccessFile(path, "rw");
        fileSize = (int) fd.length();
        fd.setLength(0);
        // obtiene el número del último bloque descargado por leeches
	    // anteriores (contenidos en FileInfo) usando getLastBlockNumber
        for (Leech l : fInfo.getLeechList()) {
            leechList.add(new LeechInfo(l, l.getLastBlockNumber()));
        }
        // se inicializa el mapa
        leechMap = leechList.stream().collect(Collectors.toMap(LeechInfo::getLeech, Function.identity()));
        // solicita a esos leeches anteriores usando newLeech
        // que le notifiquen cuando progrese su descarga
        for (LeechInfo inf : leechList) {
            inf.getLeech().newLeech(this);
        }
    }
    /* métodos locales */
    public int getNumBlocks() {
        return numBlocks;
    }
    public FileInfo getFileInfo() {
        return fInfo;
    }
    // realiza la descarga de un bloque y lo almacena en un fichero local
    public boolean downloadBlock(int numBl) throws RemoteException {
        try {
            Leech leech = null;
            boolean downloaded = false;
            byte [] buf = new byte [blockSize];
            // se lleva el puntero a la posición deseada
            fd.seek(numBl * blockSize);
            // primero se comprueba si se puede descargar de algún leech
            for (int i = lastIndex; !downloaded && i < leechList.size(); ++i) {
                // se puede descargar de este leech
                if (leechMap.get(leechList.get(i).getLeech()).getLastBlock() >= numBl) {
                    System.out.println("Se entra aquí");
                    leech = leechList.get(i).getLeech();
                    if ((buf = leech.read(numBl)) != null) {
                        fd.write(buf);
                        downloaded = true;
                        lastIndex = i + 1;
                        lastBlock = numBl;
                        // notifica a los leeches posteriores
                        for (Leech l : postLeeches) {
                            l.notifyBlock(this, numBl);
                        }
                        return true;
                    }
                }
            }
            // se comprueba que la lectura haya tenido éxito
            if ((buf = seed.read(numBl)) != null) {
                fd.write(buf);
                lastBlock = numBl;
                lastIndex = 0;
                // notifica a los leeches posteriores
                for (Leech l : postLeeches) {
                    l.notifyBlock(this, numBl);
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* métodos remotos que solo se usarán cuando se convierta en
       un objeto remoto en la fase 3 */
 
    // solo para depurar
    public String getName() throws RemoteException {
        return name;
    }
    // prácticamente igual que read del Seed
    public byte [] read(int numBl) throws RemoteException {
        byte [] buf = null;
        System.out.println("downloader "+ name + " read " + numBl);

        // se asegura de que el bloque solicitado existe
        if (numBl < numBlocks) {
            try {
                int bufSize = blockSize;
                // último bloque solicitado
                if (numBl + 1 == numBlocks) {
                    int newSize = (int) (fileSize % blockSize);
                    if (newSize > 0) bufSize = newSize;
                }
                // se lee el archivo
                buf = new byte[bufSize];
                fd.seek(numBl * blockSize);
                int n = fd.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buf;
    } 
    // obtiene cuál es el último bloque descargado por este Leech
    public int getLastBlockNumber() throws RemoteException{
        return lastBlock;
    }
    /* métodos remotos solo para la última fase */
    // leech solicitante será notificado del progreso de la descarga
    public void newLeech(Leech requester) throws RemoteException {
        // añade ese leech a la lista de leeches posteriores
	    // que deben ser notificados
        postLeeches.add(requester);
    }
    // Informa del progreso de la descarga
    public void notifyBlock(Leech l, int nBl) throws RemoteException {
        // actualizamos la información sobre el último bloque
	    // descargado por ese leech
        LeechInfo linf = leechMap.get(l);
        System.out.println(linf.getLeech().getName());
        if (linf != null) {
            System.out.println("Se actualiza el mapa");
            linf.setLastBlock(nBl);
            leechMap.put(l, linf);
        }
    }

    // método estático que obtiene del registry una referencia al tracker y
    // obtiene mediante lookupFile la información del fichero especificado
    // creando una instancia de esta clase
    static public DownloaderImpl init(String host, int port, String name, String file) throws RemoteException {
        /*if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());*/

        DownloaderImpl down = null;
        try {
            // localiza el registry en la máquina y puerto especificados
            Registry registry = LocateRegistry.getRegistry(host, port);
            // obtiene una referencia remota el servicio
            Tracker trck = (Tracker) registry.lookup("BitCascade");
            // comprobamos si ha obtenido bien la referencia:
            System.out.println("el nombre del nodo del tracker es: " + trck.getName());
            // obtiene la información del fichero mediante el método lookupFile del Tracker.
            FileInfo finf = trck.lookupFile(file);
            // comprueba resultado
            if (finf==null) {
                // si null: no se ha publicado ese fichero
                System.err.println("Fichero no publicado");
                System.exit(1);
            }
            // se crea un objeto de la clase DownloaderImpl
            down = new DownloaderImpl(name, file, finf);
            // usa el método addLeech del tracker para añadirse
            trck.addLeech(down, file);
        }
        catch (Exception e) {
            System.err.println("Downloader exception:");
            e.printStackTrace();
            System.exit(1);
        }
        return down;
    }
}
