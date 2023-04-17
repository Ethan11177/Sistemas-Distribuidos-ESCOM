import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class clientePrimo {

    static int flag = 0;

    static class Worker extends Thread {

        static Object obj = new Object();

        int puerto;
        int numero, numero_inicial, numero_final;

        Worker(int puerto, int numero, int numero_inicial, int numero_final) {
            this.puerto = puerto;
            this.numero = numero;
            this.numero_inicial = numero_inicial;
            this.numero_final = numero_final;
        }

        @Override
        public void run() {
            try (Socket conexion = new Socket("localhost", puerto)) {
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                salida.writeInt(numero);
                salida.writeInt(numero_inicial);
                salida.writeInt(numero_final);

                int tam = entrada.readInt();

                byte[] buffer = new byte[tam];
                read(entrada, buffer, 0, tam);
                String resultado = new String(buffer, "UTF-8");
                //System.out.println(resultado);

                if (resultado.equals("Divide")) {
                    flag = 1;
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {

        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    public static void main(String[] args) {
        try {

            try (Scanner tecla = new Scanner(System.in)) {
                int numero = 0;

                System.out.println("Ingresa el numero");
                numero = tecla.nextInt();

                Worker w1 = new Worker(50000, numero, 2, numero / 3);
                Worker w2 = new Worker(50001, numero, (numero / 3) + 1, 2 * (numero / 3));
                Worker w3 = new Worker(50002, numero, 2 * (numero / 3) + 1, numero - 1);

                w1.join();
                w2.join();
                w3.join();

                w1.start();
                w2.start();
                w3.start();
            }
            Thread.sleep(50);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        if (flag == 1) {
            System.out.println("Es primo");
        } else {
            System.out.println("No es primo");
        }
    }
}