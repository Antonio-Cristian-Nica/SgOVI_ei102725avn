package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.TutorDao;
import es.uji.ei1027.ovi.model.Tutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tutor")
public class TutorController {

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
        return "redirect:/tutor/list";
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
        return "redirect:/tutor/list";
    }

    // BORRAR
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        tutorDao.deleteTutor(id);
        return "redirect:/tutor/list";
    }
}