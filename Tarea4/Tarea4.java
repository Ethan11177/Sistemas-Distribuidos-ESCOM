
/*Alumno: Vaquera Aguilera Ethan Emiliano
 * Tarea 4: socket multicast
 * Escuela Superior de Computacion (ESCOM)
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class Tarea4 {

    // inicio de la funcion para el envio de datos por medio de datagramas
    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }

    // inicio de la funcion para poder recibir los mensajes por medio de datagramas
    // y un buffer permitiendo que cualquier tipo de mensaje pueda ser mandado sin
    // problemas
    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }

    // inicio del woerker que va a encargarse de la ejecucion de la recepcion de
    // mensajes por medio de la funcion que recibe mensajes, este actua mas que nada
    // como un cliente
    static class Worker extends Thread {

        String nombre;

        // constructor de worker para poder recibir el nombre
        Worker(String nombre) {
            this.nombre = nombre + ":";
        }

        // metodo run para poder hacer la ejecucion de los sitemas por medio de hilos
        // separados
        @Override
        public void run() {

            try {

                // inicializacion de todas las variables necesarias para hacer la conexion por
                // medio de multicast UDP - datagramas
                MulticastSocket socket = new MulticastSocket(10000);
                InetSocketAddress grupo = new InetSocketAddress(InetAddress.getByName("239.10.10.10"), 10000);
                NetworkInterface netInter = NetworkInterface.getByName("em0");

                // ciclo infinito para poder recibir los mensajes de manera continua
                for (;;) {

                    // aqui el socket se une al grupo multicas para poder hacer la recpcion de
                    // mensajes
                    socket.joinGroup(grupo, netInter);

                    // aqui se recibe el tamano del mensaje para depues poder hacer la recepcion del
                    // mensaje completo
                    byte[] tamMen = recibe_mensaje_multicast(socket, 1 * 8);
                    ByteBuffer a = ByteBuffer.wrap(tamMen);
                    int longitud_mensaje = (int) a.getDouble();

                    // aqui se hace la recpcion del mensaje ahora con lalongitud ya determinada de
                    // manera correcta
                    byte[] menInc = recibe_mensaje_multicast(socket, longitud_mensaje);
                    String menCom = new String(menInc, "CP850");

                    // aqui se le quitan los espacios al mensaje para poder hacer un array de datos
                    String[] menArr = menCom.split(" ");

                    // comparacion para hacer que los mensajes no sean repetidos con la persona que manda el mensaje.
                    if (nombre.equals(menArr[0])) {
                        // NADA
                    } else {
                        //impresion del mensaje
                        System.out.println(new String(menInc, "CP850"));
                    }

                    //se cierra el grupo para poner el modo de espera activa mientras el ciclo infinito siga en funcionamiento
                    socket.leaveGroup(grupo, netInter);
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

    }

    // inicio del metodo main que hace de inicio para la lectura de mensajes y datos
    // por medio de un ciclo infinito y des pues los manda a el cliente que recibe
    // los mensajes y los muetra
    public static void main(String[] args) throws Exception {

        System.setProperty("java.net.preferIPv4Stack", "true");

        try {

            // obtencion del nombre de usuario que esta queriendo hacecr un envio de mensaje
            String nombre = args[0];

            // inicio del hilo para el cliente que ahora se conecta por medio de multicast
            new Worker(nombre).start();

            // ciclo infinito para la lectira de mensajes que se quieren enviar a lo largo
            // de la ejecucion
            for (;;) {

                // delclaracion y lectura de variables necesarias para la lectura de mensajes.
                Scanner enTecla = new Scanner(System.in, "CP850");
                System.out.println(nombre + ":");
                String mensaje = enTecla.nextLine("UTF-8");
                //System.out.println(mensaje  + " lo anterior era el mensajé");
                // este es el mensaje que se crea para ser mandado
                mensaje = nombre + ": " + mensaje;

                // seccion que se encarga de determinar el tamano del mensaje para mandarlo por
                // medio de multicast, eso para despues poder recibir el paquete del otro lado
                // del.
                ByteBuffer b = ByteBuffer.allocate(1 * 8);
                b.putDouble(mensaje.length());
                envia_mensaje_multicast(b.array(), "239.10.10.10", 10000);

                // envio del mensaje por medio de la funcion enviar_mensaje_multicast
                envia_mensaje_multicast(mensaje.getBytes(), "239.10.10.10", 10000);

            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
