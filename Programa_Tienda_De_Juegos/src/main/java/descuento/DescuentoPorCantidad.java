package descuento;

/** 10 % de descuento si el cliente compra 3 o más videojuegos. */
public class DescuentoPorCantidad implements EstrategiaDescuento {

    @Override
    public double calcularDescuento(double subtotal, int unidades) {
        return unidades >= 3 ? subtotal * 0.10 : 0;
    }

    @Override
    public String getNombre() {
        return "10% por comprar 3 o más videojuegos";
    }
}
