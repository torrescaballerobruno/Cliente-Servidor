
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import javax.crypto.KeyGenerator;

/**
 *
 * @author bruno
 */
public class Servidor {
    public static void main(String[] args) {
        ServerSocket ss =  null;
        Socket cliente = null;
        
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(56);
            Key clave = keyGen.generateKey();
            System.out.println( "la clave del servidor es=" + clave );
            System.out.println( "Llave generada!" );
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("llave.ser"));
            oos.writeObject(clave);
            oos.close();

            ss = new ServerSocket(Integer.parseInt(args[0]));
            while(true){
                cliente = ss.accept();
                Conexion conn = new Conexion(cliente, new Cuenta(),clave);
                conn.start();
                System.out.println("Cliente conectado desde: "+cliente.getInetAddress());
                cliente = null;
            }
        }catch( Exception e){
            System.err.println("Error: "+e.getMessage());
        }
    }
}