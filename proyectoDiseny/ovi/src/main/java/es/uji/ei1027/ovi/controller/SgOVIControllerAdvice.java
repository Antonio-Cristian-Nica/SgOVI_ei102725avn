package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.exceptions.SgOVIException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/*
    Clase global anotada con @ControllerAdvice que captura excepciones lanzadas en cualquier controller.
    Tiene handlers en cascada de más específico (SgOVIException) a más genérico (Exception).
 */

@ControllerAdvice
public class SgOVIControllerAdvice {

    // Excepcions de domini llançades a propòsit
    @ExceptionHandler(SgOVIException.class)
    public ModelAndView handleSgOVIException(SgOVIException ex) {
        return buildErrorView(ex.getMessage(), ex.getErrorName());
    }

    // Fallback per a duplicats no capturats explícitament en els DAO
    @ExceptionHandler(DuplicateKeyException.class)
    public ModelAndView handleDuplicateKey(DuplicateKeyException ex) {
        return buildErrorView(
                "Ja existeix una entrada amb aquestes dades. Revisa els camps únics.",
                "Dada duplicada");
    }

    // Fallback per a la resta d'errors d'accés a dades
    @ExceptionHandler(DataAccessException.class)
    public ModelAndView handleDataAccess(DataAccessException ex) {
        return buildErrorView(
                "S'ha produït un error en accedir a les dades. Torna-ho a provar més tard.",
                "Error de base de dades");
    }

    // Últim recurs per a qualsevol excepció no controlada
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        return buildErrorView(
                "S'ha produït un error inesperat.",
                "Error intern");
    }

    private ModelAndView buildErrorView(String message, String errorName) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", message);
        mav.addObject("errorName", errorName);
        return mav;
    }
}