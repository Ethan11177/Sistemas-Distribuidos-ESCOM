import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.ClosedSelectorException;
import java.util.Date;

import javax.sound.sampled.Port;
import javax.xml.crypto.Data;

import java.net.ServerSocket;

public class Tarea1ServidorB {

    static int totFlag = 0;
    static Object obj = new Object();

///////////////////////////////////////////////////////////////////////////////////////////////////wORKER2//////////////////////////////////////////////////////////////////////////////////////////////////
    static class Worker2 extends Thread {
        
        int port = 0, flag =0;
        long Ninicio = 0, Nfinal = 0;
        long number = 0;

        Worker2(int port, long Ninicio, long Nfinal, long number){
            this.port = port;
            this.Ninicio = Ninicio;
            this.Nfinal = Nfinal;
            this.number = number;
        }

        @Override
        public void run() {
            
            //System.out.println("Este seria el puerto para el servidor: " + port);

            try {
                Socket conexion = new Socket("localhost", port);

                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                salida.writeLong(Ninicio);  //Inicio de los n terminos que se van a calcular

                salida.writeLong(Nfinal);   //Final de los n terminos que se van a calcular

                salida.writeLong(number);   //Numero que se ingreso por medio de la url

                flag = entrada.readInt();

                if (flag == 1) {
                    totFlag = flag;
                    System.out.println("NO ES PRIMO SOCKET: " + port);
                }else{
                    System.out.println("ES PRIMO SOCKET: " + port);
                }

                conexion.close();

            } catch (Exception e) {
                //System.err.println(e.getMessage();
            }
        }
        
    }
////////////////////////////////////////////////////////////////////////////////////////////////////WORKER//////////////////////////////////////////////////////////////////////////////////////////////////
    static class Worker extends Thread{
        
        Socket conexion;

        Worker(Socket conexion){
            this.conexion = conexion;
        }

        @Override
        public void run() {
            try {
                
                BufferedReader entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));          //Aqui se hace la inicializacion de datos para la entrada de la url y la salida.
                PrintWriter salida = new PrintWriter(conexion.getOutputStream());

                
                String enUrl = entrada.readLine();      //se leen la entrada de la url para poder hacer el chequeo de los datos que contiene.
                System.out.println(enUrl);

                char[] chUrl = enUrl.toCharArray();     //conversion de datos para poder hacer la manipulacion. Creacion de variables para poder hacer la manipulacion.
                char[] number = new char[12];
                int flag = 0;
                int j = 0;

                //NOTA: este programa solo aceptara numero no mayores a 10 digitos, tomando en cuenta dos espacios de basura que deja. Para hacer que acepte mas digitos, se debe de cambiar el valor de la variable number y el segundo ciclo for.

                ///////////////////////////////////////////////Lectura de datos para poder dejando de lado todos los datos que son de tipo no numero
                for (int i = 0; i < enUrl.length(); i++) {
                    
                    if (chUrl[i] == '=') {
                        flag = 1;
                    }
                    if (flag == 1) {
                        number[j] = chUrl[i+1];
                        j++;
                        //System.out.println("cosa: " + chUrl[i] + " numero: " + j);
                        if (chUrl[i+1]== ' ') {
                            break;
                        }
                    }
                }
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                j = 0;

                //////////////////////////////////////////////Aqui se hace la lectura de cuantos digitos se han leido para poder hacer que este sistema pueda hacer la conversion y eliminacion de basura mas adelante.
                for (int i = 0; i <= 12; i++) {
                    if (number[i] != ' ') {
                        j++;
                    }
                    else{
                        break;
                    }
                }
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                char[] number2 = new char[j];       //Creacion del areglo para poder almacenar los digitos del numero ahora sin basura, del storage correcto para cada numero.

                for (int i = 0; i < j; i++) {
                    number2[i] = number[i];
                }
                
                enUrl = String.valueOf(number2);

                System.out.println("Longitud del numero: " + enUrl.length());

                long Fnumber = Long.parseLong(enUrl);

                System.out.println("Este es el numero: " + Fnumber);

                for(;;){

                    String head = entrada.readLine();
                    //System.out.println(head);
                    if(head.equals("")) break;

                }

                ///////////////////////////////////////////Princiopio para el envio de datos a las Instancias de Servidor A creando sus respectivos hilos con el puerto que estos van a tener en sus respectivas instancias

                try {

                    //////////////////////////////////////////Calculo e inicializacion de limites para el Servidor A;
                    long[] Nlimits = new long[8];

                    Nlimits[0] = (Fnumber/2)/4;
                    Nlimits[1] = Nlimits[0] + 1;
                    Nlimits[2] = Nlimits[0] * 2;
                    Nlimits[3] = Nlimits[2] + 1;
                    Nlimits[4] = Nlimits[0] * 3;
                    Nlimits[5] = Nlimits[4] + 1;
                    Nlimits[6] = Fnumber - 1;


                    //System.out.println(Nlimits[0]);

                    Worker2 w1 = new Worker2(50001, 2, Nlimits[0], Fnumber);

                    Worker2 w2 = new Worker2(50002, Nlimits[1], Nlimits[2], Fnumber);

                    Worker2 w3 = new Worker2(50003, Nlimits[3], Nlimits[4], Fnumber);

                    Worker2 w4 = new Worker2(50004, Nlimits[5], Nlimits[6], Fnumber);

                    w1.start();
                    w2.start();
                    w3.start();
                    w4.start();
                    
                    w1.join();
                    w2.join();
                    w3.join();
                    w4.join();

                    if (totFlag == 1) {
                        String respuesta = "<html><h1>NO ES PRIMO</h1></html>";
                        salida.println("HTTP/1.1 200 OK");
                        salida.println("Content-type: text/html; charset=utf-8");
                        salida.println("Content-length: " + respuesta.length());
                        //salida.println("Server: ServidroHTTP.java");
                        //salida.println("Date" + new Date());
                        salida.println();
                        salida.flush();
                        salida.println(respuesta);
                        salida.flush();
                    }
                    else{
                        String respuesta = "<html><h1>ES PRIMO</h1></html>";
                        salida.println("HTTP/1.1 200 OK");
                        salida.println("Content-type: text/html; charset=utf-8");
                        salida.println("Content-length: " + respuesta.length());
                        //salida.println("Server: ServidroHTTP.java");
                        //salida.println("Date" + new Date());
                        salida.println();
                        salida.flush();
                        salida.println(respuesta);
                        salida.flush();
                    }
                    totFlag = 0;
                } catch (Exception e) {
                    //System.err.println(e.getMessage());
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            } catch (Exception e) {
                //System.err.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception{
        
        try (ServerSocket server = new ServerSocket(50000)) {
            
            for(;;){
                Socket conexion = server.accept();
                Worker w = new Worker(conexion);
                w.start();
            }

        } catch (Exception e) {
            //System.err.println(e.getMessage());
        }
    }    
}
