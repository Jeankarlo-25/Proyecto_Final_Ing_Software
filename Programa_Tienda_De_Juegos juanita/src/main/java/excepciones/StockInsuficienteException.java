package excepciones;

/** Se lanza cuando se pide más unidades de un videojuego de las que hay en inventario. */
public class StockInsuficienteException extends GameStoreException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
