import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Tarea3 {

    /*Esta es la parte que divide la matriz en las varias sub matrices que se tiene que usar para poder hacer el envio a los servidores */
    public static float[][] DivisorMatriz(float[][] matriz, int tamN, int iniN, int finN) {

        //System.out.println("Aqui tenemos i " + iniN + " ,j " + finN);

        float[][] divM = new float[tamN][tamN];

        for (int i = iniN; i < finN; i++) {
            for (int j = iniN; j < finN; j++) {
                divM[i][j] = matriz[i][j];
                //System.out.println("A"+ i + "," + j + " = " + divM[i][j]);
            }
        }

        return matriz; 
        
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

                int tamN = entrada.readInt();
                float[][] matrizA = new float[tamN][tamN];
                float[][] matrizB1 = new float[tamN][tamN];
                float[][] matrizB2 = new float[tamN][tamN];

                float[][] matrizC1 = new float[tamN][tamN];
                float[][] matrizC2 = new float[tamN][tamN];

                int cont = 0;

                for (int i = 0; i < tamN; i++) {
                    for (int j = 0; j < tamN; j++) {
                        matrizA[i][j] = entrada.readFloat();
                        cont ++;
                    }
                }

                for (int i = 0; i < tamN; i++) {
                    for (int j = 0; j < tamN; j++) {
                        matrizB1[i][j] = entrada.readFloat();
                        //System.err.println(matrizA[i][j]);
                    }
                }

                for (int i = 0; i < tamN; i++) {
                    for (int j = 0; j < tamN; j++) {
                        matrizB2[i][j] = entrada.readFloat();
                        //System.err.println(matrizA[i][j]);
                    }
                }

                for (int i = 0; i < tamN; i++) {
                    for (int j = 0; j < tamN; j++) {
                        salida.writeFloat(matrizA[i][j] * matrizB1[i][j]);

                        System.out.println(matrizA[i][j] * matrizB1[i][j]);
                    }
                }

                for (int i = 0; i < tamN; i++) {
                    for (int j = 0; j < tamN; j++) {
                        salida.writeFloat(matrizA[i][j] * matrizB2[i][j]);

                        System.out.println(matrizA[i][j] * matrizB2[i][j]);
                    }
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /*
     * este hilo es el inicio del cliente y el servidor y se determine cual de los
     * dos es el que se tiene que usar
     */
    static class Worker extends Thread {

        int numFun;
        int casoN=0;
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

                float[][] matrizA = new float[casoN][casoN];
                float[][] matrizB = new float[casoN][casoN];

                int cont1 = 0;
                
                float sumaC = 0;

                for (int i = 0; i < casoN; i++) {
                    for (int j = 0; j < casoN; j++) {
                        matrizA[i][j] = i+(3*j);
                        matrizB[i][j] = (2*i)-j;
                        if(casoN == 12) 
                            System.out.println("A"+ i + "," + j + " = " + matrizA[i][j] + "\tB" + i + "," + j + " = " + matrizB[i][j]);
                    }
                }

                /*Esta seccion se encarga de hacer la trasnpuesta de la matriz B con la ayuda de una matriz BT que despues regresa los valosres a la propia matriz B */
                float[][] matrizBt = new float[casoN][casoN];

                for (int x=0; x < matrizB.length; x++) {
                    for (int y=0; y < matrizB[x].length; y++) {
                        matrizBt[y][x] = matrizB[x][y];
                    }
                }

                for (int x=0; x < matrizB.length; x++) {
                    for (int y=0; y < matrizB[x].length; y++) {
                        matrizB[x][y] = matrizB[x][y];
                    }
                }

                /*Esta parte se encarga de hacer la divisio de mantrices en partes proporcionales para luego hacer el envio a los servidores */
                float[][] matrizA1 = DivisorMatriz(matrizA, casoN, 0, casoN/2);
                float[][] matrizA2 = DivisorMatriz(matrizA, casoN, (casoN/2)+1, casoN);

                float[][] matrizB1 = DivisorMatriz(matrizA, casoN, 0, casoN/2);
                float[][] matrizB2 = DivisorMatriz(matrizA, casoN, (casoN/2)+1, casoN);

                float[][] matrizC1 = new float[casoN/2][casoN/2];
                float[][] matrizC2 = new float[casoN/2][casoN/2];
                float[][] matrizC3 = new float[casoN/2][casoN/2];
                float[][] matrizC4 = new float[casoN/2][casoN/2];
                
                /*
                 * inicio del los intentos de conexion para poder hacer el envio de datos a los
                 * nodos de servidor
                 */
                for (;;) {
                    try (Socket conexionCliente = new Socket("20.172.196.231", 50000)) {

                        /*Aqui es donde se declara las llamadas para poder hacer el envio de datos y la recepcion de estos en el sistema */
                        DataOutputStream salida = new DataOutputStream(conexionCliente.getOutputStream());
                        DataInputStream entrada = new DataInputStream(conexionCliente.getInputStream());

                        /*Aqui es donde se hace el envio del N/2 que mide las matrices para hacer la inicializacion en los datos del servidor */
                        salida.writeInt(casoN/2);

                        /*Aqui es donde se hace el envio de las matrices hacia el servidor por medio de ciclos que envian y del otro lado igualmente un ciclo lo recibe */
                        for (int i = 0; i < matrizA1.length; i++) {
                            for (int j = 0; j < matrizA1.length; j++) {
                                salida.writeFloat(matrizA1[i][j]);
                            }
                        }

                        for (int i = 0; i < matrizB1.length; i++) {
                            for (int j = 0; j < matrizB1.length; j++) {
                                salida.writeFloat(matrizB1[i][j]);
                            }
                        }

                        for (int i = 0; i < matrizB2.length; i++) {
                            for (int j = 0; j < matrizB2.length; j++) {
                                salida.writeFloat(matrizB2[i][j]);
                            }
                        }

                        /*Seccion donde se regresa los valores que se obtubieron de la multiplicacion de matrices */
                        for (int i = 0; i < casoN/2; i++) {
                            for (int j = 0; j < casoN/2; j++) {
                                matrizC1[i][j] = entrada.readFloat();
                                /*esta linea de aqui se encarga de hacer la suma de todos los terminos que estan en el erreglo C */
                                sumaC += matrizC1[i][j];
                            }
                        }

                        for (int i = 0; i < casoN/2; i++) {
                            for (int j = 0; j < casoN/2; j++) {
                                matrizC2[i][j] = entrada.readFloat();
                                /*esta linea de aqui se encarga de hacer la suma de todos los terminos que estan en el erreglo C */
                                sumaC += matrizC2[i][j];
                            }
                        }

                        /*seccion que se encarga de la impresion de los arrglos si es que es necesario hacerlo */
                        if (casoN == 12) {

                            for (int i = 0; i < casoN/2; i++) {
                                for (int j = 0; j < casoN/2; j++) {
                                    System.out.println("\n\nC " + (i)  + "," + (j) + " = " + matrizC1[i][j]);
                                }
                                cont1++;
                            }
                            
                            for (int i = 0; i < casoN/2; i++) {
                                for (int j = 0; j < casoN/2; j++) {
                                    System.out.println("\n\nC " + (i+cont1)  + "," + (j) + " = " + matrizC2[i][j]);
                                }
                            }
                            
                        }

                        /*se terminan los calculos y se cierra la conexion con del cliente */
                        conexionCliente.close();
                        break;

                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                
                for(;;){
                    try (Socket conexionCliente = new Socket("4.246.176.96", 50000)) {

                        /*Aqui es donde se declara las llamadas para poder hacer el envio de datos y la recepcion de estos en el sistema */
                        DataOutputStream salida = new DataOutputStream(conexionCliente.getOutputStream());
                        DataInputStream entrada = new DataInputStream(conexionCliente.getInputStream());

                        /*Aqui es donde se hace el envio del N/2 que mide las matrices para hacer la inicializacion en los datos del servidor */
                        salida.writeInt(casoN/2);

                        /*Aqui es donde se hace el envio de las matrices hacia el servidor por medio de ciclos que envian y del otro lado igualmente un ciclo lo recibe */
                        for (int i = 0; i < matrizA2.length; i++) {
                            for (int j = 0; j < matrizA2.length; j++) {
                                salida.writeFloat(matrizA2[i][j]);
                            }
                        }

                        for (int i = 0; i < matrizB1.length; i++) {
                            for (int j = 0; j < matrizB1.length; j++) {
                                salida.writeFloat(matrizB1[i][j]);
                            }
                        }

                        for (int i = 0; i < matrizB2.length; i++) {
                            for (int j = 0; j < matrizB2.length; j++) {
                                salida.writeFloat(matrizB2[i][j]);
                            }
                        }

                        /*Seccion donde se regresa los valores que se obtubieron de la multiplicacion de matrices */
                        for (int i = 0; i < casoN/2; i++) {
                            for (int j = 0; j < casoN/2; j++) {
                                matrizC3[i][j] = entrada.readFloat();
                                /*esta linea de aqui se encarga de hacer la suma de todos los terminos que estan en el erreglo C */
                                sumaC += matrizC3[i][j];
                            }
                        }

                        for (int i = 0; i < casoN/2; i++) {
                            for (int j = 0; j < casoN/2; j++) {
                                matrizC4[i][j] = entrada.readFloat();
                                /*esta linea de aqui se encarga de hacer la suma de todos los terminos que estan en el erreglo C */
                                sumaC += matrizC4[i][j];
                            }
                        }

                        /*seccion que se encarga de la impresion de los arrglos si es que es necesario hacerlo */
                        if (casoN == 12) {

                            for (int i = 0; i < casoN/2; i++) {
                                for (int j = 0; j < casoN/2; j++) {
                                    System.out.println("\n\nC " + (i)  + "," + (j) + " = " + matrizC3[i][j]);
                                }
                                cont1++;
                            }
                            
                            for (int i = 0; i < casoN/2; i++) {
                                for (int j = 0; j < casoN/2; j++) {
                                    System.out.println("\n\nC " + (i+cont1)  + "," + (j) + " = " + matrizC4[i][j]);
                                }
                            }
                            
                        }

                        /*se terminan los calculos y se cierra la conexion con del cliente */
                        conexionCliente.close();
                        break;
    
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
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