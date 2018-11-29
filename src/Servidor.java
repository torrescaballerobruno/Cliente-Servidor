
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
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
        ServerSocket ss =  null;    //Declaracion de los sockets
        Socket cliente = null;
        
        try{
            //Se genera la clave
            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(56);
            Key clave = keyGen.generateKey();
            //Se guarda la clave en el archivo
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("llave.ser"));
            oos.writeObject(clave);
            oos.close();

            //Se crea el socket servidor
            ss = new ServerSocket(Integer.parseInt(args[0]));
            while(true){
                //Se espera la conexion de los clientes
                cliente = ss.accept();
                //Cada cliente se manda a un hilo que es quien lo va a tratar 
                Conexion conn = new Conexion(cliente, new Cuenta("Bruno",new BigDecimal(20000)),clave);
                //Se ejecuta el hilo 
                conn.start();
                //Impresion de pantalla de la direccion de donde se conectaron
                System.out.println("Cliente conectado desde: "+cliente.getInetAddress());
                //Se inicializa el socket para evitar conflictos
                cliente = null;
            }
        }catch( Exception e){   //Impresion a pantalla si llega a ocurrir algun error
            System.err.println("Error: "+e.getMessage());
        }
    }
}