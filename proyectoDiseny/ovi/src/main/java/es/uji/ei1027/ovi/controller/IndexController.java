package es.uji.ei1027.ovi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/") // Mapea la ruta raíz (http://localhost:8080/)
    public String index() {
        return "index";
    }
}