// Clase que contiene la información de un fichero
// NO MODIFICAR

package interfaces;
import java.util.LinkedList;
import java.io.Serializable;
import interfaces.Seed;
import interfaces.Leech;

// solo por print, que usa los métodos remotos getName
import java.rmi.RemoteException;

public class FileInfo implements Serializable {
    public static final long serialVersionUID=1234567890L;
    int blockSize;
    int numBlocks;
    Seed seed;
    LinkedList<Leech> leechs = null;

    // constructor
    public FileInfo(Seed pub, int blSz, int nBls) {
        seed = pub;
        blockSize = blSz;
        numBlocks = nBls;
        leechs = new LinkedList<Leech>();
    }
    // añade un nuevo leech
    public void newLeech(Leech l) {
        leechs.add(l);
    }
    // getters
    public Seed getSeed() {
        return seed;
    }
    public int getBlockSize() {
        return blockSize;
    }
    public int getNumBlocks() {
        return numBlocks;
    }
    public LinkedList<Leech> getLeechList() {
        return leechs;
    }
    // solo para depurar
    public void print() throws RemoteException {
        System.out.println("\ttamaño de bloque: " + blockSize);
        System.out.println("\tnúmero de bloques: " + numBlocks);
        System.out.println("\tSeed: " + seed.getName());
	// llama a método remoto getName requiere RemoteException
	for (Leech leech : leechs)
	    // llama a método remoto getName requiere RemoteException
            System.out.println("\tLeech: " + leech.getName());
    }
}
