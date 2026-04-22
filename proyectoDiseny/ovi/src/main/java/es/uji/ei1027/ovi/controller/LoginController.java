package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.Credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.OviUser.OviUserDao;
import es.uji.ei1027.ovi.dao.PapPati.PapPatiDao;
import es.uji.ei1027.ovi.model.Credentials;
import es.uji.ei1027.ovi.model.OviUser;
import es.uji.ei1027.ovi.model.PapPati;
import es.uji.ei1027.ovi.utils.PasswordUtils;
import es.uji.ei1027.ovi.validator.LoginValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private CredentialsDao credentialsDao;
    private OviUserDao oviUserDao;
    private PapPatiDao papPatiDao;

    @Autowired
    public void setCredentialsDao(CredentialsDao credentialsDao) {
        this.credentialsDao = credentialsDao;
    }

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @RequestMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("user", new Credentials());
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String processLogin(@ModelAttribute("user") Credentials user,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model) {

        LoginValidator loginValidator = new LoginValidator();
        loginValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "login";
        }

        Credentials credentials = credentialsDao.getCredentials(user.getUsername());

        if (credentials == null) {
            model.addAttribute("loginError", "Usuari o contrasenya incorrectes");
            return "login";
        }

        boolean passwordOk;
        try {
            passwordOk = PasswordUtils.check(user.getPassword(), credentials.getPassword());
        } catch (Exception e) {
            passwordOk = credentials.getPassword().equals(user.getPassword());
        }

        if (!passwordOk) {
            model.addAttribute("loginError", "Usuari o contrasenya incorrectes");
            return "login";
        }

        session.setAttribute("user", credentials);

        if (!credentials.getActivated()) {
            switch (credentials.getRole()) {
                case "user_ovi":
                    OviUser oviUser = oviUserDao.getOviUserByUsername(user.getUsername());
                    session.setAttribute("sessionUser", oviUser);
                    break;
                case "pap_pati":
                    PapPati papPati = papPatiDao.getPapPatiByUsername(user.getUsername());
                    session.setAttribute("sessionUser", papPati);
                    break;
            }
            return "redirect:/pending";
        }

        String nextUrl = (String) session.getAttribute("nextUrl");
        if (nextUrl != null) {
            session.removeAttribute("nextUrl");
            return "redirect:" + nextUrl;
        }

        switch (credentials.getRole()) {
            case "admin":
                return "redirect:/admin/portal";
            case "user_ovi":
                OviUser oviUser = oviUserDao.getOviUserByUsername(user.getUsername());
                session.setAttribute("sessionUser", oviUser);
                return "redirect:/oviUser/portal";
            case "pap_pati":
                PapPati papPati = papPatiDao.getPapPatiByUsername(user.getUsername());
                session.setAttribute("sessionUser", papPati);
                return "redirect:/papPati/portal";
            default:
                model.addAttribute("loginError", "Rol d'usuari no reconegut");
                return "login";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping("/pending")
    public String pending(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        Credentials credentials = (Credentials) session.getAttribute("user");
        model.addAttribute("username", credentials.getUsername());
        model.addAttribute("role", credentials.getRole());

        // Pasamos el status del usuario a la vista
        Object sessionUser = session.getAttribute("sessionUser");
        if (sessionUser instanceof OviUser) {
            model.addAttribute("status", ((OviUser) sessionUser).getStatus());
        } else if (sessionUser instanceof PapPati) {
            model.addAttribute("status", ((PapPati) sessionUser).getStatus());
        }

        return "pending";
    }

    @RequestMapping("/mi-portal")
    public String miPortal(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        Credentials credentials = (Credentials) session.getAttribute("user");

        switch (credentials.getRole()) {
            case "admin":
                return "redirect:/admin/portal";
            case "user_ovi":
                return "redirect:/oviUser/portal";
            case "pap_pati":
                return "redirect:/papPati/portal";
            default:
                return "redirect:/login";
        }
    }
}
