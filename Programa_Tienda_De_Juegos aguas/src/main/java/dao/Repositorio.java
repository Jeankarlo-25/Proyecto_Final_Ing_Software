package dao;

import excepciones.GameStoreException;
import java.util.List;
import java.util.Optional;

/**
 * INTERFAZ genérica de acceso a datos, implementada por UsuarioDAO, VideojuegoDAO y CompraDAO.
 * Los servicios la usan como tipo (DIP) sin saber que detrás hay SQLite.
 */
public interface Repositorio<T> {

    T guardar(T objeto) throws GameStoreException;

    Optional<T> buscarPorId(int id) throws GameStoreException;

    List<T> listar() throws GameStoreException;

    void actualizar(T objeto) throws GameStoreException;

    void eliminar(int id) throws GameStoreException;
}
