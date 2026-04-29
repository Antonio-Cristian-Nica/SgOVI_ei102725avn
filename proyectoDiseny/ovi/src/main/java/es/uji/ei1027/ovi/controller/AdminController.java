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
import org.springframework.web.bind.annotation.RequestParam;

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

    // Mostra el portal d'administració
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

    // Mostra els PAP/PATI pendents de validació
    @RequestMapping("/validarPapPati")
    public String validarPapPati(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            session.setAttribute(NEXT_URL, "/admin/validarPapPati");
            return REDIRECT_LOGIN;
        }
        model.addAttribute("pappatis", papPatiDao.getPapPatisPendents());
        return "admin/validarPapPati";
    }

    // Activa un compte de PAP/PATI pendent
    @RequestMapping(value = "/validarPapPati/{username}/activar", method = RequestMethod.POST)
    public String activarPapPati(@PathVariable("username") String username,
                                 HttpSession session) {
        if (session.getAttribute("user") == null) return REDIRECT_LOGIN;
        credentialsDao.activateCredentials(username);
        papPatiDao.activatePapPati(username);
        return "redirect:/admin/validarPapPati";
    }

    // Rebutja un compte de PAP/PATI indicant el motiu
    @RequestMapping(value = "/validarPapPati/{username}/rebutjar", method = RequestMethod.POST)
    public String rebutjarPapPati(@PathVariable("username") String username,
                                  @RequestParam("rejectionReason") String rejectionReason,
                                  HttpSession session) {
        if (session.getAttribute("user") == null) return REDIRECT_LOGIN;
        credentialsDao.rejectCredentials(username, rejectionReason);
        papPatiDao.rejectPapPati(username);
        return "redirect:/admin/validarPapPati";
    }

    // =====================================================================
    // GESTIONAR PAP/PATI (acceptats i rebutjats)
    // =====================================================================

    // Mostra els PAP/PATI ja gestionats
    @RequestMapping("/gestionarPapPati")
    public String gestionarPapPati(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return REDIRECT_LOGIN;
        model.addAttribute("pappatis", papPatiDao.getPapPatisGestionats());
        return "admin/gestionarPapPati";
    }

    // Mostra el detall d'un PAP/PATI
    @RequestMapping("/gestionarPapPati/{username}")
    public String detallPapPati(@PathVariable("username") String username,
                                HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return REDIRECT_LOGIN;
        model.addAttribute("pappati", papPatiDao.getPapPatiByUsername(username));
        model.addAttribute("credentials", credentialsDao.getCredentials(username));
        return "admin/detallPapPati";
    }

    // =====================================================================
    // VALIDAR OVI USERS
    // =====================================================================

    // Mostra els usuaris OVI pendents de validació
    @RequestMapping("/validarOviUsers")
    public String validarOviUsers(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            session.setAttribute(NEXT_URL, "/admin/validarOviUsers");
            return REDIRECT_LOGIN;
        }
        model.addAttribute("oviusers", oviUserDao.getOviUsersPendents());
        return "admin/validarOviUsers";
    }

    // Activa un compte d'usuari OVI pendent
    @RequestMapping(value = "/validarOviUsers/{username}/activar", method = RequestMethod.POST)
    public String activarOviUser(@PathVariable("username") String username,
                                 HttpSession session) {
        if (session.getAttribute("user") == null) return REDIRECT_LOGIN;
        credentialsDao.activateCredentials(username);
        oviUserDao.activateOviUser(username);
        return "redirect:/admin/validarOviUsers";
    }

    // Rebutja un compte d'usuari OVI indicant el motiu
    @RequestMapping(value = "/validarOviUsers/{username}/rebutjar", method = RequestMethod.POST)
    public String rebutjarOviUser(@PathVariable("username") String username,
                                  @RequestParam("rejectionReason") String rejectionReason,
                                  HttpSession session) {
        if (session.getAttribute("user") == null) return REDIRECT_LOGIN;
        credentialsDao.rejectCredentials(username, rejectionReason);
        oviUserDao.rejectOviUser(username);
        return "redirect:/admin/validarOviUsers";
    }

    // =====================================================================
    // GESTIONAR OVI USERS (acceptats i rebutjats)
    // =====================================================================

    // Mostra els usuaris OVI ja gestionats
    @RequestMapping("/gestionarOviUsers")
    public String gestionarOviUsers(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return REDIRECT_LOGIN;
        model.addAttribute("oviusers", oviUserDao.getOviUsersGestionats());
        return "admin/gestionarOviUsers";
    }

    // Mostra el detall d'un usuari OVI
    @RequestMapping("/gestionarOviUsers/{username}")
    public String detallOviUser(@PathVariable("username") String username,
                                HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return REDIRECT_LOGIN;
        model.addAttribute("oviuser", oviUserDao.getOviUserByUsername(username));
        model.addAttribute("credentials", credentialsDao.getCredentials(username));
        return "admin/detallOviUser";
    }

    // Mostra les estadístiques bàsiques d'usuaris OVI i PAP/PATI
    @RequestMapping("/estadistiques")
    public String estadistiques(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return REDIRECT_LOGIN;

        // OVI Users
        model.addAttribute("oviActius", oviUserDao.countByStatus("active"));
        model.addAttribute("oviPendents", oviUserDao.countByStatus("approvalPending"));
        model.addAttribute("oviRebutjats", oviUserDao.countByStatus("inactive"));

        // PAP/PATIs
        model.addAttribute("papActius", papPatiDao.countByStatus("active"));
        model.addAttribute("papPendents", papPatiDao.countByStatus("approvalPending"));
        model.addAttribute("papRebutjats", papPatiDao.countByStatus("inactive"));

        return "admin/estadistiques";
    }
}