package modelo;

import excepciones.DatosInvalidosException;

/**
 * Clase ABSTRACTA que modela el concepto general de "usuario" de la tienda.
 * Cliente y Administrador heredan de ella (HERENCIA) y definen sus métodos abstractos.
 */
public abstract class Usuario {

    // ENCAPSULAMIENTO: atributos privados
    private int id;
    private String nombre;
    private String usuario;
    private String contrasenaHash;

    protected Usuario(int id, String nombre, String usuario, String contrasenaHash)
            throws DatosInvalidosException {
        this.id = id;
        setNombre(nombre);
        setUsuario(usuario);
        setContrasenaHash(contrasenaHash);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    // ENCAPSULAMIENTO: se valida antes de cambiar el estado del objeto
    public void setNombre(String nombre) throws DatosInvalidosException {
        if (nombre == null || nombre.trim().length() < 3) {
            throw new DatosInvalidosException("El nombre debe tener al menos 3 caracteres.");
        }
        this.nombre = nombre.trim();
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) throws DatosInvalidosException {
        if (usuario == null || !usuario.trim().matches("[A-Za-z0-9_]{3,20}")) {
            throw new DatosInvalidosException(
                    "El usuario debe tener de 3 a 20 letras, números o guion bajo (sin espacios).");
        }
        this.usuario = usuario.trim();
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) throws DatosInvalidosException {
        if (contrasenaHash == null || contrasenaHash.isBlank()) {
            throw new DatosInvalidosException("La contraseña no puede estar vacía.");
        }
        this.contrasenaHash = contrasenaHash;
    }

    // ABSTRACCIÓN: cada tipo de usuario define estos métodos
    public abstract String getRol();

    public abstract boolean puedeAdministrar();
}
