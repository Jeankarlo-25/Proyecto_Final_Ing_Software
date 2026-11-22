package dao;

import bd.ConexionBD;
import excepciones.DatosInvalidosException;
import excepciones.GameStoreException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import modelo.Administrador;
import modelo.Cliente;
import modelo.Usuario;

/** Guarda y consulta usuarios en la tabla "usuario" de SQLite. */
public class UsuarioDAO implements UsuarioRepositorio {

    @Override
    public Usuario guardar(Usuario u) throws GameStoreException {
        String sql = "INSERT INTO usuario (nombre, usuario, contrasena, rol, direccion) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getContrasenaHash());
            ps.setString(4, u.getRol());
            ps.setString(5, u instanceof Cliente ? ((Cliente) u).getDireccion() : null);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    u.setId(keys.getInt(1));
                }
            }
            return u;
        } catch (SQLException e) {
            throw new GameStoreException("No se pudo guardar el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorNombreUsuario(String usuario) throws GameStoreException {
        return buscar("SELECT * FROM usuario WHERE usuario = ?", usuario);
    }

    private Optional<Usuario> buscar(String sql, String valor) throws GameStoreException {
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(convertir(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new GameStoreException("No se pudo consultar el usuario: " + e.getMessage(), e);
        }
    }

    // Convierte una fila de la tabla en un Cliente o un Administrador según el rol
    private Usuario convertir(ResultSet rs) throws SQLException, GameStoreException {
        try {
            if ("ADMIN".equals(rs.getString("rol"))) {
                return new Administrador(rs.getInt("id"), rs.getString("nombre"), rs.getString("usuario"),
                        rs.getString("contrasena"));
            }
            return new Cliente(rs.getInt("id"), rs.getString("nombre"), rs.getString("usuario"),
                    rs.getString("contrasena"), rs.getString("direccion"));
        } catch (DatosInvalidosException e) {
            throw new GameStoreException("Datos de usuario corruptos en la base de datos.", e);
        }
    }
}
