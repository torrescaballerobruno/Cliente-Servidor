import java.math.BigDecimal;

/**
 *
 * @author bruno
 */
public class Cuenta {

    private String nombre;  //El nombre del cliente
    private BigDecimal saldo;   //El saldo del cliente

    public Cuenta(){}

    public Cuenta(String nombre,BigDecimal saldo){      //Clase que va a tener el nombre del usuario y el saldo
        this.nombre = nombre;
        this.saldo = saldo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

}