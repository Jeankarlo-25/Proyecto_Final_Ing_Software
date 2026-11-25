package fachada;

import bd.ConexionBD;
import dao.CompraDAO;
import dao.UsuarioDAO;
import dao.VideojuegoDAO;
import descuento.DescuentoPorCantidad;
import descuento.DescuentoPorMonto;
import descuento.EstrategiaDescuento;
import descuento.SinDescuento;
import excepciones.GameStoreException;
import java.util.Arrays;
import java.util.List;
import modelo.Carrito;
import modelo.Cliente;
import modelo.Compra;
import modelo.Usuario;
import modelo.Videojuego;
import servicio.AuthService;
import servicio.CatalogoService;
import servicio.CompraService;

/**
 * PATRÓN ESTRUCTURAL: Facade.
 * Ofrece a la interfaz gráfica UNA sola puerta de entrada simple y esconde los DAO,
 * los servicios, la base de datos y las estrategias de descuento.
 * Es también el único lugar donde se eligen las clases concretas (inyección de dependencias).
 */
public class TiendaFacade {

    private final AuthService auth;
    private final CatalogoService catalogo;
    private final CompraService compras;

    public TiendaFacade() throws GameStoreException {
        ConexionBD.getInstancia().inicializar();

        this.auth = new AuthService(new UsuarioDAO());
        this.catalogo = new CatalogoService(new VideojuegoDAO());
        List<EstrategiaDescuento> estrategias =
                Arrays.asList(new SinDescuento(), new DescuentoPorCantidad(), new DescuentoPorMonto());
        this.compras = new CompraService(new CompraDAO(), estrategias);
    }

    /** Constructor alterno: recibe los servicios ya armados (útil para pruebas). */
    public TiendaFacade(AuthService auth, CatalogoService catalogo, CompraService compras) {
        this.auth = auth;
        this.catalogo = catalogo;
        this.compras = compras;
    }

    // ---- Usuarios ----
    public Usuario iniciarSesion(String usuario, String contrasena) throws GameStoreException {
        return auth.iniciarSesion(usuario, contrasena);
    }

    public Cliente registrarCliente(String nombre, String usuario, String contrasena, String direccion)
            throws GameStoreException {
        return auth.registrar(nombre, usuario, contrasena, direccion);
    }

    // ---- Catálogo ----
    public List<Videojuego> listarVideojuegos() throws GameStoreException {
        return catalogo.listar();
    }

    public List<Videojuego> buscarVideojuegos(String texto) throws GameStoreException {
        return catalogo.buscar(texto);
    }

    public void agregarVideojuego(String nombre, String genero, String plataforma, String precio, String stock)
            throws GameStoreException {
        catalogo.agregar(nombre, genero, plataforma, precio, stock);
    }

    public void modificarVideojuego(int id, String nombre, String genero, String plataforma, String precio,
            String stock) throws GameStoreException {
        catalogo.modificar(id, nombre, genero, plataforma, precio, stock);
    }

    public void eliminarVideojuego(int id) throws GameStoreException {
        catalogo.eliminar(id);
    }

    // ---- Compras ----
    public double descuentoDe(Carrito carrito) {
        return compras.calcularDescuento(carrito);
    }

    public String nombreDescuento(Carrito carrito) {
        return compras.mejorEstrategia(carrito).getNombre();
    }

    public Compra comprar(Cliente cliente, Carrito carrito, String direccionEnvio) throws GameStoreException {
        return compras.confirmarCompra(cliente, carrito, direccionEnvio);
    }

    public List<Compra> comprasDe(Cliente cliente) throws GameStoreException {
        return compras.listarDeCliente(cliente);
    }

    public List<Compra> todasLasCompras() throws GameStoreException {
        return compras.listarTodas();
    }

    public void marcarEnviada(int idCompra) throws GameStoreException {
        compras.marcarComoEnviada(idCompra);
    }
}
