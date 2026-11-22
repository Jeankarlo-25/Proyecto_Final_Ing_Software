package excepciones;

/** Se lanza cuando el usuario escribe un dato vacío, con formato incorrecto o fuera de rango. */
public class DatosInvalidosException extends GameStoreException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
