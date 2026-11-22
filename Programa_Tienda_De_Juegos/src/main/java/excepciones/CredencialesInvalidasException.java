package excepciones;

/** Se lanza cuando el usuario o la contraseña no coinciden al iniciar sesión. */
public class CredencialesInvalidasException extends GameStoreException {

    public CredencialesInvalidasException() {
        super("Usuario o contraseña incorrectos.");
    }
}
