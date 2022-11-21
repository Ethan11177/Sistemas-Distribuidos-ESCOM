import java.rmi.Naming;

/**
 * Servidor
 */
public class ServidorRMI {

    public static void main(String[] args) throws Exception{
        String url = "rmi://localhost/prueba";
        ClaseRMI obj = new ClaseRMI();

        Naming.rebind(url, obj);
    }
}