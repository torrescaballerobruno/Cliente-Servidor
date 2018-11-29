
import java.io.DataInputStream;
import java.net.Socket;
import java.security.Key;
import java.util.Arrays;
import javax.crypto.Cipher;

/**
 *
 * @author bruno
 */
public class Conexion extends Thread{
    private Socket cliente;
    private Cuenta cuenta;
    private Key clave;
    
    public Conexion(Socket cliente, Cuenta cuenta,Key clave){
        this.cliente = cliente;
        this.cuenta = cuenta;
        this.clave = clave;
    }

    @Override
    public void run(){
        byte arreglo[] = new byte[24];
        try{
            String cadena = null;
            DataInputStream dis = new DataInputStream( cliente.getInputStream() );
            int numeroBytesLeidos = dis.read(arreglo);


        }catch(Exception e){
        }
    }
    
    public String obtenerCadena(byte arreglo[], int numeroBytesLeidos){
        
        if(numeroBytesLeidos == 8){
            byte arreglo2[] = new byte[8];
            for(int i = 0; i<8;i++)
                arreglo2[i] = arreglo[i];
            return desencriptarCadena(arreglo2);
        }else if(numeroBytesLeidos == 16){
            byte arreglo2[] = new byte[16];
            for(int i = 0; i<16;i++)
                arreglo2[i] = arreglo[i];
            return desencriptarCadena(arreglo2);
        }else{
            return desencriptarCadena(arreglo);
        }
    }
    
    public String desencriptarCadena(byte arreglo[]){

        try{

            Cipher cifrar = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cifrar.init(Cipher.DECRYPT_MODE, clave);
            byte[] textoPlano = cifrar.doFinal(arreglo);
            return new String(textoPlano, "UTF8");

        }catch(Exception e){
            System.err.println("Error:   "+e.getMessage());
        }
        return null;
    }

    public Socket getCliente() {
        return cliente;
    }

    public void setCliente(Socket cliente) {
        this.cliente = cliente;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }
}