package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final String NEXT_URL = "nextUrl";
    private static final String REDIRECT_LOGIN = "redirect:/login";

    private PapPatiDao papPatiDao;
    private OviUserDao oviUserDao;
    private CredentialsDao credentialsDao;


    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setCredentialsDao(CredentialsDao credentialsDao) {
        this.credentialsDao = credentialsDao;
    }

    @RequestMapping("/portal")
    public String portal(HttpSession session) {
        if (session.getAttribute("user") == null) {
            session.setAttribute(NEXT_URL, "/admin/portal");
            return REDIRECT_LOGIN;
        }
        return "admin/portal";
    }

    // =====================================================================
    // VALIDAR PAP/PATI
    // =====================================================================
    @RequestMapping("/validarPapPati")
    public String validarPapPati(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            session.setAttribute(NEXT_URL, "/admin/validarPapPati");
            return REDIRECT_LOGIN;
        }
        model.addAttribute("pappatis", papPatiDao.getPapPatisPendents());
        return "admin/validarPapPati";
    }

    @RequestMapping(value = "/validarPapPati/{username}", method = RequestMethod.POST)
    public String activarPapPati(@PathVariable("username") String username,
                                 HttpSession session) {
        if (session.getAttribute("user") == null) {
            return REDIRECT_LOGIN;
        }
        credentialsDao.activateCredentials(username);
        papPatiDao.activatePapPati(username);
        return "redirect:/admin/validarPapPati";
    }

    // =====================================================================
    // VALIDAR OVI USERS
    // =====================================================================
    @RequestMapping("/validarOviUsers")
    public String validarOviUsers(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            session.setAttribute(NEXT_URL, "/admin/validarOviUsers");
            return REDIRECT_LOGIN;
        }
        model.addAttribute("oviusers", oviUserDao.getOviUsersPendents());
        return "admin/validarOviUsers";
    }

    @RequestMapping(value = "/validarOviUsers/{username}", method = RequestMethod.POST)
    public String activarOviUser(@PathVariable("username") String username,
                                 HttpSession session) {
        if (session.getAttribute("user") == null) {
            return REDIRECT_LOGIN;
        }
        credentialsDao.activateCredentials(username);
        oviUserDao.activateOviUser(username);
        return "redirect:/admin/validarOviUsers";
    }
}
