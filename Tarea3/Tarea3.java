import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Tarea3 {

    static int casoN = 12;

    static double sumaC = 0;

    static double[][] matrizA = new double[casoN][casoN];
    static double[][] matrizB = new double[casoN][casoN];
    static double[][] matrizC = new double[casoN][casoN];

    public static void Checksum() {
        for (int i = 0; i < matrizC.length; i++) {
            for (int j = 0; j < matrizC.length; j++) {
                sumaC += matrizC[i][j];
            }
        }

        System.out.println("\nLa suma total es de: " + sumaC);
    }

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    static void enviarMatriz(DataOutputStream salida, double[][] M, int filas, int columnas, int iniJ, int finJ,
            int iniI, int finI) throws Exception {

        // ByteBuffer n = ByteBuffer.allocate(filas * columnas * 8);

        for (int i = iniI; i < finI; i++) {
            for (int j = iniJ; j < finJ; j++) {
                salida.writeDouble(M[i][j]);
            }
        }

        // byte[] conv = n.array();

        // salida.write(conv);
    }

    /*
     * Esta es la parte que divide la matriz en las varias sub matrices que se tiene
     * que usar para poder hacer el envio a los servidores
     */

    /* inicializacion de hilo para el inicio de los calculos del servidor */
    static class Worker2 extends Thread {

        Socket conexionServidor;

        Worker2(Socket conexionServidor) {
            this.conexionServidor = conexionServidor;
        }

        @Override
        public void run() {
            try {

                DataOutputStream salida = new DataOutputStream(conexionServidor.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexionServidor.getInputStream());

                // System.out.println(tamN1 + " " + tamN2);

                double[][] matrizA2 = new double[casoN/2][casoN];
                double[][] matrizB2 = new double[casoN/2][casoN];
                double[][] matrizC2 = new double[casoN/2][casoN];

                for (int i = 0; i < casoN/2; i++) {
                    for (int j = 0; j < casoN; j++) {
                        matrizA2[i][j] = entrada.readDouble();
                    }
                }

                for (int i = 0; i < casoN/2; i++) {
                    for (int j = 0; j < casoN; j++) {
                        matrizB2[i][j] = entrada.readDouble();
                    }
                }

                for (int i = 0; i < casoN / 2; i++) {
                    for (int j = 0; j < casoN / 2; j++) {
                        matrizC2[i][j] += matrizA2[i][j] * matrizB2[i][j];
                    }
                }

                for (int i = 0; i < casoN / 2; i++) {
                    for (int j = 0; j < casoN / 2; j++) {
                        salida.writeDouble(matrizC2[i][j]);
                    }
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    static class Worker3 extends Thread {

        String ipHost;
        int N = 0;
        int nodo = 0;
        static Object obj = new Object();

        Worker3(String ipHost, int N, int nodo) {
            this.ipHost = ipHost;
            this.N = N;
            this.nodo = nodo;
        }

        @Override
        public void run() {

            try (Socket conexionCliente = new Socket(ipHost, 50000)) {

                /*
                 * Aqui es donde se declara las llamadas para poder hacer el envio de datos y la
                 * recepcion de estos en el sistema
                 */
                DataOutputStream salida = new DataOutputStream(conexionCliente.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexionCliente.getInputStream());

                /*
                 * Aqui es donde se hace el envio de las matrices hacia el servidor por medio de
                 * ciclos que envian y del otro lado igualmente un ciclo lo recibe
                 */
                switch (nodo) {
                    case 1:
                        // A1
                        for (int i = 0; i < N / 2; i++) {
                            for (int j = 0; j < N; j++) {
                                salida.writeDouble(matrizA[i][j]);;
                            }
                        }
                        // B1
                        for (int i = 0; i < N / 2; i++) {
                            for (int j = 0; j < N; j++) {
                                salida.writeDouble(matrizB[i][j]);
                            }
                        }
                        // C1
                        for (int i = 0; i < N / 2; i++) {
                            for (int j = 0; j < N / 2; j++) {
                                matrizC[i][j] = entrada.readDouble();
                            }
                        }
                        break;
                    case 2:
                        // A1
                        for (int i = 0; i < N / 2; i++) {
                            for (int j = 0; j < N; j++) {
                                salida.writeDouble(matrizA[i][j]);
                            }
                        }
                        // B2
                        for (int i = N / 2; i < N; i++) {
                            for (int j = 0; j < N; j++) {
                                salida.writeDouble(matrizB[i][j]);
                            }
                        }
                        // C2
                        for (int i = 0; i < N / 2; i++) {
                            for (int j = N / 2; j < N; j++) {
                                matrizC[i][j] = entrada.readDouble();
                            }
                        }
                        break;
                    case 3:
                        // A2
                        for (int i = N/2; i < N; i++) {
                            for (int j = 0; j < N; j++) {
                                salida.writeDouble(matrizA[i][j]);
                            }
                        }
                        // B1
                        for (int i=0; i<N/2; i++) {
                            for (int j=0; j<N; j++) {
                                salida.writeDouble(matrizB[i][j]);
                            }
                        }
                        // C3
                        for (int i = N / 2; i < N; i++) {
                            for (int j = 0; j < N / 2; j++) {
                                matrizC[i][j] = entrada.readDouble();
                            }
                        }
                        break;
                    case 4:
                        // A2
                        for (int i=N/2; i<N; i++) {
                            for (int j=0; j<N; j++) {
                                salida.writeDouble(matrizA[i][j]);
                            }
                        }
                        // B2
                        for (int i=N/2; i<N; i++) {
                            for (int j=0; j<N; j++) {
                                salida.writeDouble(matrizB[i][j]);
                            }
                        }
                        // C4
                        for (int i = N / 2; i < N; i++) {
                            for (int j = N / 2; j < N; j++) {
                                matrizC[i][j] = entrada.readDouble();
                            }
                        }
                        break;
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

    }

    public static void ImprimirMatrices() {

        if (casoN == 12) {

            System.out.println("////////////\tMatrisA\t//////////////////\n");
            for (int i = 0; i < matrizA.length; i++) {
                for (int j = 0; j < matrizA[i].length; j++) {
                    System.out.print("MatrizA" + "(" + i + "," + j + ")=" + matrizA[i][j] + "\t");
                }
                System.out.println();
            }

            System.out.println("////////////\tMatrisB\t//////////////////\n");
            for (int i = 0; i < matrizB.length; i++) {
                for (int j = 0; j < matrizB[i].length; j++) {
                    System.out.print("MatrizB" + "(" + i + "," + j + ")=" + matrizB[i][j] + "\t");
                }
                System.out.println();
            }

            System.out.println("////////////\tMatrisC\t//////////////////\n");
            for (int i = 0; i < matrizC.length; i++) {
                for (int j = 0; j < matrizC[i].length; j++) {
                    System.out.print("MatrizC" + "(" + i + "," + j + ")=" + matrizC[i][j] + "\t");
                }
                System.out.println();
            }
        }

    }

    /*
     * este hilo es el inicio del cliente y el servidor y se determine cual de los
     * dos es el que se tiene que usar
     */
    static class Worker extends Thread {

        int numFun;
        Scanner read = new Scanner(System.in);

        Worker(int numFun) {
            this.numFun = numFun;
        }

        @Override
        public void run() {

            /*
             * inicio del if usado para determinar el proceso que se quiere usar, si es un
             * servidor o un cliente
             */
            if (numFun == 0) {

                /*
                 * seccion para inicializar el caso que se quiere hacer, ademas de la
                 * inicializacion de las matrices
                 */
                // System.out.println("Que caso quiere ejecutar \nN=12\nN=4000");
                // casoN = read.nextInt();

                for (int i = 0; i < casoN; i++) {
                    for (int j = 0; j < casoN; j++) {
                        matrizA[i][j] = i + 3 * j;
                        matrizB[i][j] = 2 * i - j;
                    }
                }

                /*
                 * Esta seccion se encarga de hacer la trasnpuesta de la matriz B con la ayuda
                 * de una matriz BT que despues regresa los valosres a la propia matriz B
                 */
                double[][] matrizBt = new double[casoN][casoN];

                for (int x = 0; x < matrizB.length; x++) {
                    for (int y = 0; y < matrizB[x].length; y++) {
                        matrizBt[y][x] = matrizB[x][y];
                    }
                }

                for (int x = 0; x < matrizB.length; x++) {
                    for (int y = 0; y < matrizB.length; y++) {
                        matrizB[x][y] = matrizBt[x][y];
                    }
                }

                // ImprimirMatrices(matrizA1, matrizA1.length, casoN, "A1");
                // ImprimirMatrices(matrizB1, matrizB1.length, casoN, "B1");

                try {
                    Worker3 w1 = new Worker3("20.172.196.231", casoN, 1);
                    Worker3 w2 = new Worker3("20.172.196.231", casoN, 2);
                    Worker3 w3 = new Worker3("4.246.176.96", casoN, 3);
                    Worker3 w4 = new Worker3("4.246.176.96", casoN, 4);

                    w1.start();
                    w2.start();
                    w3.start();
                    w4.start();

                    w1.join();
                    w2.join();
                    w3.join();
                    w4.join();

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

                ImprimirMatrices();

                Checksum();

                System.exit(MAX_PRIORITY);
            }

            /*
             * else que sirve para iniciar la ejecucion del servidor en los nodos que
             * corresponda ser servidor
             */
            else {
                try (ServerSocket servidor = new ServerSocket(50000)) {

                    for (;;) {
                        Socket conexionServidor = servidor.accept();

                        System.out.println("este es el servidor");
                        Worker2 w1 = new Worker2(conexionServidor);

                        w1.start();
                        w1.join();

                    }

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {

        /* seccion que determina el nombre de los HOST que se estan mandando a llamar */
        InetAddress address = InetAddress.getLocalHost();
        /*
         * linea para poder hacer la asignacion sin espacios y convertido de manera
         * correcta a String
         */
        String nameHost = String.valueOf(address.getHostName().replace("\\s", ""));
        /*
         * este de aqui es el nombre host de la computadora local donde se inicia el
         * nodo0 (nombre de mi computadora (esta chido asi que no se vale juzgar))
         */
        String nameHost1 = "PChiquitaEVaquera";
        int nameNumHost = 0;

        if (nameHost.equals(nameHost1)) {
            nameNumHost = 0;
        } else {
            nameNumHost = 1;
        }

        /*
         * esta seccion manda a llamar los hilos de trabajo para poder determinar el
         * servidor y el clinte
         */

        if (nameNumHost == 0) {
            Worker w1 = new Worker(0);
            w1.start();
            w1.join();
        } else {
            Worker w2 = new Worker(1);
            w2.start();
            w2.join();
        }
    }
}