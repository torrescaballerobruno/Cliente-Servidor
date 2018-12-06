
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 *
 * @author bruno
 */
public class Servidor {
    public static void main(String[] args) {
        ServerSocket ss =  null;    //Declaracion de los sockets
        Socket cliente = null;
        Map<String,Cuenta> clientes = Servidor.crearMapa();
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

                //Se obtienen las credenciales de acceso
                String[] acceso = Servidor.obtenerCadena(new DataInputStream(cliente.getInputStream()), clave).split("_");
                //Se valida que vengan completas
                if(acceso.length<2){
                    cliente.close();
                    continue;
                }
                //Se verifica que exista el cliente y que sea la contraseña correcta
                Cuenta tmp = clientes.get(acceso[0]);
                if(tmp == null || !tmp.getPass().equals(acceso[1])){
                    cliente.close();
                    continue;
                }

                //Cada cliente se manda a un hilo que es quien lo va a tratar 
                Conexion conn = new Conexion(cliente, tmp,clave);
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
    /**
     * Retorna los usuarios registrados "Emula una base de datos"
     * @return Mapa de usuarios
     */
    public static Map<String,Cuenta> crearMapa(){
        Map<String,Cuenta> map = new HashMap();
        map.put("bruno123",new Cuenta("Bruno",new BigDecimal(20000),"proteco123"));
        map.put("juancho",new Cuenta("Juan",new BigDecimal(40000),"jn123"));
        map.put("pedrinisss",new Cuenta("Pedro",new BigDecimal(2000),"proteco4ever"));
        map.put("JuaninJuanHarry",new Cuenta("Juan Vazquez",new BigDecimal(26000),"juanin"));
        map.put("KREN",new Cuenta("Karen",new BigDecimal(68000),"karen1997"));
        map.put("Lissie",new Cuenta("Lisset",new BigDecimal(12000),"Lisss12@"));
        map.put("Kari",new Cuenta("Karina",new BigDecimal(21000),"Karinaina"));
        map.put("Ta-Ta",new Cuenta("Tania",new BigDecimal(65000),"TaniaChida"));
        return map;
    }
    
    /**
     * Lee una cadena del flujo de entrada de
     * @return cadena desencriptada
     * @throws IOException
     */
    public static String obtenerCadena(DataInputStream dis,Key clave) throws IOException{
        byte arreglo[] = new byte[24],arreglo2[] = null;
        int numeroBytesLeidos = dis.read(arreglo);
        switch (numeroBytesLeidos) {
            case 8:
                arreglo2 = new byte[8];
                for(int i = 0; i<8;i++)
                    arreglo2[i] = arreglo[i];
                return desencriptarCadena(arreglo2,clave);
            case 16:
                arreglo2 = new byte[16];
                for(int i = 0; i<16;i++)
                    arreglo2[i] = arreglo[i];
                return desencriptarCadena(arreglo2,clave);
            default:
                return desencriptarCadena(arreglo,clave);
        }
    }
    /**
     * Metodo que desencripta una cadena usando la clave del objeto
     * @param arreglo Cadena que se va a desencriptar
     * @return Cadena desencriptada
     */
    public static String desencriptarCadena(byte arreglo[], Key clave){
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
}