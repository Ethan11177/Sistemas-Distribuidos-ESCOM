import java.rmi.Naming;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class ClienteRmi {
    public static void main(String[] args) throws Exception{

        String url = "rmi://localhost/prueba";

        InterfaceRMI r = (InterfaceRMI)(Naming.lookup(url));

        System.out.println(r.mayusculas("hola"));
        System.out.println(r.suma(10, 20));

        int[][] m = {{1,2,5,4}, {12,2,6,7}, {4,4,6,8}};
        System.out.println(r.checksum(m));

    }
}
