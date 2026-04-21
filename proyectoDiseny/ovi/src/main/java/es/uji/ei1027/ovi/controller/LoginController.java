package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.Credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.OviUser.OviUserDao;
import es.uji.ei1027.ovi.dao.PapPati.PapPatiDao;
import es.uji.ei1027.ovi.model.Credentials;
import es.uji.ei1027.ovi.model.OviUser;
import es.uji.ei1027.ovi.model.PapPati;
import es.uji.ei1027.ovi.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String showLogin() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {

        Credentials credentials = credentialsDao.getCredentials(username);

        // 1. Usuario no existe o contraseña incorrecta
        if (credentials == null) {
            model.addAttribute("loginError", "Usuari o contrasenya incorrectes");
            return "login";
        }

        // Comprobamos contraseña
        boolean passwordOk;
        try {
            passwordOk = PasswordUtils.check(password, credentials.getPassword());
        } catch (Exception e) {
            // Si falla la desencriptación, la contraseña está en texto plano (usuario antiguo)
            passwordOk = credentials.getPassword().equals(password);
        }

        if (!passwordOk) {
            model.addAttribute("loginError", "Usuari o contrasenya incorrectes");
            return "login";
        }

        // 2. Guardamos siempre las credenciales en sesión
        session.setAttribute("user", credentials);

        // 3. Cuenta no activada
        if (!credentials.getActivated()) {
            // Recuperamos el objeto completo para tener acceso al status
            switch (credentials.getRole()) {
                case "user_ovi":
                    OviUser oviUser = oviUserDao.getOviUserByUsername(username);
                    session.setAttribute("sessionUser", oviUser);
                    break;
                case "pap_pati":
                    PapPati papPati = papPatiDao.getPapPatiByUsername(username);
                    session.setAttribute("sessionUser", papPati);
                    break;
            }
            return "redirect:/pending";
        }

        // 4. Cuenta activada, recuperamos objeto completo y redirigimos según rol
        switch (credentials.getRole()) {
            case "admin":
                return "redirect:/admin/portal";
            case "user_ovi":
                OviUser oviUser = oviUserDao.getOviUserByUsername(username);
                session.setAttribute("sessionUser", oviUser);
                return "redirect:/oviUser/portal";
            case "pap_pati":
                PapPati papPati = papPatiDao.getPapPatiByUsername(username);
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
