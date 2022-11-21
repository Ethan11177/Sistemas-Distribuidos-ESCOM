import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServidorRmi {

    public static void main(String[] args) throws Exception {
        String url = "rmi://localhost/prueba";
        ClaseRMI obj = new ClaseRMI();

        Naming.rebind(url, obj);
    }
}
