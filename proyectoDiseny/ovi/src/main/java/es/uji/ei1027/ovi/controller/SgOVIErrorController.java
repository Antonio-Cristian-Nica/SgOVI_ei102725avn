package es.uji.ei1027.ovi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SgOVIErrorController implements ErrorController {
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        if (statusCode == null) {
            model.addAttribute(STATUS, "???");
            model.addAttribute(ERROR, "Error desconegut");
        } else if (statusCode == 404) {
            model.addAttribute(STATUS, "404");
            model.addAttribute(ERROR, "Pàgina no trobada");
        } else if (statusCode == 500) {
            model.addAttribute(STATUS, "500");
            model.addAttribute(ERROR, "Error intern del servidor");
        } else {
            model.addAttribute(STATUS, statusCode);
            model.addAttribute(ERROR, "S'ha produït un error inesperat");
        }

        model.addAttribute("message", "Si el problema persisteix, contacta amb l'administrador.");
        return ERROR;
    }
}
