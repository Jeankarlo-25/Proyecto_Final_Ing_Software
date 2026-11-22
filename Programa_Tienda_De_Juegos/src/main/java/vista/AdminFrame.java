package vista;

import excepciones.GameStoreException;
import fachada.TiendaFacade;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.Compra;
import modelo.DetalleCompra;
import modelo.Usuario;
import modelo.Videojuego;

/** Ventana del administrador: gestiona el catálogo (CRUD) y los pedidos. */
public class AdminFrame extends JFrame {

    private final TiendaFacade tienda;

    private final DefaultTableModel modeloCatalogo =
            UtilVista.modelo("ID", "Videojuego", "Género", "Plataforma", "Precio", "Stock");
    private final DefaultTableModel modeloPedidos =
            UtilVista.modelo("N°", "Cliente", "Fecha", "Dirección de envío", "Total", "Estado");
    private final JTable tablaCatalogo = new JTable(modeloCatalogo);
    private final JTable tablaPedidos = new JTable(modeloPedidos);

    private final JTextField txtNombre = new JTextField();
    private final JTextField txtGenero = new JTextField();
    private final JTextField txtPlataforma = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtStock = new JTextField();
    private final JTextArea txtDetalle = new JTextArea(5, 40);

    private List<Videojuego> catalogoActual = List.of();
    private List<Compra> pedidosActuales = List.of();

    public AdminFrame(TiendaFacade tienda, Usuario admin) {
        super("GameStore - Administrador: " + admin.getNombre());
        this.tienda = tienda;

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Catálogo", panelCatalogo());
        tabs.addTab("Pedidos", panelPedidos());
        tabs.addChangeListener(e -> refrescarTodo());

        add(tabs);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 560);
        setLocationRelativeTo(null);
        refrescarTodo();
    }

    private JPanel panelCatalogo() {
        tablaCatalogo.getSelectionModel().addListSelectionListener(e -> llenarFormulario());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(new EmptyBorder(8, 8, 8, 8));
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Género:"));
        form.add(txtGenero);
        form.add(new JLabel("Plataforma:"));
        form.add(txtPlataforma);
        form.add(new JLabel("Precio:"));
        form.add(txtPrecio);
        form.add(new JLabel("Stock (unidades físicas):"));
        form.add(txtStock);

        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar seleccionado");
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        JButton btnLimpiar = new JButton("Limpiar");
        btnAgregar.addActionListener(e -> agregar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnAgregar);
        botones.add(btnModificar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel abajo = new JPanel(new BorderLayout());
        abajo.add(form, BorderLayout.CENTER);
        abajo.add(botones, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tablaCatalogo), BorderLayout.CENTER);
        panel.add(abajo, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel panelPedidos() {
        txtDetalle.setEditable(false);
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> mostrarDetalle());

        JButton btnEnviado = new JButton("Marcar como enviado");
        btnEnviado.addActionListener(e -> marcarEnviado());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnEnviado);

        JPanel abajo = new JPanel(new BorderLayout());
        abajo.add(new JScrollPane(txtDetalle), BorderLayout.CENTER);
        abajo.add(botones, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);
        panel.add(abajo, BorderLayout.SOUTH);
        return panel;
    }

    private void refrescarTodo() {
        try {
            catalogoActual = tienda.listarVideojuegos();
            modeloCatalogo.setRowCount(0);
            for (Videojuego v : catalogoActual) {
                modeloCatalogo.addRow(new Object[]{v.getId(), v.getNombre(), v.getGenero(), v.getPlataforma(),
                    UtilVista.dinero(v.getPrecio()), v.getStock()});
            }
            pedidosActuales = tienda.todasLasCompras();
            modeloPedidos.setRowCount(0);
            for (Compra c : pedidosActuales) {
                modeloPedidos.addRow(new Object[]{c.getId(), c.getNombreCliente(),
                    c.getFecha().toString().replace('T', ' ').substring(0, 16), c.getDireccionEnvio(),
                    UtilVista.dinero(c.getTotal()), c.getEstado()});
            }
            txtDetalle.setText("");
        } catch (GameStoreException ex) {
            UtilVista.error(this, ex.getMessage());
        }
    }

    private void llenarFormulario() {
        int fila = tablaCatalogo.getSelectedRow();
        if (fila >= 0 && fila < catalogoActual.size()) {
            Videojuego v = catalogoActual.get(fila);
            txtNombre.setText(v.getNombre());
            txtGenero.setText(v.getGenero());
            txtPlataforma.setText(v.getPlataforma());
            txtPrecio.setText(String.valueOf(v.getPrecio()));
            txtStock.setText(String.valueOf(v.getStock()));
        }
    }

    private void limpiarFormulario() {
        tablaCatalogo.clearSelection();
        txtNombre.setText("");
        txtGenero.setText("");
        txtPlataforma.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
    }

    private void agregar() {
        try {
            tienda.agregarVideojuego(txtNombre.getText(), txtGenero.getText(), txtPlataforma.getText(),
                    txtPrecio.getText(), txtStock.getText());
            limpiarFormulario();
            refrescarTodo();
        } catch (GameStoreException ex) {
            UtilVista.error(this, ex.getMessage());
        }
    }

    private void modificar() {
        int fila = tablaCatalogo.getSelectedRow();
        if (fila < 0) {
            UtilVista.error(this, "Selecciona un videojuego de la tabla.");
            return;
        }
        try {
            tienda.modificarVideojuego(catalogoActual.get(fila).getId(), txtNombre.getText(),
                    txtGenero.getText(), txtPlataforma.getText(), txtPrecio.getText(), txtStock.getText());
            refrescarTodo();
        } catch (GameStoreException ex) {
            UtilVista.error(this, ex.getMessage());
        }
    }

    private void eliminar() {
        int fila = tablaCatalogo.getSelectedRow();
        if (fila < 0) {
            UtilVista.error(this, "Selecciona un videojuego de la tabla.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Eliminar este videojuego?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            tienda.eliminarVideojuego(catalogoActual.get(fila).getId());
            limpiarFormulario();
            refrescarTodo();
        } catch (GameStoreException ex) {
            UtilVista.error(this, ex.getMessage());
        }
    }

    private void mostrarDetalle() {
        int fila = tablaPedidos.getSelectedRow();
        if (fila < 0 || fila >= pedidosActuales.size()) {
            return;
        }
        Compra c = pedidosActuales.get(fila);
        StringBuilder sb = new StringBuilder("Pedido N° " + c.getId() + " - " + c.getNombreCliente() + "\n");
        for (DetalleCompra d : c.getDetalles()) {
            sb.append("  ").append(d.getNombre()).append(" x").append(d.getCantidad()).append("  ")
                    .append(UtilVista.dinero(d.getSubtotal())).append('\n');
        }
        sb.append("Subtotal: ").append(UtilVista.dinero(c.getSubtotal())).append("  Descuento: -")
                .append(UtilVista.dinero(c.getDescuento())).append("  Total: ")
                .append(UtilVista.dinero(c.getTotal()));
        txtDetalle.setText(sb.toString());
    }

    private void marcarEnviado() {
        int fila = tablaPedidos.getSelectedRow();
        if (fila < 0) {
            UtilVista.error(this, "Selecciona un pedido de la tabla.");
            return;
        }
        try {
            tienda.marcarEnviada(pedidosActuales.get(fila).getId());
            refrescarTodo();
        } catch (GameStoreException ex) {
            UtilVista.error(this, ex.getMessage());
        }
    }
}
