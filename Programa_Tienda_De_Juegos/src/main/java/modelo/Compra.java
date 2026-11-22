package modelo;

import excepciones.DatosInvalidosException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Compra confirmada: videojuegos físicos que se envían a la dirección del cliente. */
public class Compra {

    private int id;
    private final int idCliente;
    private final String nombreCliente;
    private final LocalDateTime fecha;
    private final String direccionEnvio;
    private final double subtotal;
    private final double descuento;
    private EstadoPedido estado;
    private final List<DetalleCompra> detalles;

    public Compra(int id, int idCliente, String nombreCliente, LocalDateTime fecha, String direccionEnvio,
            double subtotal, double descuento, EstadoPedido estado, List<DetalleCompra> detalles)
            throws DatosInvalidosException {
        if (direccionEnvio == null || direccionEnvio.trim().length() < 5) {
            throw new DatosInvalidosException("La dirección de envío debe tener al menos 5 caracteres.");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new DatosInvalidosException("La compra no tiene videojuegos.");
        }
        this.id = id;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.fecha = fecha;
        this.direccionEnvio = direccionEnvio.trim();
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.estado = estado;
        this.detalles = new ArrayList<>(detalles);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public double getTotal() {
        return subtotal - descuento;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) throws DatosInvalidosException {
        if (estado == null) {
            throw new DatosInvalidosException("El estado del pedido no puede ser nulo.");
        }
        this.estado = estado;
    }

    public List<DetalleCompra> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }
}
