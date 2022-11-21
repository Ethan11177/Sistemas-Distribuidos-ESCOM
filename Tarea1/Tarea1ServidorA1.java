import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Tarea1ServidorA1 {
    static class Worker extends Thread {
    
        Socket conexion;
        
        Worker(Socket conexion){
            this.conexion = conexion;
        }

        @Override
        public void run() {
            try {

                int flag = 0;
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream()); 

                long Sinicio = entrada.readLong();
                System.out.println("Limite inferior: " + Sinicio);

                long Sfinal = entrada.readLong();
                System.out.println("Limite Superior: " + Sfinal);

                long number = entrada.readLong();
                System.out.println("Numero a calcular: " + number);

                ///////////////////////////////////////////////Inicio de la iteracion para encontrar si es que el numero es primo o no es primo
                for (long i = Sinicio; i < Sfinal; i++) {
                    if (number%i == 0) {
                        flag = 1;
                        break;
                    }
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                ///////////////////////////////////////////////Return de la bandere indicadora si es primo o no al ServidorB
                if (flag==1) {
                    salida.writeInt(1);
                }else{
                    salida.writeInt(0);
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

    public static void main(String[] args) throws Exception{
        try (ServerSocket server = new ServerSocket(50002)) {
            for(;;){
                Socket conexion = server.accept();
                Worker w = new Worker(conexion);
                w.start(); 
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
