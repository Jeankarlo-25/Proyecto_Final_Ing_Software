package descuento;

/**
 * PATRÓN DE COMPORTAMIENTO: Strategy.
 * Cada implementación es una forma distinta de calcular el descuento de una compra.
 * Sirve también como INTERFAZ propia (implementada por varias clases).
 */
public interface EstrategiaDescuento {

    /** Devuelve el valor (en dinero) que se descuenta. 0 si la estrategia no aplica. */
    double calcularDescuento(double subtotal, int unidades);

    String getNombre();
}
