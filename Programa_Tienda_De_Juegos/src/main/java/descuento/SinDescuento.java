package descuento;

/** Estrategia por defecto: no se aplica descuento. */
public class SinDescuento implements EstrategiaDescuento {

    @Override
    public double calcularDescuento(double subtotal, int unidades) {
        return 0;
    }

    @Override
    public String getNombre() {
        return "Sin descuento";
    }
}
