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

    // Vistes
    private static final String LOGIN_VIEW = "login";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String REDIRECT_PENDING = "redirect:/pending";

    // Rols
    private static final String ROL_ADMIN = "admin";
    private static final String ROL_USER_OVI = "user_ovi";
    private static final String ROL_PAPPATI = "pap_pati";
    private static final String ROL_INSTRUCTOR = "instructor";

    // Atributs de sessió i de model
    private static final String USER_ATTR = "user";
    private static final String SESSION_USER_ATTR = "sessionUser";
    private static final String NEXT_URL_ATTR = "nextUrl";
    private static final String ERROR_LOGIN = "loginError";
    private static final String LOGIN_ERROR_MSG = "Usuari o contrasenya incorrectes";

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

    // Mostra el formulari d'inici de sessió
    @RequestMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute(USER_ATTR, new Credentials());
        return LOGIN_VIEW;
    }

    // Valida les credencials i redirigix l'usuari segons el seu rol
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String processLogin(@ModelAttribute("user") Credentials user,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model) {

        // Pas 1: validar el formulari (camps no buits)
        LoginValidator loginValidator = new LoginValidator();
        loginValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return LOGIN_VIEW;
        }

        // Pas 2: buscar les credencials a la BBDD
        Credentials credentials = credentialsDao.getCredentials(user.getUsername());
        if (credentials == null) {
            model.addAttribute(ERROR_LOGIN, LOGIN_ERROR_MSG);
            return LOGIN_VIEW;
        }

        // Pas 3: comprovar la contrasenya amb Jasypt
        if (!PasswordUtils.check(user.getPassword(), credentials.getPassword())) {
            model.addAttribute(ERROR_LOGIN, LOGIN_ERROR_MSG);
            return LOGIN_VIEW;
        }

        // Pas 4: guardar a la sessió una còpia segura sense la contrasenya
        session.setAttribute(USER_ATTR, sanitize(credentials));

        // Pas 5: si el compte no està activat, anar a /pending
        if (!credentials.getActivated()) {
            loadSessionUserIfNeeded(credentials.getRole(), user.getUsername(), session);
            return REDIRECT_PENDING;
        }

        // Pas 6: si l'usuari intentava anar a una pàgina concreta, redirigir-lo
        String nextUrl = (String) session.getAttribute(NEXT_URL_ATTR);
        if (nextUrl != null) {
            session.removeAttribute(NEXT_URL_ATTR);
            return "redirect:" + nextUrl;
        }

        // Pas 7: redirigir al portal corresponent al rol
        loadSessionUserIfNeeded(credentials.getRole(), user.getUsername(), session);
        return redirectToPortal(credentials.getRole(), model);
    }

    // Tanca la sessió actual
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // Mostra la pàgina d'estat de compte pendent o rebutjat
    @RequestMapping("/pending")
    public String pending(HttpSession session, Model model) {
        if (session.getAttribute(USER_ATTR) == null) {
            return REDIRECT_LOGIN;
        }

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        model.addAttribute("username", credentials.getUsername());
        model.addAttribute("role", credentials.getRole());
        model.addAttribute("rejectionReason", credentials.getRejectionReason());

        Object sessionUser = session.getAttribute(SESSION_USER_ATTR);
        if (sessionUser instanceof OviUser oviUser) {
            model.addAttribute("status", oviUser.getStatus());
        } else if (sessionUser instanceof PapPati papPati) {
            model.addAttribute("status", papPati.getStatus());
        }

        return "pending";
    }

    // Punt d'entrada únic que redirigix al portal segons el rol
    @RequestMapping("/mi-portal")
    public String miPortal(HttpSession session, Model model) {
        if (session.getAttribute(USER_ATTR) == null) {
            return REDIRECT_LOGIN;
        }
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        return redirectToPortal(credentials.getRole(), model);
    }

    // ---------- Helpers privats ----------

    // Crea una còpia de Credentials sense la contrasenya per guardar a sessió
    private Credentials sanitize(Credentials c) {
        Credentials safe = new Credentials();
        safe.setUsername(c.getUsername());
        safe.setRole(c.getRole());
        safe.setId(c.getId());
        safe.setActivated(c.getActivated());
        safe.setRejected(c.isRejected());
        safe.setRejectionReason(c.getRejectionReason());
        // No es copia la contrasenya intencionadament
        return safe;
    }

    // Carrega l'OviUser o PapPati a sessionUser quan el rol ho requereix
    private void loadSessionUserIfNeeded(String role, String username, HttpSession session) {
        switch (role) {
            case ROL_USER_OVI:
                session.setAttribute(SESSION_USER_ATTR, oviUserDao.getOviUserByUsername(username));
                break;
            case ROL_PAPPATI:
                session.setAttribute(SESSION_USER_ATTR, papPatiDao.getPapPatiByUsername(username));
                break;
            default:
                // admin i instructor no necessiten sessionUser
                break;
        }
    }

    // Decideix la URL del portal segons el rol
    private String redirectToPortal(String role, Model model) {
        switch (role) {
            case ROL_ADMIN:      return "redirect:/admin/portal";
            case ROL_USER_OVI:   return "redirect:/oviUser/portal";
            case ROL_PAPPATI:    return "redirect:/papPati/portal";
            case ROL_INSTRUCTOR: return "redirect:/instructor/portal";
            default:
                model.addAttribute(ERROR_LOGIN, "Rol d'usuari no reconegut");
                return LOGIN_VIEW;
        }
    }
}