FF//DEV: VAQUERA AGUILERA ETHAN EMILIANO
//DESARROLLO DE SISTEMAS DISTRIDUIDOS
//PROFE: PINEDA GUERRERO CARLOS
//GRUPO: 3CV14

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import javax.net.ssl.SSLServerSocketFactory;

public class ServidorSSL {
    //clase hecha para poder hacer el uso de datos byte y hacer su conversion a datos del tipo string en una cantidad la cual sea
    static void read(DataInputStream f,byte[] b,int posicion,int longitud) throws Exception         
    {
        while (longitud > 0)
        {
            int n = f.read(b,posicion,longitud);
            posicion += n;
            longitud -= n;
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Worker
     */
    static class Worker extends Thread {        //inicio de la clase multithread Worker que sirve para poder hacer todos los procesamientos del servidor
        Socket conexion;                        

        Worker(Socket conexion) {               //contructor que recibe la conexion del servidor
            this.conexion = conexion;
        }

        @Override
        public void run() {                     //metodo run donde se ejecutan los procesos de multi hilos 

            try {

                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());             //inicio de variable usada para hacer la escritura de respuestas al cliente 
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());               //inicio de variable de lectura para todas las peticiones del cliente

                int tamanoNombreA = entrada.readInt();                              //recepcion de dato para poder saber la longitud del nombre del archivo
                System.out.println(tamanoNombreA);

                byte[] nombreA = new byte[tamanoNombreA];                            //proceso para poder obtener el nombre del archivo recibido como arreglo e bytes
                read(entrada, nombreA, 0, tamanoNombreA);                  //conversion de datos por medio de funcion read determinando el tamaño de la cadena en base al tamanoNombreA
                String nombre = new String(nombreA, "UTF-8");
                System.out.println(nombre);

                int tamanoContenidoA = (int)entrada.readLong();                     //obtencion del tamano del contenido que esta en el archivo que se desea guardar
                System.out.println(tamanoContenidoA);                               

                byte[] contenidoA = new byte[tamanoContenidoA];                     //proceso para poder obtener el contenido del archivo recibido como un arreglo de bytes
                read(entrada, contenidoA, 0, tamanoContenidoA);           //conversion de datos por medio de la funcion read determinado por el tamano de la cadena en base al tamanoContenidoA
                String contenido = new String(contenidoA, "UTF-8");
                System.out.println(contenido);

                String filePath = "archivosGuardados\\" + nombre;
                FileOutputStream f = new FileOutputStream(filePath, true);          //Escritura de datos en el directoria "archivosGuardados/nombre_archivo.txt"
                byte[] appendArr = contenido.getBytes();
                f.write(appendArr);
                f.close();

                if (contenido.length() > 0 && nombre.length() > 0) {
                    salida.writeInt(1);                                                  //regreso de bandera que indica si el proceso se efectuo de manera correcta de ser ser regresa un Ok de no se regresa un Error
                }else{
                    salida.writeInt(0);
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }
    }

    public static void main(String[] args) {

        System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");  
        System.setProperty("javax.net.ssl.keyStorePassword", "1234567");
        //seccion de seteo de la propiedades para poder hacer el uso de sockets seguros en el sistema 

        SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        //linea que se encarga de poder hacer el inicio del socket seguro para poder despues hacer la conexion

        try (ServerSocket socket_servidor = socket_factory.createServerSocket(50000)) { 
        //en esta parte de aqui es donde ahora si se inicia el servidor y ahora puede recibir conexiones de manera repetitiva de cualquier cliente

            for(;;){           
                Socket conexion = socket_servidor.accept();
                Worker w1 = new Worker(conexion);          
                w1.start();                                 
            }
            //iniciando al servidor en modo que recibira peticiones del clienteD
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
