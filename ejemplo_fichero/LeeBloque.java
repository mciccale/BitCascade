// Muestra cómo leer un determinado bloque de un fichero.
// Dado que el último bloque normalmente no estará completo,
// en la práctica se requiere reservar justo el tamaño necesario
// para el buffer de lectura y este ejemplo ilustra cómo hacerlo. 
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.File;

class LeeBloque {
    static public void main(String args[]) throws IOException {
        if (args.length!=3) {
            System.err.println("Uso: LeeBloque fichero tam_bloque num_bloque");
            return;
        }
        String file = args[0];
        int blockSize = Integer.parseInt(args[1]);
        int numBlockRequested = Integer.parseInt(args[2]);

        long fileSizeBytes = new File(file).length();
	// redondeo por exceso
        int fileSizeBlocks = (int)((fileSizeBytes + blockSize - 1)/blockSize);

        // se asegura que el bloque solicitado está dentro del fichero
        if (numBlockRequested < fileSizeBlocks) {
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            int bufSize = blockSize;

            if (numBlockRequested + 1 == fileSizeBlocks) { // último bloque
                int fragmentSize = (int) (fileSizeBytes % blockSize);
                if (fragmentSize > 0) bufSize = fragmentSize;
            }
            byte [] buf = new byte[bufSize];
            raf.seek(numBlockRequested * blockSize);
            int n = raf.read(buf);
            System.out.println("Bytes leídos = " + n);
            System.out.write(buf);
        }
    }
}


    
