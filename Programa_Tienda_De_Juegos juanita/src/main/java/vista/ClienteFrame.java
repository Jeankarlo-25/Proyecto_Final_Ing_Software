package vista;

import excepciones.GameStoreException;
import fachada.TiendaFacade;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import modelo.Carrito;
import modelo.Cliente;
import modelo.Compra;
import modelo.DetalleCompra;
import modelo.ItemCarrito;
import modelo.Videojuego;

/** Ventana del cliente: ver catálogo, usar el carrito, comprar y ver sus pedidos. */
public class ClienteFrame extends JFrame {

    private final TiendaFacade tienda;
    private final Cliente cliente;
    private final Carrito carrito = new Carrito();

    private final DefaultTableModel modeloCatalogo =
            UtilVista.modelo("ID", "Videojuego", "Género", "Plataforma", "Precio", "Stock");
    private final DefaultTableModel modeloCarrito =
            UtilVista.modelo("ID", "Videojuego", "Cantidad", "Precio", "Subtotal");
    private final DefaultTableModel modeloCompras =
            UtilVista.modelo("N°", "Fecha", "Videojuegos", "Total", "Estado");

    private final JTable tablaCatalogo = new JTable(modeloCatalogo);
    private final JTable tablaCarrito = new JTable(modeloCarrito);
    private final JTextField txtBuscar = new JTextField(20);
    private final JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JTextField txtDireccion = new JTextField(30);
    private final JLabel lblResumen = new JLabel(" ");
    private List<Videojuego> catalogoActual = List.of();

    public ClienteFrame(TiendaFacade tienda, Cliente cliente) {
        super("GameStore - Cliente: " + cliente.getNombre());
        this.tienda = tienda;
        this.cliente = cliente;
        txtDireccion.setText(cliente.getDireccion());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Catálogo", panelCatalogo());
        tabs.addTab("Mi carrito", panelCarrito());
        tabs.addTab("Mis compras", panelCompras());
        tabs.addChangeListener(e -> refrescarTodo());

        add(tabs);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 520);
        setLocationRelativeTo(null);
        refrescarTodo();
    }

    private JPanel panelCatalogo() {
        JButton btnBuscar = new JButton("Buscar");
        JButton btnAgregar = new JButton("Agregar al carrito");
        btnBuscar.addActionListener(e -> cargarCatalogo());
        txtBuscar.addActionListener(e -> cargarCatalogo());
        btnAgregar.addActionListener(e -> agregarAlCarrito());

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(new JLabel("Buscar (nombre, género o plataforma):"));
        arriba.add(txtBuscar);
        arriba.add(btnBuscar);

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        abajo.add(new JLabel("Cantidad:"));
        abajo.add(spCantidad);
        abajo.add(btnAgregar);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaCatalogo), BorderLayout.CENTER);
        panel.add(abajo, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel panelCarrito() {
        JButton btnQuitar = new JButton("Quitar seleccionado");
        JButton btnComprar = new JButton("Confirmar compra");
        btnQuitar.addActionListener(e -> quitarDelCarrito());
        btnComprar.addActionListener(e -> comprar());

        JPanel direccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        direccion.add(new JLabel("Dirección de envío:"));
        direccion.add(txtDireccion);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnQuitar);
        botones.add(btnComprar);

        JPanel abajo = new JPanel(new BorderLayout());
        abajo.add(lblResumen, BorderLayout.NORTH);
        abajo.add(direccion, BorderLayout.CENTER);
        abajo.add(botones, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);
        panel.add(abajo, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel panelCompras() {
        JTable tabla = new JTable(modeloCompras);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void refrescarTodo() {
        cargarCatalogo();
        cargarCarrito();
        cargarCompras();
    }

    private void cargarCatalogo() {
        try {
            catalogoActual = tienda.buscarVideojuegos(txtBuscar.getText());
            modeloCatalogo.setRowCount(0);
            for (Videojuego v : catalogoActual) {
                modeloCatalogo.addRow(new Object[]{v.getId(), v.getNombre(), v.getGenero(), v.getPlataforma(),
                    UtilVista.dinero(v.getPrecio()), v.getStock()});
            }
        } catch (GameStoreException ex) {
            UtilVista.error(this, ex.getMessage());
        }
    }

    private void agregarAlCarrito() {
        int fila = tablaCatalogo.getSelectedRow();
        if (fila < 0) {
            UtilVista.error(this, "Selecciona un videojuego de la tabla.");
            return;
        }
        try {
            carrito.agregar(catalogoActual.get(fila), (Integer) spCantidad.getValue());
            cargarCarrito();
            UtilVista.info(this, "Agregado al carrito.");
        } catch (GameStoreException ex) {
            UtilVista.error(this, ex.getMessage());
        }
    }

    private void cargarCarrito() {
        modeloCarrito.setRowCount(0);
        for (ItemCarrito item : carrito.getItems()) {
            Videojuego v = item.getVideojuego();
            modeloCarrito.addRow(new Object[]{v.getId(), v.getNombre(), item.getCantidad(),
                UtilVista.dinero(v.getPrecio()), UtilVista.dinero(item.getSubtotal())});
        }
        double descuento = tienda.descuentoDe(carrito);
        lblResumen.setText("<html>Subtotal: " + UtilVista.dinero(carrito.getSubtotal())
                + " &nbsp;|&nbsp; Descuento (" + tienda.nombreDescuento(carrito) + "): -"
                + UtilVista.dinero(descuento) + " &nbsp;|&nbsp; <b>TOTAL: "
                + UtilVista.dinero(carrito.getSubtotal() - descuento) + "</b></html>");
    }

    private void quitarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila < 0) {
            UtilVista.error(this, "Selecciona un videojuego del carrito.");
            return;
        }
        carrito.eliminar((Integer) modeloCarrito.getValueAt(fila, 0));
        cargarCarrito();
    }

    private void comprar() {
        try {
            Compra compra = tienda.comprar(cliente, carrito, txtDireccion.getText());
            UtilVista.info(this, "Compra N° " + compra.getId() + " realizada. Total: "
                    + UtilVista.dinero(compra.getTotal()) + "\nSe enviará a: " + compra.getDireccionEnvio());
            refrescarTodo();
        } catch (GameStoreException ex) {
            UtilVista.error(this, ex.getMessage());
            refrescarTodo(); // el stock pudo cambiar
        }
    }

    private void cargarCompras() {
        try {
            modeloCompras.setRowCount(0);
            for (Compra c : tienda.comprasDe(cliente)) {
                StringBuilder juegos = new StringBuilder();
                for (DetalleCompra d : c.getDetalles()) {
                    juegos.append(d.getNombre()).append(" x").append(d.getCantidad()).append("; ");
                }
                modeloCompras.addRow(new Object[]{c.getId(), c.getFecha().toString().replace('T', ' ').substring(0, 16),
                    juegos.toString(), UtilVista.dinero(c.getTotal()), c.getEstado()});
            }
        } catch (GameStoreException ex) {
            UtilVista.error(this, ex.getMessage());
        }
    }
}
