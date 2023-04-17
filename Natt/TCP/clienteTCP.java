package TCP;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class clienteTCP {
    
        static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception{

            while (longitud > 0) {
                int n = f.read(b, posicion, longitud);
                posicion += n;
                longitud -= n;
            }
        }

    public static void main(String[] args) {
        try {
            Socket conexion = new Socket("localhost", 50000);

            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            
            int numero = 73, numero_inicial = 2, numero_final = 199;

            System.out.println("Numero: " + numero + " Numero Inicial: " + numero_inicial + " Numero Final: " + numero_final);
            
            salida.writeInt(numero);

            salida.writeInt(numero_inicial);

            salida.writeInt(numero_final);

            int tam = entrada.readInt();

            byte[] buffer = new byte[tam];
            read(entrada, buffer, 0, tam);
            System.out.println(new String(buffer, "UTF-8"));

            Thread.sleep(100);
            conexion.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
