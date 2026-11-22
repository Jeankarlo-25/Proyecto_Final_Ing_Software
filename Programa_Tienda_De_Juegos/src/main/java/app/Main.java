package app;

import excepciones.GameStoreException;
import fachada.TiendaFacade;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import vista.LoginFrame;

/** Punto de entrada de GameStore. */
public class Main {

    public static void main(String[] args) {
        try {
            TiendaFacade tienda = new TiendaFacade(); // crea la base de datos si no existe
            SwingUtilities.invokeLater(() -> new LoginFrame(tienda).setVisible(true));
        } catch (GameStoreException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "No se pudo iniciar GameStore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
