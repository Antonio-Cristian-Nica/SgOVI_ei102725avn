package es.uji.ei1027.ovi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SgOVIErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        if (statusCode == null) {
            model.addAttribute("status", "???");
            model.addAttribute("error", "Error desconegut");
        } else if (statusCode == 404) {
            model.addAttribute("status", "404");
            model.addAttribute("error", "Pàgina no trobada");
        } else if (statusCode == 500) {
            model.addAttribute("status", "500");
            model.addAttribute("error", "Error intern del servidor");
        } else {
            model.addAttribute("status", statusCode);
            model.addAttribute("error", "S'ha produït un error inesperat");
        }

        model.addAttribute("message", "Si el problema persisteix, contacta amb l'administrador.");
        return "error";
    }
}
