package dao;

import bd.ConexionBD;
import excepciones.DatosInvalidosException;
import excepciones.GameStoreException;
import excepciones.StockInsuficienteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import modelo.Compra;
import modelo.DetalleCompra;
import modelo.EstadoPedido;

/** Guarda y consulta compras (tablas "compra" y "detalle_compra"). */
public class CompraDAO implements CompraRepositorio {

    private static final String SELECT_COMPRA =
            "SELECT c.*, u.nombre AS nombre_cliente FROM compra c JOIN usuario u ON u.id = c.id_cliente ";

    /** Guarda la compra y descuenta el stock en UNA sola transacción: o se hace todo o nada. */
    @Override
    public Compra guardar(Compra compra) throws GameStoreException {
        Connection con = ConexionBD.getInstancia().getConexion();
        try {
            con.setAutoCommit(false);
            try {
                int idCompra = insertarCompra(con, compra);
                for (DetalleCompra d : compra.getDetalles()) {
                    descontarStock(con, d);
                    insertarDetalle(con, idCompra, d);
                }
                con.commit();
                compra.setId(idCompra);
                return compra;
            } catch (SQLException | GameStoreException e) {
                con.rollback();
                if (e instanceof GameStoreException) {
                    throw (GameStoreException) e;
                }
                throw new GameStoreException("No se pudo guardar la compra: " + e.getMessage(), e);
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new GameStoreException("Error en la transacción de la compra: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Compra> buscarPorId(int id) throws GameStoreException {
        List<Compra> lista = consultar("WHERE c.id = ?", id);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public List<Compra> listar() throws GameStoreException {
        return consultar("", null);
    }

    @Override
    public List<Compra> listarPorCliente(int idCliente) throws GameStoreException {
        return consultar("WHERE c.id_cliente = ?", idCliente);
    }

    @Override
    public void actualizar(Compra compra) throws GameStoreException {
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement("UPDATE compra SET estado = ? WHERE id = ?")) {
            ps.setString(1, compra.getEstado().name());
            ps.setInt(2, compra.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new GameStoreException("No se pudo actualizar la compra: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int id) throws GameStoreException {
        Connection con = ConexionBD.getInstancia().getConexion();
        try {
            con.setAutoCommit(false);
            try (PreparedStatement d = con.prepareStatement("DELETE FROM detalle_compra WHERE id_compra = ?");
                    PreparedStatement c = con.prepareStatement("DELETE FROM compra WHERE id = ?")) {
                d.setInt(1, id);
                d.executeUpdate();
                c.setInt(1, id);
                c.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new GameStoreException("No se pudo eliminar la compra: " + e.getMessage(), e);
        }
    }

    private int insertarCompra(Connection con, Compra c) throws SQLException, GameStoreException {
        String sql = "INSERT INTO compra (id_cliente, fecha, direccion, subtotal, descuento, total, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getIdCliente());
            ps.setString(2, c.getFecha().toString());
            ps.setString(3, c.getDireccionEnvio());
            ps.setDouble(4, c.getSubtotal());
            ps.setDouble(5, c.getDescuento());
            ps.setDouble(6, c.getTotal());
            ps.setString(7, c.getEstado().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new GameStoreException("No se obtuvo el número de la compra.");
        }
    }

    private void descontarStock(Connection con, DetalleCompra d) throws SQLException, StockInsuficienteException {
        String sql = "UPDATE videojuego SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getCantidad());
            ps.setInt(2, d.getIdVideojuego());
            ps.setInt(3, d.getCantidad());
            if (ps.executeUpdate() == 0) {
                throw new StockInsuficienteException("No hay stock suficiente de \"" + d.getNombre() + "\".");
            }
        }
    }

    private void insertarDetalle(Connection con, int idCompra, DetalleCompra d) throws SQLException {
        String sql = "INSERT INTO detalle_compra (id_compra, id_videojuego, nombre, cantidad, precio_unitario) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCompra);
            ps.setInt(2, d.getIdVideojuego());
            ps.setString(3, d.getNombre());
            ps.setInt(4, d.getCantidad());
            ps.setDouble(5, d.getPrecioUnitario());
            ps.executeUpdate();
        }
    }

    private List<Compra> consultar(String where, Integer parametro) throws GameStoreException {
        List<Compra> lista = new ArrayList<>();
        Connection con = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(SELECT_COMPRA + where + " ORDER BY c.id DESC")) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(convertir(con, rs));
                }
            }
            return lista;
        } catch (SQLException | DatosInvalidosException e) {
            throw new GameStoreException("No se pudieron consultar las compras: " + e.getMessage(), e);
        }
    }

    private Compra convertir(Connection con, ResultSet rs) throws SQLException, DatosInvalidosException {
        int id = rs.getInt("id");
        List<DetalleCompra> detalles = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM detalle_compra WHERE id_compra = ?")) {
            ps.setInt(1, id);
            try (ResultSet d = ps.executeQuery()) {
                while (d.next()) {
                    detalles.add(new DetalleCompra(d.getInt("id_videojuego"), d.getString("nombre"),
                            d.getInt("cantidad"), d.getDouble("precio_unitario")));
                }
            }
        }
        return new Compra(id, rs.getInt("id_cliente"), rs.getString("nombre_cliente"),
                LocalDateTime.parse(rs.getString("fecha")), rs.getString("direccion"),
                rs.getDouble("subtotal"), rs.getDouble("descuento"),
                EstadoPedido.valueOf(rs.getString("estado")), detalles);
    }
}
