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

/**
 * ClienteMulticas
 */
public class ClienteMulticas {

    /*static byte[] recibe_mensaje(MulticastSocket socket, int longitud) throws IOException {
        byte[] buffer = new byte[longitud];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return buffer;
    }*/

    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }

    public static void main(String[] args) throws Exception {

        System.setProperty("java.net.preferIPv4Stack", "true");
        MulticastSocket socket = new MulticastSocket(50000);
        InetSocketAddress grupo = new InetSocketAddress(InetAddress.getByName("230.0.0.0"), 50000);
        NetworkInterface netInter = NetworkInterface.getByName("em0");

        for (;;) {
            socket.joinGroup(grupo, netInter);

            System.out.println("Esperando el datagrama...");

            byte[] a = recibe_mensaje_multicast(socket, 4);
            System.out.println(new String(a, "UTF-8"));

            byte[] buffer = recibe_mensaje_multicast(socket, 5 * 8);
            ByteBuffer b = ByteBuffer.wrap(buffer);

            for (int i = 0; i < 5; i++) {
                System.out.println(b.getDouble());
            }
            socket.leaveGroup(grupo, netInter);
        }

    }

}