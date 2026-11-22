package descuento;

/** 5 % de descuento si el subtotal es de $150 o más. */
public class DescuentoPorMonto implements EstrategiaDescuento {

    @Override
    public double calcularDescuento(double subtotal, int unidades) {
        return subtotal >= 150 ? subtotal * 0.05 : 0;
    }

    @Override
    public String getNombre() {
        return "5% por compras desde $150";
    }
}
