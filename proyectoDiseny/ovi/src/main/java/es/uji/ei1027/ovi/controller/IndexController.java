package es.uji.ei1027.ovi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/noticias")
    public String noticias() {
        return "noticias";
    }

    @GetMapping("/ovi")
    public String ovi() {
        return "ovi";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }
}