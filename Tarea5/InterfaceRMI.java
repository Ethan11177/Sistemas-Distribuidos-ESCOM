import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceRMI extends Remote{

    public double[] DividirMatrizA(double[][] matrizA, int zona) throws RemoteException;
    public double[] MultiplicarMatricez(double[][] matrizB, double[] matriz) throws RemoteException;
}
