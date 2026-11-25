package vista;

import java.awt.Component;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/** Utilidades pequeñas compartidas por las ventanas. */
final class UtilVista {

    private UtilVista() {
    }

    static String dinero(double valor) {
        return String.format(Locale.US, "$%.2f", valor);
    }

    static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "GameStore", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Modelo de tabla de solo lectura. */
    static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }
}
