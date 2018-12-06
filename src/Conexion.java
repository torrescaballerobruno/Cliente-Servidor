
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.security.Key;
import java.util.InputMismatchException;
import javax.crypto.Cipher;

/**
 *Clase que se encarga del manejo de los clientes
 * @author bruno
 */
public class Conexion extends Thread{
    private Socket cliente;
    private Cuenta cuenta;
    private Key clave;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    
    /**
     * Constructor de conexion, que recibe lo necesario para tratar al cliente
     * @param cliente conexion del cliente
     * @param cuenta cuenta sobre la cual se van a efectuar las operaciones
     * @param clave clave con la que se encripta y desencripta
     */
    public Conexion(Socket cliente, Cuenta cuenta,Key clave){
        this.cliente = cliente;
        this.cuenta = cuenta;
        this.clave = clave;
        try{//Se obtienen los flujos del socket
            this.dis = new DataInputStream(cliente.getInputStream());
            this.dos = new DataOutputStream(cliente.getOutputStream());
        }catch(IOException e){
            System.err.println("Error: "+e.getMessage());
        }
    }
    /**
     * Todo el tratamiento del cliente
     */
    @Override
    public void run(){
        String cadena = null;
        String[] cadenas = null;
        BigDecimal monto = null,saldo = null;
        try{
            //Se manda el mensaje de bienvenida
            escribirCadena("\n\tBienvenido:   "+cuenta.getNombre());
            do{
                //Se separa la cadena cadena recibida
                cadenas = obtenerCadena().split(" ");
                cadenas[0] = cadenas[0].toUpperCase();
                //Se checa cual fue el comando que envio el cliente
                switch(cadenas[0]){
                    case "CONSULTA":
                        //se manda el saldo que tiene la cuenta
                        escribirCadena("Saldo: "+cuenta.getSaldo());
                        break;
                    case "DEPOSITAR":
                        try{
                            //Se checa que el segundo parametro si haya sido un numero
                            monto = new BigDecimal(cadenas[1]);
                        }catch(InputMismatchException | IndexOutOfBoundsException e){
                            escribirCadena("Error al depositar");
                            break;
                        }
                        
                        //Le agregamos la cantidad a la cuenta
                        cuenta.setSaldo(cuenta.getSaldo().add(new BigDecimal(cadenas[1])));
                        //Le retornamos el nuevo saldo
                        escribirCadena("Operacion Realizada con exito\nNuevo Saldo: "+cuenta.getSaldo());
                        break;
                    case "RETIRAR": 
                        //Obtenemos el saldo de la cuenta
                        saldo = cuenta.getSaldo();
                        monto = null;
                        try{
                            //Checamos que el segundo parametro si haya sido un numero
                            monto = new BigDecimal(cadenas[1]);
                        }catch(InputMismatchException | IndexOutOfBoundsException e){
                            escribirCadena("Error al retirar");
                            break;
                        }
                        //Verificamos que tenga el saldo suficiente
                        if(saldo.compareTo(monto) >= 0 ){
                            //Retiramos lo especificado
                            cuenta.setSaldo(cuenta.getSaldo().subtract(new BigDecimal(cadenas[1])));
                            //Retornamos el nuevo saldo
                            escribirCadena("Operacion Realizada con exito\nNuevo Saldo: "+cuenta.getSaldo());
                        }else{
                            escribirCadena("No cuenta con saldo suficiente");
                        }
                        
                        break;
                    case "SALIR":
                        //Log del servidor para ver quien termino la sesion
                        System.out.println("Conexion terminada: "+cliente.getInetAddress());
                        //Mandamos el salir para terminar la conexion en el cliente
                        escribirCadena("SALIR");
                        //Se termina la conexion en el servidor
                        cliente.close();
                        break;
                    default:
                        //Por si se equivoca
                        escribirCadena("Operacion no valida");
                        break;
                }
            }while(true);

        }catch(Exception e){
        }
    }
    /**
     * Se encarga de cifrar y enviar la cadena 
     * @param cadena cadena sin cifrar que se va a enviar
     */
    public void escribirCadena(String cadena){
        Cipher cifrar = null;
        byte[] textoCifrado = null;
        try{
            //se cifra la cadena
            cifrar = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cifrar.init(Cipher.ENCRYPT_MODE, clave);
            textoCifrado = cifrar.doFinal(cadena.getBytes("UTF8"));
            //Se escribe la cadena en el flujo
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