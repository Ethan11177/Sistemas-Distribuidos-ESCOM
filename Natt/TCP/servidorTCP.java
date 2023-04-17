package TCP;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class servidorTCP {

    static class Worker extends Thread {
    
        Socket conexion;
        Worker(Socket conexion){
            this.conexion = conexion;
        }

        @Override
        public void run() {
            try {
                int flag = 0;
                String cadena;

                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                
                int numero = entrada.readInt();
                System.out.println("Este es el numero:" + numero);

                int numero_inicial = entrada.readInt();
                System.out.println("Numero inicial: " + numero_inicial);

                int numero_final = entrada.readInt();
                System.out.println("Numero final: " + numero_final);

                for (int i = numero_inicial; i <= numero_final; i++) {
                    if (numero%i == 0) {
                        cadena = "Divide " + i;
                        salida.writeInt(cadena.length());
                        salida.write(cadena.getBytes());;
                        flag = 1;
                        break;
                    }
                }
                
                if(flag == 0){
                    cadena = "No divide";
                    salida.writeInt(cadena.length());
                    salida.write(cadena.getBytes());
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception{

        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(50000)) {
            for(;;){
                Socket conexion = servidor.accept();
                Worker w = new Worker(conexion);
                w.start();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
