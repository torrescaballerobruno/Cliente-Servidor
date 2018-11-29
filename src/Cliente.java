
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author bruno
 */
public class Cliente {
    public static void main(String[] args) {
        Socket s;
        try{
            s = new Socket(args[0],Integer.parseInt(args[1]));
        }catch(IndexOutOfBoundsException | IOException e){
            System.err.println("Error: "+e.getMessage());
        }
    }
}