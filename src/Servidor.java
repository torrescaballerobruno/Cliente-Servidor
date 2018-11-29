
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author bruno
 */
public class Servidor {
    public static void main(String[] args) {
        ServerSocket ss =  null;
        Socket cliente = null;
        try{
            ss = new ServerSocket(Integer.parseInt(args[0]));
            while(true){
                cliente = ss.accept();
                Conexion conn = new Conexion(cliente, new Cuenta());
                conn.start();
                System.out.println("Cliente conectado desde: "+cliente.getInetAddress());
                cliente = null;
            }
        }catch(IndexOutOfBoundsException | IOException e){
            System.err.println("Error: "+e.getMessage());
        }
        
    }
}