import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * clienteETSwilli
 */
public class clienteETSwilli {

    public static void main(String[] args) throws Exception{
        
        try (Socket conexion = new Socket("sisdis.sytes.net", 30000)) {
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

            salida.writeDouble(69.15);
            
            salida.write(58);

            salida.writeDouble(10.4);

            salida.writeInt(42);

            System.out.println(entrada.readDouble());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}