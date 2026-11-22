package vista;

import excepciones.GameStoreException;
import fachada.TiendaFacade;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import modelo.Cliente;
import modelo.Usuario;

/** Ventana de inicio: iniciar sesión o registrarse como cliente. */
public class LoginFrame extends JFrame {

    private final TiendaFacade tienda;
    private final JTextField txtUsuario = new JTextField(15);
    private final JPasswordField txtContrasena = new JPasswordField(15);

    public LoginFrame(TiendaFacade tienda) {
        super("GameStore - Iniciar sesión");
        this.tienda = tienda;

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.add(new JLabel("Usuario:"));
        panel.add(txtUsuario);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtContrasena);

        JButton btnIngresar = new JButton("Ingresar");
        JButton btnRegistro = new JButton("Registrarse");
        panel.add(btnIngresar);
        panel.add(btnRegistro);

        btnIngresar.addActionListener(e -> ingresar());
        txtContrasena.addActionListener(e -> ingresar());
        btnRegistro.addActionListener(e -> registrar());

        setContentPane(panel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        try {
            Usuario u = tienda.iniciarSesion(txtUsuario.getText(), new String(txtContrasena.getPassword()));
            dispose();
            // POLIMORFISMO: se pregunta al Usuario (tipo padre) qué es, sin importar si es Cliente o Administrador
            if (u.puedeAdministrar()) {
                new AdminFrame(tienda, u).setVisible(true);
            } else {
                new ClienteFrame(tienda, (Cliente) u).setVisible(true);
            }
        } catch (GameStoreException ex) {
            UtilVista.error(this, ex.getMessage());
        }
    }

    private void registrar() {
        JTextField nombre = new JTextField();
        JTextField usuario = new JTextField();
        JPasswordField clave = new JPasswordField();
        JTextField direccion = new JTextField();
        Object[] campos = {"Nombre completo:", nombre, "Usuario:", usuario, "Contraseña (mín. 4):", clave,
            "Dirección de envío:", direccion};

        int opcion = JOptionPane.showConfirmDialog(this, campos, "Registro de cliente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            tienda.registrarCliente(nombre.getText(), usuario.getText(), new String(clave.getPassword()),
                    direccion.getText());
            UtilVista.info(this, "Registro exitoso. Ya puedes iniciar sesión.");
        } catch (GameStoreException ex) {
            UtilVista.error(this, ex.getMessage());
        }
    }
}
