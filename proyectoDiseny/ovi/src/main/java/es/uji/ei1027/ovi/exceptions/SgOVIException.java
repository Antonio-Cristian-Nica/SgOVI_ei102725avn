package es.uji.ei1027.ovi.exceptions;

public class SgOVIException extends RuntimeException {
    private String message;    // Para mostrar en la vista
    private String errorName;  // Identificador del error

    public SgOVIException(String message, String errorName) {
        this.message = message;
        this.errorName = errorName;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }
}
