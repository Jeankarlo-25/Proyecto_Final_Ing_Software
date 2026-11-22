package servicio;

import dao.UsuarioRepositorio;
import excepciones.CredencialesInvalidasException;
import excepciones.DatosInvalidosException;
import excepciones.GameStoreException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import modelo.Cliente;
import modelo.Usuario;

/** Responsabilidad única: registrar clientes e iniciar sesión. */
public class AuthService {

    private final UsuarioRepositorio usuarios; // DIP: depende de la interfaz, no del DAO

    public AuthService(UsuarioRepositorio usuarios) {
        this.usuarios = usuarios;
    }

    public Cliente registrar(String nombre, String usuario, String contrasena, String direccion)
            throws GameStoreException {
        if (contrasena == null || contrasena.length() < 4) {
            throw new DatosInvalidosException("La contraseña debe tener al menos 4 caracteres.");
        }
        Cliente nuevo = new Cliente(0, nombre, usuario, hash(contrasena), direccion);
        if (usuarios.buscarPorNombreUsuario(nuevo.getUsuario()).isPresent()) {
            throw new DatosInvalidosException("Ese nombre de usuario ya existe.");
        }
        usuarios.guardar(nuevo);
        return nuevo;
    }

    public Usuario iniciarSesion(String usuario, String contrasena) throws GameStoreException {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isEmpty()) {
            throw new DatosInvalidosException("Escribe tu usuario y tu contraseña.");
        }
        Optional<Usuario> encontrado = usuarios.buscarPorNombreUsuario(usuario.trim());
        if (encontrado.isEmpty() || !encontrado.get().getContrasenaHash().equals(hash(contrasena))) {
            throw new CredencialesInvalidasException();
        }
        return encontrado.get();
    }

    // La contraseña nunca se guarda en texto plano: se guarda su hash SHA-256
    static String hash(String texto) throws GameStoreException {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new GameStoreException("No se pudo proteger la contraseña.", e);
        }
    }
}
