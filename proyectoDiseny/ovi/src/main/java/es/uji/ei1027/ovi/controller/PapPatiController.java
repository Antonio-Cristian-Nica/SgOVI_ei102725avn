package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.PapPatiDao;
import es.uji.ei1027.ovi.model.PapPati;
import es.uji.ei1027.ovi.validator.PapPatiValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/papPati")
public class PapPatiController {

    private PapPatiDao papPatiDao;

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    // 1. MÉTODOS PARA EL REGISTRO (OPERACIÓN ADD)

    // Muestra el formulario vacío
    @RequestMapping("/add")
    public String addPapPati(Model model) {
        // Creamos un objeto vacío y lo pasamos al modelo para que Thymeleaf lo use en th:object
        model.addAttribute("pappati", new PapPati());
        return "papPati/add";
    }

    // Procesa los datos enviados desde el formulario
    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("pappati") PapPati papPati,
                                   BindingResult bindingResult) {

        // INSTANCIAMOS Y EJECUTAMOS EL VALIDADOR
        // Nota: Le pasamos el objeto 'papPati' con los datos y 'bindingResult' donde dejará los errores
        PapPatiValidator papPatiValidator = new PapPatiValidator();
        papPatiValidator.validate(papPati, bindingResult);

        // CONTROL DE ERRORES:
        // Si el validador ha encontrado fallos, bindingResult.hasErrors() será true
        if (bindingResult.hasErrors()) {
            // Volvemos a la vista del formulario (pappati/add.html)
            // IMPORTANTE: NO usamos redirect aquí, porque perderíamos los mensajes de error
            return "pappati/add";
        }

        // SI NO HAY ERRORES:
        // Llamamos al DAO para persistir los datos en la base de datos
        papPatiDao.addPapPati(papPati);

        // REDIRECCIÓN (Post-Redirect-Get):
        // Una vez guardado, redirigimos al listado para evitar que el usuario
        // cree duplicados al refrescar la página (F5)
        return "redirect:list";
    }

    // 2. MÉTODO PARA EL LISTADO (OPCIONAL PARA PROBAR)
    @RequestMapping("/list")
    public String listPapPatis(Model model) {
        model.addAttribute("pappatis", papPatiDao.getPapPatis());
        return "pappati/list";
    }
}
