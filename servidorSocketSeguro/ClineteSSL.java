//DEV: VAQUERA AGUILERA ETHAN EMILIANO
//DESARROLLO DE SISTEMAS DISTRIDUIDOS
//PROFE: PINEDA GUERRERO CARLOS
//GRUPO: 3CV14

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.Socket;
import java.util.Scanner;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.print.Doc;

/**
 * ClineteSSL
 */
public class ClineteSSL {

    static class CadenaArchivos {               //inicio de la clse para la lectura de la cadena y obtener la ruta de navegacion para los archivos.txt que se desea leer

        String cadena = new String();
        int nCadena = 0;

        CadenaArchivos(String cadena, int nCadena) {                //contructor de la clase para poder obtener la cadena y su longitud
            this.cadena = cadena;
            this.nCadena = nCadena;
        }

        public String DividirCadena(int num) {                      //inicio del metodo para poder hacer la division de la cadena y encontrar la ruta de cada unos de los archivos

            char[] caraStr = cadena.toCharArray();
            int[] ban1 = new int[3];
            int j = 0;

            for (int i = 0; i < nCadena; i++) {                     //este for lo que hace es encontrar los puntos de la extensio del archivo, ya que solo los archivos pueden contener puntos en sus nombres
                if (caraStr[i] == '.') {
                    ban1[j] = i;
                    j++;
                }
            }

            String archivo = new String();  

            ///////////////////////////////////////////////////////////comparacion encargada de encontar y regresar la ruta especifica de los archivos 1 2 y 3 de manera individual dependiendo del parametro que el metodo reciba
            if (num == 1) {
                for (int i = 0; i < ban1[0]; i++) {
                    archivo = archivo + String.valueOf(caraStr[i]);
                }
                archivo = archivo + ".txt";
            }
            if (num == 2) {
                for (int i = ban1[0] + 4; i < ban1[1]; i++) {
                    archivo = archivo + String.valueOf(caraStr[i]);
                }
                archivo = archivo + ".txt";
            }
            if (num == 3) {
                for (int i = ban1[1] + 4; i < ban1[2]; i++) {
                    archivo = archivo + String.valueOf(caraStr[i]);
                }
                archivo = archivo + ".txt";
            }

            // System.out.println(archivo);

            return archivo;
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }

    }

    static class Worker extends Thread {                //inicio de la clase Worker siendo que esta va a ser de uso multi-thread para asi poder hacer el envio de datos de manera inidividual

        double num = 0;
        String contenido = new String();
        String nombreA = new String();
        long tamanoA = 0;
        static Object onj = new Object();

        Worker(String contenido, String nombreA, long tamanoA) {                //contructor de la clase que recibe como parametros el contenido que tiene el archivo, su nombre de este y el peso que tiene este
            this.contenido = contenido;
            this.nombreA = nombreA;
            this.tamanoA = tamanoA;
        }

        @Override
        public void run() {                 //inicio del metodo run, que es donde se efectua el multi-threading 
            synchronized (onj) {
                try {

                    SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault(); //inicio del socket para poder hacer la comunicacion con el servidor

                    Socket conexion = null;             //creacion de variable conexion del tipo socket 

                    //inicio de cliente con reintentos de conexion 
                    for (;;) {
                        try {
                            conexion = cliente.createSocket("localhost", 50000);                //inicializacion de la variable conexion como un socket seguro, estableciendo conexion con el servidor

                            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());                 //inicio de variables de entrada de respuesta del servidor, y envio de solicitud para el servidor
                            DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                            //System.out.println("Nombre archivo: " + nombreA + " Peso Bytes archivo: " + tamanoA + " Contenido:\n" + contenido);
                            //salida.writeDouble(num);

                            ///////////////////////////////////////////////////////////Envio de variables al servidor, como el conetenido del archivo, nombre del archivo, longitud del contenido, longitud del nombre del arhivo
                            salida.writeInt(nombreA.length());

                            salida.write(nombreA.getBytes());

                            salida.writeLong(contenido.length());

                            salida.write(contenido.getBytes());
                            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                            int ban1 = entrada.readInt();                   //recepcion de respuesta del servidor

                            if (ban1==1) {
                                System.out.println("OK " + nombreA + " escrito recibido correctamente");                        //comparacion de la respuesta para poder saber si es que los datos fueron escritos de manera correcta o no lo fueron
                            }else{
                                System.out.println("ERROR " + nombreA + " no se pudo recibir de manera correcta");
                            }

                            Thread.sleep(1000);
                            conexion.close();                                       //fin de la conexion y el proceso en general se mata con el break
                            break;

                        } catch (Exception e) {
                            Thread.sleep(1000);
                        }
                    }

                } catch (Exception e) {
                    // System.err.println(e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {                                                        //inicio de la clase principal main 

        if (args.length > 0) {                  //inicio que verifica si es que los parametros necesario para la tarea2 son ingresados de manera correcta
            try {

                String archivos = new String();

                System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");                 //seteo de parametrso para el uso de la conexion por medio de sockets seguros
                System.setProperty("javax.net.ssl.trustStorePassword", "123456");

                for (String arg : args) {
                    archivos = archivos + arg;                  //lectura de los datos que residen en el args para poder hacer la lectura de estos
                }

                CadenaArchivos cadena = new CadenaArchivos(archivos, archivos.length());            //llamada a la clase para poder hacer el desgloce de la cadena que deseamos

                String archivo1 = cadena.DividirCadena(1);                                      //llamada al metodo que nos retornara el nombre de los 3 archivos de manera inidividual para poder hacer el funcionamiento de la aplicacion
                String archivo2 = cadena.DividirCadena(2);
                String archivo3 = cadena.DividirCadena(3);

                // System.out.println(archivo1 + " " + archivo2 + " " + archivo3 );

                ////////////////////////////////////////////////////////////////////////////////////////Inicio de la lectura de los archivos para poder hacer el procesamiento de estos, sus nombres, contenido y peso que este tiene en el
                File onion1 = new File(archivo1);                               
                FileReader fr = new FileReader(onion1);
                BufferedReader br = new BufferedReader(fr);
                String linea = new String();
                String contenido1 = new String();

                while ((linea = br.readLine()) != null) {
                    contenido1 = contenido1 + linea + "\n";
                }

                //System.out.println(contenido1 + " " + onion1.getName() + " " + onion1.length());

                File onion2 = new File(archivo2);
                FileReader fr2 = new FileReader(onion2);
                BufferedReader br2 = new BufferedReader(fr2);
                String contenido2 = new String();

                while ((linea = br2.readLine()) != null) {
                    contenido2 = contenido2 + linea + "\n";
                }

                //System.out.println(contenido2 + " " + onion2.getName() + " " + onion2.length());

                File onion3 = new File(archivo3);
                FileReader fr3 = new FileReader(onion3);
                BufferedReader br3 = new BufferedReader(fr3);
                String contenido3 = new String();

                while ((linea = br3.readLine()) != null) {
                    contenido3 = contenido3 + linea + "\n";
                }

                //System.out.println(contenido3 + " " + onion3.getName() + " " + onion3.length());
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                try {

                    Worker w1 = new Worker(contenido1, onion1.getName(), onion1.length());                  //inicio de los hilos multithread para envio de datos al server 
                    Worker w2 = new Worker(contenido2, onion2.getName(), onion2.length()-1);
                    Worker w3 = new Worker(contenido3, onion3.getName(), onion3.length());

                    w1.start();
                    w2.start();
                    w3.start();

                    w1.join();
                    w2.join();
                    w3.join();

                } catch (Exception e) {
                    // System.err.println(e.getMessage());
                }

            } catch (Exception e) {
                // System.err.println(e.getMessage());
            }
        } else {
            System.out.println("especifique los elementos que desea");
        }
    }
}