// Clase que implementa la interfaz remota Seed.
// Actúa como un servidor que ofrece un método remoto para leer los bloques
// del fichero publicado.
// LA FUNCIONALIDAD DE LA CLASE SE COMPLETA EN FASE 1 (TODO 1) Y LA 2 (TODO 2)

package peers;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import interfaces.Seed;
import interfaces.Tracker;

// Se comporta como un objeto remoto: UnicastRemoteObject
public class Publisher extends UnicastRemoteObject implements Seed {
    public static final long serialVersionUID=1234567890L;
    String name; // nombre del nodo (solo para depurar)
    String file;
    String path; // convenio: path = name + "/" + file
    int blockSize;
    int fileSize;
    int numBlocks;
    transient RandomAccessFile fd;

    public Publisher(String n, String f, int bSize) throws RemoteException, IOException {
        name = n; // nombre del nodo (solo para depurar)
        file = f; // nombre del fichero especificado
        path = name + "/" + file; // convenio: directorio = nombre del nodo
        blockSize = bSize; // tamaño de bloque especificado
        fd = new RandomAccessFile(path, "r");
	    // Cálculo del nº bloques redondeado por exceso:
	    //     truco: ⌈x/y⌉ -> (x+y-1)/y
        fileSize = (int) fd.length();
        numBlocks = (int) (fd.length() + blockSize - 1)/blockSize;
    }
    public String getName() throws RemoteException {
        return name;
    }

    public byte [] read(int numBl) throws RemoteException {
        byte [] buf = null;
        System.out.println("publisher read " + numBl);

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
    public int getNumBlocks() { // no es método remoto
        return numBlocks;
    }

    // Obtiene del registry una referencia al tracker y publica mediante
    // announceFile el fichero especificado creando una instancia de esta clase
    static public void main(String args[]) throws RemoteException {
        if (args.length!=5) {
            System.err.println("Usage: Publisher registryHost registryPort name file blockSize");
            return;
        }
        /*if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());*/

        try {
            // localiza el registry en la máquina y puerto especificados
            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            // obtiene una referencia remota el servicio
            Tracker trck = (Tracker) registry.lookup("BitCascade");
            // comprobamos si ha obtenido bien la referencia:
            System.out.println("el nombre del nodo del tracker es: " + trck.getName());
            // se crea un objeto publisher para publicar el fichero
            Publisher pub = new Publisher(args[2], args[3], Integer.parseInt(args[4]));
            // se llama al método anounceFile del tracker
            boolean res = trck.announceFile(pub, args[3], Integer.parseInt(args[4]), pub.getNumBlocks());
            // comprueba resultado
            if (!res) {
                // si false: ya existe fichero publicado con ese nombre
                System.err.println("Fichero ya publicado");
                System.exit(1);
            }
            System.err.println("Dando servicio...");
            // no termina nunca (modo de operación de UnicastRemoteObject)
        }
        catch (Exception e) {
            System.err.println("Publisher exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
