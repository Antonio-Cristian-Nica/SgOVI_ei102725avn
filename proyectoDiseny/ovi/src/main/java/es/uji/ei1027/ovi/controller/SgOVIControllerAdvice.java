package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.exceptions.SgOVIException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class SgOVIControllerAdvice {

    @ExceptionHandler(value = SgOVIException.class)
    public ModelAndView handleSgOVIException(SgOVIException ex) {
        // Creamos el contenedor 'ModelAndView' apuntando a la vista "error" (error.html)
        ModelAndView mav = new ModelAndView("error");

        // Añadimos los datos de la excepción para que Thymeleaf pueda leerlos
        mav.addObject("message", ex.getMessage());
        mav.addObject("errorName", ex.getErrorName());

        return mav;
    }
}
