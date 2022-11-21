import java.net.Socket;
import java.net.ServerSocket;
import java.lang.Thread;
import java.util.Date;

import javafx.concurrent.Worker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * servidorweb
 */
public class servidorweb {

    static class worker extends Thread {
        
        Socket conexion;

        Worker(Socket conexion){
            this.conexion = conexion;
        }
        public void run(){
            try {

                BufferedReader entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                PrintWriter salida = new PrintWriter(conexion.getOutputStream());

                String s = entrada.readLine();
                System.out.println(s);

                if (s.startsWith("GET /hola")) {
                    String respuesta = "<html><button onclick='alert()'>"; ////////////////////////////////Termina esta parte sjsjsjsjsjs

                }else{
                    
                }
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }
}