package modelo;

import excepciones.DatosInvalidosException;

/** Videojuego físico (disco/cartucho) que se vende en la tienda. */
public class Videojuego {

    private int id;
    private String nombre;
    private String genero;
    private String plataforma;
    private double precio;
    private int stock;

    public Videojuego(int id, String nombre, String genero, String plataforma, double precio, int stock)
            throws DatosInvalidosException {
        this.id = id;
        setNombre(nombre);
        setGenero(genero);
        setPlataforma(plataforma);
        setPrecio(precio);
        setStock(stock);
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

    public void setNombre(String nombre) throws DatosInvalidosException {
        this.nombre = textoObligatorio(nombre, "El nombre");
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) throws DatosInvalidosException {
        this.genero = textoObligatorio(genero, "El género");
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) throws DatosInvalidosException {
        this.plataforma = textoObligatorio(plataforma, "La plataforma");
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) throws DatosInvalidosException {
        if (precio <= 0) {
            throw new DatosInvalidosException("El precio debe ser mayor que 0.");
        }
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) throws DatosInvalidosException {
        if (stock < 0) {
            throw new DatosInvalidosException("El stock no puede ser negativo.");
        }
        this.stock = stock;
    }

    private String textoObligatorio(String texto, String campo) throws DatosInvalidosException {
        if (texto == null || texto.isBlank()) {
            throw new DatosInvalidosException(campo + " no puede estar vacío.");
        }
        return texto.trim();
    }
}
