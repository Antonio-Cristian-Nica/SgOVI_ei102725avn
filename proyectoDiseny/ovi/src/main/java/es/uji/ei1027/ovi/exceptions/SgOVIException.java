package es.uji.ei1027.ovi.exceptions;

/*
 Excepción de dominio que extiende RuntimeException. Se lanza desde DAOs/controllers para errores
 controlados (username duplicado, validación, etc). Hereda message del padre con super(message).
 */
public class SgOVIException extends RuntimeException {

    private final String errorName;

    public SgOVIException(String message, String errorName) {
        super(message);
        this.errorName = errorName;
    }

    public SgOVIException(String message, String errorName, Throwable cause) {
        super(message, cause);
        this.errorName = errorName;
    }

    public String getErrorName() {
        return errorName;
    }
}
