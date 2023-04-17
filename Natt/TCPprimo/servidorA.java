import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class servidorA {
    
    static class Worker extends Thread {
        
        Socket conexion;
        Worker(Socket conexion){
            this.conexion = conexion;
        }

        @Override
        public void run() {
            
            try {
                String resultado = "No divide";
                int flag = 0;

                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                int numero = entrada.readInt();
                int numero_inicial = entrada.readInt();
                int numero_final = entrada.readInt();

                System.out.println(numero + " " + numero_inicial + " " + numero_final);

                for (int i = numero_inicial; i <= numero_final; i++) {
                    if (numero%i == 0) {
                        resultado = "Divide";
                        flag = 1;

                        System.out.println(resultado);

                        salida.writeInt(resultado.length());
                        salida.write(resultado.getBytes());
                        break;
                    }
                }

                if(flag == 0){
                    resultado = "No divide";

                    System.out.println(resultado);

                    salida.writeInt(resultado.length());
                    salida.write(resultado.getBytes());
                }
                

            } catch (IOException e) {
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
