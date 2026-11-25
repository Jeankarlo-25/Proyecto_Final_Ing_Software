package excepciones;

/** Excepción base del dominio GameStore. Las demás excepciones propias heredan de ella. */
public class GameStoreException extends Exception {

    public GameStoreException(String mensaje) {
        super(mensaje);
    }

    public GameStoreException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
