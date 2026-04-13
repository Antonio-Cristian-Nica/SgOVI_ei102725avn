package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.OviUserDao;
import es.uji.ei1027.ovi.dao.TutorDao;
import es.uji.ei1027.ovi.model.OviUser;
import es.uji.ei1027.ovi.validator.OviUserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/oviuser")
public class OviUserController {

    private OviUserDao oviUserDao;
    private TutorDao tutorDao;

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setTutorDao(TutorDao tutorDao) {
        this.tutorDao = tutorDao;
    }

    @GetMapping("/register")
    public String mostrarPaginaRegistro(Model model) {
        model.addAttribute("oviUser", new OviUser());
        model.addAttribute("tutors", tutorDao.getTutors());
        return "oviuser/register";
    }


    // Este sirve para PROCESAR los datos enviados
    @PostMapping("/register") // <--- CAMBIADO DE /add A /register
    public String add(@ModelAttribute("oviUser") OviUser oviUser, BindingResult bindingResult, Model model) {

        OviUserValidator userValidator = new OviUserValidator();
        userValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("tutors", tutorDao.getTutors());
            return "oviuser/register";
        }

        oviUserDao.addOviUser(oviUser);
        return "redirect:/oviuser/list";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("oviUsers", oviUserDao.getOviUsers());
        return "oviuser/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("oviUser", new OviUser());
        model.addAttribute("tutors", tutorDao.getTutors());
        return "oviuser/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("oviUser") OviUser oviUser) {
        oviUserDao.addOviUser(oviUser);
        return "redirect:/oviuser/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("oviUser", oviUserDao.getOviUser(id));
        model.addAttribute("tutors", tutorDao.getTutors());
        return "oviuser/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable int id, @ModelAttribute("oviUser") OviUser oviUser) {
        oviUser.setOviID(id);
        oviUserDao.updateOviUser(oviUser);
        return "redirect:/oviuser/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        oviUserDao.deleteOviUser(id);
        return "redirect:/oviuser/list";
    }
}