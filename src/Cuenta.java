import java.math.BigDecimal;

/**
 *
 * @author bruno
 */
public class Cuenta {

    private String nombre;
    private BigDecimal saldo;

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