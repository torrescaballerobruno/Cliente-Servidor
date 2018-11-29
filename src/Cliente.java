
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author bruno
 */
public class Cliente {
    
    public static void main(String[] args) {
        Socket s = null;
        Scanner sc = new Scanner(System.in);
        try{

            s = new Socket(args[0],Integer.parseInt(args[1]));
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("llave.ser"));
            Key clave = (Key)ois.readObject();
            ois.close();
            DataOutputStream dos = new DataOutputStream( s.getOutputStream());

            do{
                String texto = sc.nextLine();
                if(texto.equals("Salir")) break;
                
                Cliente.enviarMensaje(texto.getBytes("UTF8"), dos, clave);
            }while(true);
            

        }catch(IndexOutOfBoundsException | IOException | ClassNotFoundException e){
            System.err.println("Error: "+e.getMessage());
        }
    }
    
    public static void enviarMensaje(byte[] mensaje, DataOutputStream dos, Key clave){
        try{
            Cipher cifrar = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cifrar.init(Cipher.ENCRYPT_MODE, clave);
            byte[] textoCifrado = cifrar.doFinal(mensaje);
            System.out.println("Texto Cifrado:  "+textoCifrado.toString());
            dos.write(textoCifrado);
        }catch(Exception e){
            System.err.println("Error: "+e.getMessage());
        }
    }
}
