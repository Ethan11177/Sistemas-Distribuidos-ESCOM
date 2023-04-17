import java.rmi.Naming;

import javax.xml.stream.events.StartDocument;

public class ClienteRMI {

    // Declaracion de objeto para poder hacer sincronizacion, los numeros generales
    // donde se pueden modificar las dimensiones de los arreglos, matrices
    // resultantes para poder hacer el calculo final de suma
    static Object obj = new Object();
    static int numN = 6, numM = 5;
    static double[][] matrizC1, matrizC2, matrizC3, matrizC4, matrizC5, matrizC6;
    static double sumaT = 0;

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
                } else {
                    matriz[j][j2] = (2 * j) - (3 * j2);
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

    public static double[][] DividirMatrizA(double[][] matrizA, int zona) {

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

    public static double[][] MultiplicarMatricez2(double[][] matrizB, double[][] matrizA) {

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

    public static double Sumar(double[][] c1, double[][] c2){
        double suma = 0;

        for (int i = 0; i < c1.length; i++) {
            for (int j = 0; j < c1[0].length; j++) {
                suma += c1[i][j] + c2[i][j];
            }
        }

        //System.out.println("\n\nResultado del checksum: " + suma);
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
                        double[][] matriz = DividirMatrizA(matrizA, 0);
                        matrizC1 = MultiplicarMatricez2(matrizB, matriz);

                        ImprimirMatrices(matrizC1, numM, "MatrizC1");
                    }
                    break;

                case 1:
                    synchronized (obj) {
                        double[][] matriz = DividirMatrizA(matrizA, 1);
                        matrizC2 = MultiplicarMatricez2(matrizB, matriz);

                        ImprimirMatrices(matrizC2, numM, "MatrizC2");
                    }
                    break;

                case 2:
                    try {
                        synchronized (obj) {
                            String url = "rmi://localhost/prueba";
                            InterfaceRMI r = (InterfaceRMI) (Naming.lookup(url));

                            double[][] matriz = r.DividirMatrizA(matrizA, 2);
                            matrizC3 = r.MultiplicarMatricez(matrizB, matriz);

                            ImprimirMatrices(matrizC3, numM, "MatrizC3");
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 3:
                    try {
                        synchronized (obj) {
                            String url = "rmi://localhost/prueba";
                            InterfaceRMI r = (InterfaceRMI) (Naming.lookup(url));

                            double[][] matriz = r.DividirMatrizA(matrizA, 3);
                            matrizC4 = r.MultiplicarMatricez(matrizB, matriz);

                            ImprimirMatrices(matrizC4, numM, "MatrizC4");
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 4:
                    try {
                        synchronized (obj) {
                            String url = "rmi://localhost/prueba";
                            InterfaceRMI r = (InterfaceRMI) (Naming.lookup(url));

                            double[][] matriz = r.DividirMatrizA(matrizA, 4);
                            matrizC5 = r.MultiplicarMatricez(matrizB, matriz);

                            ImprimirMatrices(matrizC5, numM, "MatrizC5");

                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 5:
                    try {
                        synchronized (obj) {
                            String url = "rmi://localhost/prueba";
                            InterfaceRMI r = (InterfaceRMI) (Naming.lookup(url));

                            double[][] matriz = r.DividirMatrizA(matrizA, 5);
                            matrizC6 = r.MultiplicarMatricez(matrizB, matriz);

                            ImprimirMatrices(matrizC6, numM, "MatrizC6");
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 117:
                    try {
                        String url = "rmi://localhost/prueba";
                        InterfaceRMI r = (InterfaceRMI) (Naming.lookup(url));

                        String url2 = "rmi://localhost/prueba";
                        InterfaceRMI r2 = (InterfaceRMI) (Naming.lookup(url2));

                        sumaT += r.Sumar(matrizC1, matrizC2);
                        sumaT += r.Sumar(matrizC3, matrizC4);
                        sumaT += r2.Sumar(matrizC5, matrizC6);

                    System.out.println("suma total es: " + sumaT);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
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
