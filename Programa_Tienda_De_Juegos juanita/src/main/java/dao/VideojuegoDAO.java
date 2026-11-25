package dao;

import bd.ConexionBD;
import excepciones.DatosInvalidosException;
import excepciones.GameStoreException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import modelo.Videojuego;

/** Guarda y consulta los videojuegos en la tabla "videojuego" de SQLite. */
public class VideojuegoDAO implements Repositorio<Videojuego> {

    @Override
    public Videojuego guardar(Videojuego v) throws GameStoreException {
        String sql = "INSERT INTO videojuego (nombre, genero, plataforma, precio, stock) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getNombre());
            ps.setString(2, v.getGenero());
            ps.setString(3, v.getPlataforma());
            ps.setDouble(4, v.getPrecio());
            ps.setInt(5, v.getStock());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    v.setId(keys.getInt(1));
                }
            }
            return v;
        } catch (SQLException e) {
            throw new GameStoreException("No se pudo guardar el videojuego: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Videojuego> buscarPorId(int id) throws GameStoreException {
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement("SELECT * FROM videojuego WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(convertir(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new GameStoreException("No se pudo consultar el videojuego: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Videojuego> listar() throws GameStoreException {
        List<Videojuego> lista = new ArrayList<>();
        try (Statement st = ConexionBD.getInstancia().getConexion().createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM videojuego ORDER BY id")) {
            while (rs.next()) {
                lista.add(convertir(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new GameStoreException("No se pudo cargar el catálogo: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Videojuego v) throws GameStoreException {
        String sql = "UPDATE videojuego SET nombre = ?, genero = ?, plataforma = ?, precio = ?, stock = ? WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, v.getNombre());
            ps.setString(2, v.getGenero());
            ps.setString(3, v.getPlataforma());
            ps.setDouble(4, v.getPrecio());
            ps.setInt(5, v.getStock());
            ps.setInt(6, v.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new GameStoreException("No se pudo actualizar el videojuego: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int id) throws GameStoreException {
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement("DELETE FROM videojuego WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new GameStoreException(
                    "No se puede eliminar el videojuego porque ya tiene compras asociadas.", e);
        }
    }

    private Videojuego convertir(ResultSet rs) throws SQLException, GameStoreException {
        try {
            return new Videojuego(rs.getInt("id"), rs.getString("nombre"), rs.getString("genero"),
                    rs.getString("plataforma"), rs.getDouble("precio"), rs.getInt("stock"));
        } catch (DatosInvalidosException e) {
            throw new GameStoreException("Datos de videojuego corruptos en la base de datos.", e);
        }
    }
}
