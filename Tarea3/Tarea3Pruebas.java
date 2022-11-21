import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Tarea3 {

    static int casoN=0;

    static double sumaC = 0;

    static double[][] matrizA = new double[casoN][casoN];
    static double[][] matrizB = new double[casoN][casoN];

    /*Esta es la parte que divide la matriz en las varias sub matrices que se tiene que usar para poder hacer el envio a los servidores */
    public static double[][] DivisorMatriz(double[][] matriz, int tamN, int iniNi, int finNi, int iniNj, int finNj, int realTam) {

        //System.out.println("Aqui tenemos i " + iniN + " ,j " + finN);

        double[][] divM = new double[tamN][realTam];

        int cont1 = 0, cont2 = 0;

        for (int i = iniNi; i < finNi; i++) {
            for (int j = iniNj; j < finNj; j++) {
                divM[cont1][cont2] = matriz[i][j];
                //System.out.println("i="+ i + ", j=" + j  + " matris=" + matriz[i][j]);
                cont2++;
            }
            cont1++;
            cont2 = 0;
        }

        return divM; 
        
    }

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

                int tamN1 = entrada.readInt();
                int tamN2 = entrada.readInt();

                //System.out.println(tamN1 + " " + tamN2);

                double[][] matrizA = new double[tamN1][tamN2];
                double[][] matrizB1 = new double[tamN1][tamN2];
                double[][] matrizB2 = new double[tamN1][tamN2];

                for (int i = 0; i < tamN1; i++) {
                    for (int j = 0; j < tamN2; j++) {
                        matrizA[i][j] = entrada.readDouble();
                    }
                }

                for (int i = 0; i < tamN1; i++) {
                    for (int j = 0; j < tamN2; j++) {
                        matrizB1[i][j] = entrada.readDouble();
                        //System.err.println(matrizA[i][j]);
                    }
                }

                for (int i = 0; i < tamN1; i++) {
                    for (int j = 0; j < tamN2; j++) {
                        matrizB2[i][j] = entrada.readDouble();
                        //System.err.println(matrizA[i][j]);
                    }
                }             

                System.out.println("yo aviso cuando llegue aqui");

                for (int i = 0; i < tamN1; i++) {
                    for (int j = 0; j < tamN2; j++) {
                        salida.writeDouble(matrizA[i][j] * matrizB1[i][j]);

                        //System.out.println(matrizA[i][j] * matrizB1[i][j]);
                    }
                }

                for (int i = 0; i < tamN1; i++) {
                    for (int j = 0; j < tamN2; j++) {
                        salida.writeDouble(matrizA[i][j] * matrizB2[i][j]);

                        //System.out.println(matrizA[i][j] * matrizB2[i][j]);
                    }
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    static class Worker3 extends Thread {
        
        String ipHost;
        double[][] matrizA;
        double[][] matrizB1;
        double[][] matrizB2;
        int salidaTamN = 0;
        static Object obj = new Object();

        Worker3(String ipHost, double[][] matrizA, double[][] matrizB1, double[][] matrizB2, int salidaTamN){
            this.ipHost = ipHost;
            this.matrizA = matrizA;
            this.matrizB1 = matrizB1;
            this.matrizB2 = matrizB2;
            this.salidaTamN = salidaTamN;
        }

        @Override
        public void run() {

            try (Socket conexionCliente = new Socket(ipHost, 50000)) {

                /*Aqui es donde se declara las llamadas para poder hacer el envio de datos y la recepcion de estos en el sistema */
                DataOutputStream salida = new DataOutputStream(conexionCliente.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexionCliente.getInputStream());

                 /*Aqui es donde se hace el envio del N/2 que mide las matrices para hacer la inicializacion en los datos del servidor */

                salida.writeInt(salidaTamN);
                salida.writeInt(salidaTamN/2);

                double[][] matrizC1 = new double[salidaTamN/2][salidaTamN];
                double[][] matrizC2 = new double[salidaTamN/2][salidaTamN];

                /*Aqui es donde se hace el envio de las matrices hacia el servidor por medio de ciclos que envian y del otro lado igualmente un ciclo lo recibe */
                for (int i = 0; i < matrizA.length; i++) {
                    for (int j = 0; j < matrizA[i].length; j++) {
                        salida.writeDouble(matrizA[i][j]);
                    }
                }

                for (int i = 0; i < matrizB1.length; i++) {
                    for (int j = 0; j < matrizB1[i].length; j++) {
                        salida.writeDouble(matrizB1[i][j]);
                    }
                }

                for (int i = 0; i < matrizB2.length; i++) {
                    for (int j = 0; j < matrizB2[i].length; j++) {
                        salida.writeDouble(matrizB2[i][j]);
                    }
                }

                /*Seccion donde se regresa los valores que se obtubieron de la multiplicacion de matrices */
                for (int i = 0; i < matrizC1.length; i++) {
                    for (int j = 0; j < matrizC1[i].length; j++) {
                        matrizC1[i][j] = entrada.readDouble();
                        /*esta linea de aqui se encarga de hacer la suma de todos los terminos que estan en el erreglo C */
                        
                    }
                }

                ImprimirMatrices(matrizC1, matrizC1.length, salidaTamN, "C");

                for (int i = 0; i < matrizC2.length; i++) {
                    for (int j = 0; j < matrizC2[i].length; j++) {
                        matrizC2[i][j] = entrada.readDouble();
                        /*esta linea de aqui se encarga de hacer la suma de todos los terminos que estan en el erreglo C */
                    }
                }
                
                ImprimirMatrices(matrizC2, matrizC2.length, salidaTamN, "C");
                
                for (int i = 0; i < matrizC1.length; i++) {
                    for (int j = 0; j < matrizC1[i].length; j++) {
                        sumaC = matrizC1[i][j] + matrizC2[i][j];
                    }
                }   
                

                
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

    }

    public static void ImprimirMatrices(double[][] matriz, int tam, int caso, String nameMatris){
        
        if (caso == 12) {
            System.out.println("////////////\tMatris" + nameMatris + "\t//////////////////\n");
            for (int i = 0; i < tam; i++) {
                for (int j = 0; j < tam; j++) {
                    System.out.print("Matriz" + nameMatris +"("+ i + "," + j + ")=" + matriz[i][j] + "\t");
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

                /*seccion para inicializar el caso que se quiere hacer, ademas de la inicializacion de las matrices */
                System.out.println("Que caso quiere ejecutar \nN=12\nN=4000");
                casoN = read.nextInt();

                for (int i = 0; i < matrizA.length; i++) {
                    for (int j = 0; j < matrizB.length; j++) {
                        matrizA[i][j] = i+3*j;
                        matrizB[i][j] = 2*i-j;
                    }
                }

                ImprimirMatrices(matrizA, matrizA.length, casoN, "A");

                /*Esta seccion se encarga de hacer la trasnpuesta de la matriz B con la ayuda de una matriz BT que despues regresa los valosres a la propia matriz B */
                double[][] matrizBt = new double[casoN][casoN];

                for (int x=0; x < matrizB.length; x++) {
                    for (int y=0; y < matrizB[x].length; y++) {
                        matrizBt[y][x] = matrizB[x][y];
                    }
                }

                for (int x=0; x < matrizB.length; x++) {
                    for (int y=0; y < matrizB.length; y++) {
                        matrizB[x][y] = matrizB[x][y];
                    }
                }

                ImprimirMatrices(matrizB, matrizB.length, casoN, "Bt");

                /*Esta parte se encarga de hacer la divisio de mantrices en partes proporcionales para luego hacer el envio a los servidores */
                double[][] matrizA1 = DivisorMatriz(matrizA, casoN/2, 0, matrizA.length/2, 0,12, casoN);
                double[][] matrizA2 = DivisorMatriz(matrizA, casoN/2, (matrizA.length/2)+1, matrizA.length, 0, 12, casoN);


                double[][] matrizB1 = DivisorMatriz(matrizA, casoN/2, 0, matrizB.length/2, 0,12, casoN);
                double[][] matrizB2 = DivisorMatriz(matrizA, casoN/2, (matrizB.length/2)+1, matrizB.length, 0, 12, casoN);


                //ImprimirMatrices(matrizA1, matrizA1.length, casoN, "A1");
                //ImprimirMatrices(matrizB1, matrizB1.length, casoN, "B1");

                try {
                    Worker3 w1 = new Worker3("20.172.196.231", matrizA1, matrizB1, matrizB2, casoN);
                    Worker3 w2 = new Worker3("4.246.176.96", matrizA2, matrizB1, matrizB2, casoN);


                    w1.start();
                    w2.start();

                    w1.join();
                    w2.join();

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

                System.out.println("Suma total de los numeros es de " + sumaC);

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