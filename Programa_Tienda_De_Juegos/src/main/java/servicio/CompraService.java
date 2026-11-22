package servicio;

import dao.CompraRepositorio;
import descuento.EstrategiaDescuento;
import excepciones.DatosInvalidosException;
import excepciones.GameStoreException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import modelo.Carrito;
import modelo.Cliente;
import modelo.Compra;
import modelo.DetalleCompra;
import modelo.EstadoPedido;
import modelo.ItemCarrito;

/**
 * Responsabilidad única: confirmar compras y consultar pedidos.
 * Usa el patrón Strategy: elige la mejor EstrategiaDescuento de la lista que recibe.
 * Para agregar un nuevo descuento solo se crea otra clase; este servicio no cambia (OCP).
 */
public class CompraService {

    private final CompraRepositorio compras; // DIP
    private final List<EstrategiaDescuento> estrategias; // DIP + OCP

    public CompraService(CompraRepositorio compras, List<EstrategiaDescuento> estrategias) {
        this.compras = compras;
        this.estrategias = estrategias;
    }

    /** Devuelve la estrategia que más descuento le da al carrito (polimorfismo sobre la interfaz). */
    public EstrategiaDescuento mejorEstrategia(Carrito carrito) {
        EstrategiaDescuento mejor = estrategias.get(0);
        for (EstrategiaDescuento e : estrategias) {
            if (e.calcularDescuento(carrito.getSubtotal(), carrito.getTotalUnidades())
                    > mejor.calcularDescuento(carrito.getSubtotal(), carrito.getTotalUnidades())) {
                mejor = e;
            }
        }
        return mejor;
    }

    public double calcularDescuento(Carrito carrito) {
        double d = mejorEstrategia(carrito).calcularDescuento(carrito.getSubtotal(), carrito.getTotalUnidades());
        return Math.round(d * 100) / 100.0;
    }

    public Compra confirmarCompra(Cliente cliente, Carrito carrito, String direccionEnvio)
            throws GameStoreException {
        if (carrito.estaVacio()) {
            throw new DatosInvalidosException("El carrito está vacío.");
        }
        List<DetalleCompra> detalles = new ArrayList<>();
        for (ItemCarrito item : carrito.getItems()) {
            detalles.add(new DetalleCompra(item.getVideojuego().getId(), item.getVideojuego().getNombre(),
                    item.getCantidad(), item.getVideojuego().getPrecio()));
        }
        Compra compra = new Compra(0, cliente.getId(), cliente.getNombre(), LocalDateTime.now(), direccionEnvio,
                carrito.getSubtotal(), calcularDescuento(carrito), EstadoPedido.PENDIENTE, detalles);

        compras.guardar(compra); // descuenta el stock y guarda la compra en una transacción
        carrito.vaciar();
        return compra;
    }

    public List<Compra> listarTodas() throws GameStoreException {
        return compras.listar();
    }

    public List<Compra> listarDeCliente(Cliente cliente) throws GameStoreException {
        return compras.listarPorCliente(cliente.getId());
    }

    public void marcarComoEnviada(int idCompra) throws GameStoreException {
        Compra compra = compras.buscarPorId(idCompra)
                .orElseThrow(() -> new DatosInvalidosException("La compra no existe."));
        compra.setEstado(EstadoPedido.ENVIADO);
        compras.actualizar(compra);
    }
}
