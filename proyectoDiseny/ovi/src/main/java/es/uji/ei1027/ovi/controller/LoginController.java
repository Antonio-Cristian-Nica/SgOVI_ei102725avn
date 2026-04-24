package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
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

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    private static final String LOGIN_VIEW = "login";
    private static final String ROL_USER_OVI = "user_ovi";
    private static final String ROL_PAPPATI = "pap_pati";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String ERROR_LOGIN = "loginError";
    private static final String USER_SESSION = "sessionUser";
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
        return LOGIN_VIEW;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String processLogin(@ModelAttribute("user") Credentials user,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model) {

        LoginValidator loginValidator = new LoginValidator();
        loginValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return LOGIN_VIEW;
        }

        Credentials credentials = credentialsDao.getCredentials(user.getUsername());

        if (credentials == null) {
            model.addAttribute(ERROR_LOGIN, "Usuari o contrasenya incorrectes");
            return LOGIN_VIEW;
        }

        boolean passwordOk;
        try {
            passwordOk = PasswordUtils.check(user.getPassword(), credentials.getPassword());
        } catch (Exception e) {
            passwordOk = credentials.getPassword().equals(user.getPassword());
        }

        if (!passwordOk) {
            model.addAttribute(ERROR_LOGIN, "Usuari o contrasenya incorrectes");
            return LOGIN_VIEW;
        }

        session.setAttribute("user", credentials);

        if (!credentials.getActivated()) {
            switch (credentials.getRole()) {
                case ROL_USER_OVI:
                    OviUser oviUser = oviUserDao.getOviUserByUsername(user.getUsername());
                    session.setAttribute(USER_SESSION, oviUser);
                    break;
                case ROL_PAPPATI:
                    PapPati papPati = papPatiDao.getPapPatiByUsername(user.getUsername());
                    session.setAttribute(USER_SESSION, papPati);
                    break;
                default:
                    // Caso para roles desconocidos o no gestionados
                    return REDIRECT_LOGIN;
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
            case ROL_USER_OVI:
                OviUser oviUser = oviUserDao.getOviUserByUsername(user.getUsername());
                session.setAttribute(USER_SESSION, oviUser);
                return "redirect:/oviUser/portal";
            case ROL_PAPPATI:
                PapPati papPati = papPatiDao.getPapPatiByUsername(user.getUsername());
                session.setAttribute(USER_SESSION, papPati);
                return "redirect:/papPati/portal";
            default:
                model.addAttribute(ERROR_LOGIN, "Rol d'usuari no reconegut");
                return LOGIN_VIEW;
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
            return REDIRECT_LOGIN;
        }

        Credentials credentials = (Credentials) session.getAttribute("user");
        model.addAttribute("username", credentials.getUsername());
        model.addAttribute("role", credentials.getRole());

        // Pasamos el status del usuario a la vista
        Object sessionUser = session.getAttribute(USER_SESSION);
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
            return REDIRECT_LOGIN;
        }

        Credentials credentials = (Credentials) session.getAttribute("user");

        switch (credentials.getRole()) {
            case "admin":
                return "redirect:/admin/portal";
            case ROL_USER_OVI:
                return "redirect:/oviUser/portal";
            case ROL_PAPPATI:
                return "redirect:/papPati/portal";
            default:
                return REDIRECT_LOGIN;
        }
    }
}
