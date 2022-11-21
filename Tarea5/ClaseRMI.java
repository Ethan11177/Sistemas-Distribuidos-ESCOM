import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClaseRMI extends UnicastRemoteObject implements InterfaceRMI{

    public ClaseRMI() throws RemoteException{
        super();
    }

    public double[] DividirMatrizA(double[][] matrizA, int zona) throws RemoteException{
        double[] matriz = new double[matrizA[0].length];

        for (int i = 0; i < matrizA[zona].length; i++) {
            matriz[i] = matrizA[zona][i];
        }

        return matriz;
    }

    public double[] MultiplicarMatricez(double[][] matrizB, double[] matriz) throws RemoteException{

        int suma = 0;
        double[] matrizC = new double[matrizB[0].length];

        for (int i = 0; i < matrizB[0].length; i++) {
            for (int j = 0; j < matriz.length; j++) {

                suma += matrizB[j][i] * matriz[j];

                // System.out.println(matrizB[j][i] + " * " + matriz[j] + " = " + (matrizB[j][i]
                // * matriz[j]));
            }
            matrizC[i] = suma;
            suma = 0;
        }

        return matrizC;
    }
    
}
