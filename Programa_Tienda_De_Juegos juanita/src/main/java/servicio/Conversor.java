package servicio;

import excepciones.DatosInvalidosException;

/** Convierte el texto escrito por el usuario en números, con mensajes de error claros. */
public final class Conversor {

    private Conversor() {
    }

    public static double aDecimal(String texto, String campo) throws DatosInvalidosException {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException | NullPointerException e) {
            throw new DatosInvalidosException(campo + " debe ser un número válido.");
        }
    }

    public static int aEntero(String texto, String campo) throws DatosInvalidosException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new DatosInvalidosException(campo + " debe ser un número entero.");
        }
    }
}
