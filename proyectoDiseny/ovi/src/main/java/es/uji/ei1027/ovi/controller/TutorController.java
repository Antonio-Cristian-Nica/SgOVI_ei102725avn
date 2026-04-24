package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.oviusertutor.TutorDao;
import es.uji.ei1027.ovi.model.Tutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tutor")
public class TutorController {
    private static final String REDIRECT_TUTOR_LIST = "redirect:/tutor/list";
    private TutorDao tutorDao;

    @Autowired
    public void setTutorDao(TutorDao tutorDao) {
        this.tutorDao = tutorDao;
    }

    // LISTAR
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("tutors", tutorDao.getTutors());
        return "tutor/list";
    }

    // MOSTRAR FORMULARIO AÑADIR
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("tutor", new Tutor());
        return "tutor/add";
    }

    // GUARDAR NUEVO
    @PostMapping("/add")
    public String add(@ModelAttribute("tutor") Tutor tutor) {
        tutorDao.addTutor(tutor);
        return REDIRECT_TUTOR_LIST;
    }

    // MOSTRAR FORMULARIO EDITAR
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("tutor", tutorDao.getTutor(id));
        return "tutor/edit";
    }

    // GUARDAR CAMBIOS
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable int id, @ModelAttribute("tutor") Tutor tutor) {
        tutor.setTutorID(id);
        tutorDao.updateTutor(tutor);
        return REDIRECT_TUTOR_LIST;
    }

    // BORRAR
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        tutorDao.deleteTutor(id);
        return REDIRECT_TUTOR_LIST;
    }
}