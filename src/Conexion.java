
import java.net.Socket;

/**
 *
 * @author bruno
 */
public class Conexion extends Thread{
    private Socket cliente;
    private Cuenta cuenta;
    
    public Conexion(Socket cliente, Cuenta cuenta){
        this.cliente = cliente;
        this.cuenta = cuenta;
    }

    public Socket getCliente() {
        return cliente;
    }

    @Override
    public void run(){
        
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