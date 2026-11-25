package dao;

import excepciones.GameStoreException;
import java.util.Optional;
import modelo.Usuario;

/**
 * Interfaz pequeña solo con lo que necesita AuthService (ISP):
 * registrar un usuario y buscarlo por su nombre de usuario.
 */
public interface UsuarioRepositorio {

    Usuario guardar(Usuario usuario) throws GameStoreException;

    Optional<Usuario> buscarPorNombreUsuario(String usuario) throws GameStoreException;
}
