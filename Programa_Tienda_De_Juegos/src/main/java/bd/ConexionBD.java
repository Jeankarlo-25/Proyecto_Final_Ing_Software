package bd;

import excepciones.GameStoreException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class ConexionBD {

    private static final String URL = "jdbc:sqlite:gamestore.db";
    private static ConexionBD instancia;

    private Connection conexion;

    private ConexionBD() {
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public synchronized Connection getConexion() throws GameStoreException {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL);
                try (Statement st = conexion.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON");
                }
            }
            return conexion;
        } catch (SQLException e) {
            throw new GameStoreException("No se pudo conectar a la base de datos: " + e.getMessage(), e);
        }
    }

    /** Crea las tablas y los datos iniciales ejecutando schema.sql. */
    public void inicializar() throws GameStoreException {
        try (InputStream in = ConexionBD.class.getResourceAsStream("/schema.sql")) {
            if (in == null) {
                throw new GameStoreException("No se encontró el script schema.sql.");
            }
            StringBuilder script = new StringBuilder();
            for (String linea : new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\n")) {
                if (!linea.trim().startsWith("--")) {
                    script.append(linea).append('\n');
                }
            }
            try (Statement st = getConexion().createStatement()) {
                for (String sql : script.toString().split(";")) {
                    if (!sql.isBlank()) {
                        st.execute(sql.trim());
                    }
                }
            }
        } catch (IOException | SQLException e) {
            throw new GameStoreException("No se pudo crear la base de datos: " + e.getMessage(), e);
        }
    }
}
