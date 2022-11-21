import java.rmi.Naming;

import javax.xml.stream.events.StartDocument;

public class ClienteRMI {

    // Declaracion de objeto para poder hacer sincronizacion, los numeros generales
    // donde se pueden modificar las dimensiones de los arreglos, matrices
    // resultantes para poder hacer el calculo final de suma
    static Object obj = new Object();
    static int numN = 6000, numM = 5000;
    static double[] matrizC1, matrizC2, matrizC3, matrizC4, matrizC5, matrizC6;

    public static double[][] InicializarMatriz(int i, int numN, int numM) {

        // Inicio de la matris que luego va a ser retornada para poder hacer la
        // inicializacion de una de las dos matrices esenciales
        double[][] matriz = new double[numN][numM];

        // Inicio del ciclo que inicializa las matrices, dependiendo de cual de las dos
        // opciones se hayan elegido en la funcion contructura de esta
        for (int j = 0; j < matriz.length; j++) {
            for (int j2 = 0; j2 < matriz[0].length; j2++) {
                if (i == 0) {
                    matriz[j][j2] = (3 * j) + (2 * j2);
                    // matriz[j][j2] = i + 3 * j;
                } else {
                    matriz[j][j2] = (2 * j) - (3 * j2);
                    // matriz[j][j2] = 2 * i - j;
                }
            }
        }

        // Comparacion que se fija si la matriz a inicializar es la matrisB, si es asi
        // la transpone y la retorna
        if (i == 1) {
            double[][] matrizBt = new double[numM][numN];

            for (int x = 0; x < matriz.length; x++) {
                for (int y = 0; y < matriz[x].length; y++) {
                    matrizBt[y][x] = matriz[x][y];
                }
            }

            return matrizBt;
        } else {
            return matriz;
        }
    }

    public static void ImprimirMatrices(double[][] matriz, int casoN, String nombre) {

        // Comparacion que pone como limite los numeros que puede imprimir esta funcion
        if (casoN <= 12) {

            // Metodo de impresion donde se usa la matris que recibe y el nombre para que se
            // vea mejor al momento de hacer la impresion de la misma matriz
            System.out.println("\n////////////\t" + nombre + "\t//////////////////\n");
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[i].length; j++) {
                    System.out.print(nombre + "(" + i + "," + j + ")=" + matriz[i][j] + "\t");
                }
                System.out.println();
            }
        }
    }

    public static double[] DividirMatrizA(double[][] matrizA, int zona) {

        // Aqui se hace el inicio del arreglo para dividir a la matriz A por secciones
        // de renglones y asi hacer la multiplicacion
        double[] matriz = new double[matrizA[0].length];

        // inicio del ciclo que toma los valores en base la matrizA y se elige el
        // renglon con base a la seccion que se requiera en el contructor de la funcion
        for (int i = 0; i < matrizA[zona].length; i++) {
            matriz[i] = matrizA[zona][i];
        }

        return matriz;
    }

    public static double[] MultiplicarMatricez(double[][] matrizB, double[] matriz) {

        // funcion que se hace cargo de la multiplicacion de un arreglo de A, junto con
        // toda la matrizB para poder asi dar como resultado uno de los arrglos
        // resultantes C
        int suma = 0;
        double[] matrizC = new double[matrizB[0].length];

        // Inicio de ciclo doble para poder hacer la multiplicacion de la matriz con su
        // respectivo arregloA que conste en el momento de la ejecucion
        for (int i = 0; i < matrizB[0].length; i++) {
            for (int j = 0; j < matriz.length; j++) {

                suma += matrizB[j][i] * matriz[j];

                // System.out.println(matrizB[j][i] + " * " + matriz[j] + " = " + (matrizB[j][i]
                // * matriz[j]));
            }
            matrizC[i] = suma;
            suma = 0;
        }

        // Retorno de arreglo para uno de los componentes de la matriz C
        return matrizC;

        // NOTA: esta misma funcion, es la que se implemento para los servidores remotos
        // RMI, no esta comentado en la ClaseRMI.java porque los comentarios se
        // agragaron al final
    }

    public static void ImprimirArreglo(double[] matrizC, String nombre) {

        // Aqui se imprimen los componentes correspondientes para la matrizC, siendo los
        // arreglos de C

        if (matrizC.length <= 12) {
            System.out.println("\n////////////\t" + nombre + "\t//////////////////\n");
            for (int i = 0; i < matrizC.length; i++) {
                System.out.print(nombre + "= " + matrizC[i] + "\t");
            }
        }
    }

    public static double UnirMatrizC(double[] mC1, double[] mC2, double[] mC3, double[] mC4, double[] mC5,
            double[] mC6) {

        // Aqui se hace la suma de todos los componentes en conjunto para poder sacar el
        // checksum resultante de toda la multiplicacion
        double suma = 0;

        for (int i = 0; i < mC1.length; i++) {
            suma += mC1[i] + mC2[i] + mC3[i] + mC3[i] + mC4[i] + mC5[i] + mC6[i];
        }

        return suma;

    }

    static class Worker extends Thread {

        // Inicio de las matrices para poder hacer el conjunto de operaciones
        double[][] matrizA;
        double[][] matrizB;
        int numSer;

        // Constructor del worker que recibe las matrices ya inicializadas
        Worker(int numSer, double[][] matrizA, double[][] matrizB) {
            this.numSer = numSer;
            this.matrizA = matrizA;
            this.matrizB = matrizB;
        }

        @Override
        public void run() {

            // Comparador que se hace a la tarea de identificar las operaciones que ahora se
            // tiene que hacer apra poder hacer la division de matrices, multiplicacion e
            // impresion de las mismas, esto dividiendolas en los diferentes hilos y
            // servidores que corresponde a su funcionamiento
            switch (numSer) {
                case 0:
                    synchronized (obj) {
                        double[] matriz = DividirMatrizA(matrizA, 0);
                        matrizC1 = MultiplicarMatricez(matrizB, matriz);

                        ImprimirArreglo(matrizC1, "MatrizC1");
                    }
                    break;

                case 1:
                    synchronized (obj) {
                        double[] matriz = DividirMatrizA(matrizA, 1);
                        matrizC2 = MultiplicarMatricez(matrizB, matriz);

                        ImprimirArreglo(matrizC2, "MatrizC2");
                    }
                    break;

                case 2:
                    try {
                        synchronized (obj) {
                            String url = "rmi://10.0.0.5/prueba";
                            InterfaceRMI r = (InterfaceRMI) (Naming.lookup(url));

                            double[] matriz = r.DividirMatrizA(matrizA, 2);
                            matrizC3 = r.MultiplicarMatricez(matrizB, matriz);

                            ImprimirArreglo(matrizC3, "MatrizC3");
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 3:
                    try {
                        synchronized (obj) {
                            String url = "rmi://10.0.0.5/prueba";
                            InterfaceRMI r = (InterfaceRMI) (Naming.lookup(url));

                            double[] matriz = r.DividirMatrizA(matrizA, 3);
                            matrizC4 = r.MultiplicarMatricez(matrizB, matriz);

                            ImprimirArreglo(matrizC4, "MatrizC4");
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 4:
                    try {
                        synchronized (obj) {
                            String url = "rmi://10.0.0.6/prueba";
                            InterfaceRMI r = (InterfaceRMI) (Naming.lookup(url));

                            double[] matriz = r.DividirMatrizA(matrizA, 5);
                            matrizC6 = r.MultiplicarMatricez(matrizB, matriz);

                            ImprimirArreglo(matrizC6, "MatrizC6");
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 5:
                    try {
                        synchronized (obj) {
                            String url = "rmi://10.0.0.6/prueba";
                            InterfaceRMI r = (InterfaceRMI) (Naming.lookup(url));

                            double[] matriz = r.DividirMatrizA(matrizA, 4);
                            matrizC5 = r.MultiplicarMatricez(matrizB, matriz);

                            ImprimirArreglo(matrizC5, "MatrizC5");
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 117:

                    System.out.println("\n\nResultado del checksum: "
                            + UnirMatrizC(matrizC1, matrizC2, matrizC3, matrizC4, matrizC5, matrizC6));

                    break;

                default:
                    System.err.println("Error");
                    break;
            }

        }
    }

    public static void main(String[] args) throws Exception {

        // inicializacion de las matricez
        double[][] matrizA = InicializarMatriz(0, numN, numM);
        double[][] matrizB = InicializarMatriz(1, numN, numM);
        // impresionde las matrices con el nombre que estas tienen
        ImprimirMatrices(matrizA, numN, "MatrizA");
        ImprimirMatrices(matrizB, numN, "MatrizB");

        // inicio de todos los hilos para poder hacer una ejecucion en parelelo
        Worker w0 = new Worker(0, matrizA, matrizB);
        Worker w1 = new Worker(1, matrizA, matrizB);
        Worker w2 = new Worker(2, matrizA, matrizB);
        Worker w3 = new Worker(3, matrizA, matrizB);
        Worker w4 = new Worker(4, matrizA, matrizB);
        Worker w5 = new Worker(5, matrizA, matrizB);

        // Aqui se hace el ingraso a los hilos y se pone en espera el hilo principal
        // hasta que termine de ejecutarse los demas hilos
        w0.start();
        w1.start();
        w2.start();
        w3.start();
        w4.start();
        w5.start();

        w0.join();
        w1.join();
        w2.join();
        w3.join();
        w4.join();
        w5.join();

        // Declaracion de inicio e inicio del hilo final donde se hace muestra del
        // resultado de la suma NOTA: me gusta ese numero
        Worker w117 = new Worker(117, matrizA, matrizB);

        w117.start();
        w117.join();

    }
}
