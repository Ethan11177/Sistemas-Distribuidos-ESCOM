import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.net.ServerSocket;

public class ServidorHTTP{
    
    static class Worker extends Thread {
        Socket conexion;
        Worker(Socket conexion){
            this.conexion = conexion;
        }

        public void run() {
            try {
                BufferedReader entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                PrintWriter salida = new PrintWriter(conexion.getOutputStream());
    
                String req = entrada.readLine();
                System.out.println(req);
    
                for(;;){
                    String encabezado = entrada.readLine();
                    //System.out.println(encabezado);
                    if (encabezado.equals("")) break;
                }
    
                if (req.startsWith("GET /hola")) {
                    String respuesta = "<html><button onclick='alert(\"Se presionó el botón\")'>Aceptar</button></html>";
                    salida.println("HTTP/1.1 200 OK");
                    salida.println("Content-type: text/html; charset=utf-8");
                    salida.println("Content-length: " + respuesta.length());
                    //salida.println("Server: ServidroHTTP.java");
                    //salida.println("Date" + new Date());
                    salida.println();
                    salida.flush();
                    salida.println(respuesta);
                    salida.flush();
                }else{
                    salida.println("HTTP/1.1 404 File Not Found");
                    salida.flush();
                }

                System.out.println(conexion.getRemoteSocketAddress().toString());

            } 
            catch (Exception e) {
                //TODO: handle exception
            }
        }
    }

    

    public static void main(String[] args) throws Exception{
        try (ServerSocket servidor = new ServerSocket(50000)) {
            for(;;){
                Socket conexion = servidor.accept();
                Worker w = new Worker(conexion);
                w.start();
            }
        }
    }
}
