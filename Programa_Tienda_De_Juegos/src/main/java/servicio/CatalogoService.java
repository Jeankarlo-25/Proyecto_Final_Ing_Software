package servicio;

import dao.Repositorio;
import excepciones.DatosInvalidosException;
import excepciones.GameStoreException;
import java.util.ArrayList;
import java.util.List;
import modelo.Videojuego;

/** Responsabilidad única: reglas del catálogo de videojuegos. */
public class CatalogoService {

    private final Repositorio<Videojuego> videojuegos; // DIP

    public CatalogoService(Repositorio<Videojuego> videojuegos) {
        this.videojuegos = videojuegos;
    }

    public List<Videojuego> listar() throws GameStoreException {
        return videojuegos.listar();
    }

    public List<Videojuego> buscar(String texto) throws GameStoreException {
        String filtro = texto == null ? "" : texto.trim().toLowerCase();
        List<Videojuego> resultado = new ArrayList<>();
        for (Videojuego v : videojuegos.listar()) {
            if (v.getNombre().toLowerCase().contains(filtro) || v.getGenero().toLowerCase().contains(filtro)
                    || v.getPlataforma().toLowerCase().contains(filtro)) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    public Videojuego agregar(String nombre, String genero, String plataforma, String precio, String stock)
            throws GameStoreException {
        Videojuego nuevo = new Videojuego(0, nombre, genero, plataforma,
                Conversor.aDecimal(precio, "El precio"), Conversor.aEntero(stock, "El stock"));
        return videojuegos.guardar(nuevo);
    }

    public void modificar(int id, String nombre, String genero, String plataforma, String precio, String stock)
            throws GameStoreException {
        Videojuego v = videojuegos.buscarPorId(id)
                .orElseThrow(() -> new DatosInvalidosException("El videojuego ya no existe."));
        v.setNombre(nombre);
        v.setGenero(genero);
        v.setPlataforma(plataforma);
        v.setPrecio(Conversor.aDecimal(precio, "El precio"));
        v.setStock(Conversor.aEntero(stock, "El stock"));
        videojuegos.actualizar(v);
    }

    public void eliminar(int id) throws GameStoreException {
        videojuegos.eliminar(id);
    }
}
