package modelo;

import excepciones.DatosInvalidosException;

/** Línea de una compra ya confirmada (guarda el nombre y precio del momento de la compra). */
public class DetalleCompra {

    private final int idVideojuego;
    private final String nombre;
    private final int cantidad;
    private final double precioUnitario;

    public DetalleCompra(int idVideojuego, String nombre, int cantidad, double precioUnitario)
            throws DatosInvalidosException {
        if (cantidad <= 0 || precioUnitario <= 0) {
            throw new DatosInvalidosException("Detalle de compra inválido.");
        }
        this.idVideojuego = idVideojuego;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdVideojuego() {
        return idVideojuego;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }
}
