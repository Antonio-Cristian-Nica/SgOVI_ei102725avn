package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.exceptions.SgOVIException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class SgOVIControllerAdvice {

    // Captura les excepcions personalitzades i mostra la vista d'error
    @ExceptionHandler(value = SgOVIException.class)
    public ModelAndView handleSgOVIException(SgOVIException ex) {
        ModelAndView mav = new ModelAndView("error");

        mav.addObject("message", ex.getMessage());
        mav.addObject("errorName", ex.getErrorName());

        return mav;
    }
}