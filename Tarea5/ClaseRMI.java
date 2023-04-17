import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClaseRMI extends UnicastRemoteObject implements InterfaceRMI{

    public ClaseRMI() throws RemoteException{
        super();
    }

    public double[][] DividirMatrizA(double[][] matrizA, int zona) throws RemoteException{

        // Aqui se hace el inicio del arreglo para dividir a la matriz A por secciones
        // de renglones y asi hacer la multiplicacion
        double[][] matriz = new double[matrizA.length/6][matrizA[0].length];

        // inicio del ciclo que toma los valores en base la matrizA y se elige el
        // renglon con base a la seccion que se requiera en el contructor de la funcion
        for (int i = 0; i < matrizA.length/6; i++) {
            for (int j = 0; j < matrizA[0].length; j++) {
                matriz[i][j] = matrizA[i + zona][j];
            }
        }

        return matriz;
    }

    public double[][] MultiplicarMatricez(double[][] matrizB, double[][] matrizA) throws RemoteException{

        // funcion que se hace cargo de la multiplicacion de un arreglo de A, junto con
        // toda la matrizB para poder asi dar como resultado uno de los arrglos
        // resultantes C
        double[][] matrizC = new double[matrizA.length][matrizB[0].length];

        // Inicio de ciclo doble para poder hacer la multiplicacion de la matriz con su
        // respectivo arregloA que conste en el momento de la ejecucion
        if (matrizA[0].length == matrizB.length) {
            for (int i = 0; i < matrizA.length; i++) {
                for (int j = 0; j < matrizB[0].length; j++) {
                    for (int j2 = 0; j2 < matrizA[0].length; j2++) {
                        matrizC[i][j] += matrizA[i][j2] * matrizB[j2][j];
                        //System.out.println(matrizC[i][j] + "=" + matrizA[i][j2] + "*" + matrizB[j2][j]);
                    }
                }
            }
        }

        // Retorno de arreglo para uno de los componentes de la matriz C
        return matrizC;

        // NOTA: esta misma funcion, es la que se implemento para los servidores remotos
        // RMI, no esta comentado en la ClaseRMI.java porque los comentarios se
        // agragaron al final
    }
    public double Sumar(double[][] c1, double[][] c2) throws RemoteException{
        double suma = 0;

        for (int i = 0; i < c1.length; i++) {
            for (int j = 0; j < c1[0].length; j++) {
                suma += c1[i][j] + c2[i][j];
            }
        }

        //System.out.println("\n\nResultado del checksum: " + suma);
        return suma;
    }    
}
