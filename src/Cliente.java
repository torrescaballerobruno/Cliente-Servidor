
import java.io.DataInputStream;
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
        //Se declaran las variables que se van a usar
        Socket s = null;
        Scanner sc = null;
        String texto = null,mensaje = null;
        ObjectInputStream ois = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try{
            sc = new Scanner(System.in);
            //Se crea el socket con los parametros pasados por linea de comandos
            s = new Socket(args[0],Integer.parseInt(args[1]));
            //Se lee la llave de encriptacion
            ois = new ObjectInputStream(new FileInputStream("llave.ser"));
            Key clave = (Key)ois.readObject();
            ois.close();
            //Se obtienen los flujos de entrada y salida del socket
            dos = new DataOutputStream( s.getOutputStream());
            dis = new DataInputStream( s.getInputStream() );
            //Se imprime en pantalla el primer mensaje que envia el servidor
            System.out.println(Cliente.obtenerCadena(dis, clave));
            do{
                //Se imprime el menu de con las operaciones que se pueden realizar
                Cliente.menu();
                texto = sc.nextLine();
                //Se envia el mensaje que se lee desde el teclado
                Cliente.enviarMensaje(texto.getBytes("UTF8"), dos, clave);
                //obtiene el mensaje que envia el servidor
                mensaje = Cliente.obtenerCadena(dis, clave);
                //Validar que si es "SALIR" no lo imprima en pantalla
                //Esto se hace por presentacion
                if(!mensaje.equals("SALIR"))
                    System.out.println(mensaje);
            }while(!mensaje.equals("SALIR"));  //ciclo hasta que el servidor responda con "SALIR"
            //Se cierra los flujos y el socket
            dos.close();
            dis.close();
            s.close();
            //Impresion en pnatalla si es que ocurrio algun error
        }catch(IndexOutOfBoundsException | IOException | ClassNotFoundException e){
            System.err.println("Error: "+e.getMessage());
        }
    }

    public static void menu(){          //Impresion a pantalla de lo que se puede realizar
        System.out.println("\nOperaciones que se pueden realizar");
        System.out.println("\tConsulta");
        System.out.println("\tRetirar (cantidad)");
        System.out.println("\tDepositar (cantidad)\n");
    }
    /**
     * Obtiene la cadena que se lee desde el socket ya desencriptada
     * @param dis   Flujo de entrada de donde leer
     * @param clave Clave para la desencriptacion
     * @return la cadena que se lee desde el socket
     * @throws IOException 
     */
    public static String obtenerCadena(DataInputStream dis, Key clave) throws IOException{  
        
        byte arreglo[] = new byte[1024],arreglo2[] = null, tmp[] = null;
        //lee el arreglo de bytes, y  lo guarda en un arreglo temporal
        int numeroBytesLeidos = dis.read(arreglo);
        //Si el arreglo es de distinto tamaño a los bytes leidos
        if(numeroBytesLeidos != arreglo.length){
            //instanciamos un arreglo con el numro de bytes que debe de tener
            tmp = new byte[numeroBytesLeidos];
            //copiamos los elementos del arreglo, en este proyecto no nos sirvio la funcion de Arrays.copyOfRange
            for(int i = 0; i< numeroBytesLeidos ; i++)
                tmp[i] = arreglo[i];
            //Mandamos a desencriptar la cadena
            return Cliente.desencriptarCadena(tmp,clave);
        }else{
            //Mandamos a desencriptar la cadena
            return Cliente.desencriptarCadena(arreglo,clave);
        }
    }
    /**
     * Desencripta la cadena que se le pasa
     * @param arreglo Cadena a desencriptar
     * @param clave Llave que se va a usar para desencriptar la cadena
     * @return La cadena ya desencriptada
     */
    public static String desencriptarCadena(byte arreglo[],Key clave){
        Cipher cifrar = null;
        byte[] textoPlano = null;
        try{
            //Se crean y se configura los objetos que son necesarios para desencriptar la cadena
            cifrar = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cifrar.init(Cipher.DECRYPT_MODE, clave);
            //Se desencripta la cadena 
            textoPlano = cifrar.doFinal(arreglo);
            // se crea un objeto de cadena pasandole el arrego y el codigo
            return new String(textoPlano, "UTF8");

        }catch(Exception e){//Mensaje si ocurre algun error
            System.err.println("Error:   "+e.getMessage());
        }
        return null;
    }
    /**
     * Envia el mensaje al servidor
     * @param mensaje mensaje que se va a envira
     * @param dos Flujo de salida donde se va a escribir
     * @param clave Clave para encriptar el mensaje antes de enviar
     */
    public static void enviarMensaje(byte[] mensaje, DataOutputStream dos, Key clave){
        Cipher cifrar = null;
        byte[] textoCifrado = null;
        try{
            //Se instancia y se configuran los objetos necesarios para cifrar 
            cifrar = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cifrar.init(Cipher.ENCRYPT_MODE, clave);
            //Se encripta el mensaje
            textoCifrado = cifrar.doFinal(mensaje);
            //Se escribe en el flujos
            dos.write(textoCifrado);
        }catch(Exception e){ //Mensaje si ocurre algun error
            System.err.println("Error: "+e.getMessage());
        }
    }
}