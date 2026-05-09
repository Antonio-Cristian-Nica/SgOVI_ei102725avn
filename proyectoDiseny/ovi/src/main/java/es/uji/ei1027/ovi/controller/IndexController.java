package es.uji.ei1027.ovi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    // Pàgina d'inici de l'aplicació
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Llistat de notícies
    @GetMapping("/noticias")
    public String noticias() {
        return "noticias";
    }

    // Informació sobre l'OVI
    @GetMapping("/ovi")
    public String ovi() {
        return "ovi";
    }

    // Formulari de contacte
    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    // Pàgina prèvia al registre (selecció OviUser / PapPati)
    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }
}