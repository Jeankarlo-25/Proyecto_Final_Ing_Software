package modelo;

/** Una línea del carrito: un videojuego y la cantidad de copias que se quieren. */
public class ItemCarrito {

    private final Videojuego videojuego;
    private int cantidad;

    public ItemCarrito(Videojuego videojuego, int cantidad) {
        this.videojuego = videojuego;
        this.cantidad = cantidad;
    }

    public Videojuego getVideojuego() {
        return videojuego;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void sumarCantidad(int extra) {
        this.cantidad += extra;
    }

    public double getSubtotal() {
        return videojuego.getPrecio() * cantidad;
    }
}
