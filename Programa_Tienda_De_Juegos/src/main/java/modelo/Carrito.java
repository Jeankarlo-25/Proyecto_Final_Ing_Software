package modelo;

import excepciones.DatosInvalidosException;
import excepciones.StockInsuficienteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Carrito de compras del cliente. Vive en memoria hasta que se confirma la compra. */
public class Carrito {

    private final List<ItemCarrito> items = new ArrayList<>();

    public void agregar(Videojuego videojuego, int cantidad)
            throws DatosInvalidosException, StockInsuficienteException {
        if (cantidad <= 0) {
            throw new DatosInvalidosException("La cantidad debe ser mayor que 0.");
        }
        ItemCarrito existente = buscar(videojuego.getId());
        int yaEnCarrito = existente == null ? 0 : existente.getCantidad();

        if (yaEnCarrito + cantidad > videojuego.getStock()) {
            throw new StockInsuficienteException("Solo hay " + videojuego.getStock()
                    + " unidades de \"" + videojuego.getNombre() + "\".");
        }
        if (existente == null) {
            items.add(new ItemCarrito(videojuego, cantidad));
        } else {
            existente.sumarCantidad(cantidad);
        }
    }

    public boolean eliminar(int idVideojuego) {
        ItemCarrito item = buscar(idVideojuego);
        return item != null && items.remove(item);
    }

    public List<ItemCarrito> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getSubtotal() {
        double total = 0;
        for (ItemCarrito item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int getTotalUnidades() {
        int total = 0;
        for (ItemCarrito item : items) {
            total += item.getCantidad();
        }
        return total;
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }

    public void vaciar() {
        items.clear();
    }

    private ItemCarrito buscar(int idVideojuego) {
        for (ItemCarrito item : items) {
            if (item.getVideojuego().getId() == idVideojuego) {
                return item;
            }
        }
        return null;
    }
}
