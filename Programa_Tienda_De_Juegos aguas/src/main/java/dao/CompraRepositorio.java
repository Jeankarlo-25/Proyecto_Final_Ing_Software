package dao;

import excepciones.GameStoreException;
import java.util.List;
import modelo.Compra;

/** Interfaz específica para compras: agrega la consulta de las compras de un cliente. */
public interface CompraRepositorio extends Repositorio<Compra> {

    List<Compra> listarPorCliente(int idCliente) throws GameStoreException;
}
