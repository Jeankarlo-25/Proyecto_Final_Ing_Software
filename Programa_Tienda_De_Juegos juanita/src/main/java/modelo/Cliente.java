package modelo;

import excepciones.DatosInvalidosException;

/**
 * Cliente que compra videojuegos físicos en línea.
 * Especializa a Usuario agregando la dirección a la que se le envían los pedidos.
 */
public class Cliente extends Usuario {

    private String direccion;

    public Cliente(int id, String nombre, String usuario, String contrasenaHash, String direccion)
            throws DatosInvalidosException {
        super(id, nombre, usuario, contrasenaHash);
        setDireccion(direccion);
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) throws DatosInvalidosException {
        if (direccion == null || direccion.trim().length() < 5) {
            throw new DatosInvalidosException("La dirección de envío debe tener al menos 5 caracteres.");
        }
        this.direccion = direccion.trim();
    }

    @Override
    public String getRol() {
        return "CLIENTE";
    }

    @Override
    public boolean puedeAdministrar() {
        return false;
    }
}
