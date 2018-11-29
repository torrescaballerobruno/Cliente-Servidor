
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.security.Key;
import java.util.InputMismatchException;
import javax.crypto.Cipher;

/**
 *
 * @author bruno
 */
public class Conexion extends Thread{
    private Socket cliente;
    private Cuenta cuenta;
    private Key clave;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    
    public Conexion(Socket cliente, Cuenta cuenta,Key clave){
        this.cliente = cliente;
        this.cuenta = cuenta;
        this.clave = clave;
        try{
            this.dis = new DataInputStream(cliente.getInputStream());
            this.dos = new DataOutputStream(cliente.getOutputStream());
        }catch(IOException e){
            System.err.println("Error: "+e.getMessage());
        }
    }

    @Override
    public void run(){
        String cadena = null;
        String[] cadenas = null;
        BigDecimal monto = null,saldo = null;
        try{
            //Se manda el mensaje de bienvenida
            escribirCadena("\n\tBienvenido:   "+cuenta.getNombre());
            do{
                cadenas = obtenerCadena().split(" ");
                cadenas[0] = cadenas[0].toUpperCase();
                switch(cadenas[0]){
                    case "CONSULTA":
                        escribirCadena("Saldo: "+cuenta.getSaldo());
                        break;
                    case "DEPOSITAR":
                        try{
                            monto = new BigDecimal(cadenas[1]);
                        }catch(InputMismatchException | IndexOutOfBoundsException e){
                            escribirCadena("Error al retirar");
                            break;
                        }

                        cuenta.setSaldo(cuenta.getSaldo().add(new BigDecimal(cadenas[1])));
                        escribirCadena("Operacion Realizada con exito\nNuevo Saldo: "+cuenta.getSaldo());
                        break;
                    case "RETIRAR": 
                        saldo = cuenta.getSaldo();
                        monto = null;
                        try{
                            monto = new BigDecimal(cadenas[1]);
                        }catch(InputMismatchException | IndexOutOfBoundsException e){
                            escribirCadena("Error al retirar");
                            break;
                        }
                        if(saldo.compareTo(monto) >= 0 ){
                            cuenta.setSaldo(cuenta.getSaldo().subtract(new BigDecimal(cadenas[1])));
                            escribirCadena("Operacion Realizada con exito\nNuevo Saldo: "+cuenta.getSaldo());
                        }else{
                            escribirCadena("No cuenta con saldo suficiente");
                        }
                        
                        break;
                    case "SALIR":
                        System.out.println("Conexion terminada: "+cliente.getInetAddress());
                        escribirCadena("SALIR");
                        cliente.close();
                        break;
                    default:
                        escribirCadena("Operacion no valida");
                        break;
                }
            }while(true);

        }catch(Exception e){
        }
    }
    public void escribirCadena(String cadena){
        Cipher cifrar = null;
        byte[] textoCifrado = null;
        try{
            cifrar = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cifrar.init(Cipher.ENCRYPT_MODE, clave);
            textoCifrado = cifrar.doFinal(cadena.getBytes("UTF8"));
            dos.write(textoCifrado);
        }catch(Exception e){
            System.err.println("Error: "+e.getMessage());
        }
    }
    /**
     * Lee una cadena del flujo de entrada de
     * @return cadena desencriptada
     * @throws IOException
     */
    public String obtenerCadena() throws IOException{
        byte arreglo[] = new byte[24],arreglo2[] = null;
        int numeroBytesLeidos = dis.read(arreglo);
        switch (numeroBytesLeidos) {
            case 8:
                arreglo2 = new byte[8];
                for(int i = 0; i<8;i++)
                    arreglo2[i] = arreglo[i];
                return desencriptarCadena(arreglo2);
            case 16:
                arreglo2 = new byte[16];
                for(int i = 0; i<16;i++)
                    arreglo2[i] = arreglo[i];
                return desencriptarCadena(arreglo2);
            default:
                return desencriptarCadena(arreglo);
        }
    }
    /**
     * Metodo que desencripta una cadena usando la clave del objeto
     * @param arreglo Cadena que se va a desencriptar
     * @return Cadena desencriptada
     */
    public String desencriptarCadena(byte arreglo[]){
        //Declaracion de objetos a utilizar
        Cipher cifrar = null;
        byte[] textoPlano = null;
        try{
            //Se instancian y se configuran los objetos para desencriptar
            cifrar = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cifrar.init(Cipher.DECRYPT_MODE, clave);
            //Se descifra el texto
            textoPlano = cifrar.doFinal(arreglo);
            //Se retorna el texto plano
            return new String(textoPlano, "UTF8");
        }catch(Exception e){//Impresion a pantalla si ocurre algun error
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