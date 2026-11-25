package modelo;

import excepciones.DatosInvalidosException;

/** Administrador de la tienda: gestiona el catálogo y los pedidos. */
public class Administrador extends Usuario {

    public Administrador(int id, String nombre, String usuario, String contrasenaHash)
            throws DatosInvalidosException {
        super(id, nombre, usuario, contrasenaHash);
    }

    @Override
    public String getRol() {
        return "ADMIN";
    }

    @Override
    public boolean puedeAdministrar() {
        return true;
    }
}
