package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.instructor.InstructorDao;
import es.uji.ei1027.ovi.model.Credentials;
import es.uji.ei1027.ovi.model.Instructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/instructor")
public class InstructorController {

    private static final String REDIRECT_LOGIN = "redirect:/login";
    private InstructorDao instructorDao;

    @Autowired
    public void setInstructorDao(InstructorDao instructorDao) {
        this.instructorDao = instructorDao;
    }

    @RequestMapping("/portal")
    public String portal(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return REDIRECT_LOGIN;
        }
        Credentials credentials = (Credentials) session.getAttribute("user");
        Instructor instructor = instructorDao.getInstructorByUsername(credentials.getUsername());
        model.addAttribute("instructor", instructor);
        return "instructor/portal";
    }
}
