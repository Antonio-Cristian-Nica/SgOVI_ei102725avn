package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

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
    public String portal() {
        return "admin/portal";
    }

    // =====================================================================
    // VALIDAR PAP/PATI
    // =====================================================================

    @RequestMapping("/validarPapPati")
    public String validarPapPati(Model model) {
        model.addAttribute("pappatis", papPatiDao.getPapPatisPendents());
        return "admin/validarPapPati";
    }

    @Transactional
    @RequestMapping(value = "/validarPapPati/{username}/activar", method = RequestMethod.POST)
    public String activarPapPati(@PathVariable("username") String username) {
        credentialsDao.activateCredentials(username);
        papPatiDao.activatePapPati(username);
        return "redirect:/admin/validarPapPati";
    }

    @Transactional
    @RequestMapping(value = "/validarPapPati/{username}/rebutjar", method = RequestMethod.POST)
    public String rebutjarPapPati(@PathVariable("username") String username,
                                  @RequestParam("rejectionReason") String rejectionReason) {
        credentialsDao.rejectCredentials(username, rejectionReason);
        papPatiDao.rejectPapPati(username);
        return "redirect:/admin/validarPapPati";
    }

    // =====================================================================
    // GESTIONAR PAP/PATI (acceptats i rebutjats)
    // =====================================================================

    @RequestMapping("/gestionarPapPati")
    public String gestionarPapPati(Model model) {
        model.addAttribute("pappatis", papPatiDao.getPapPatisGestionats());
        return "admin/gestionarPapPati";
    }

    @RequestMapping("/gestionarPapPati/{username}")
    public String detallPapPati(@PathVariable("username") String username, Model model) {
        model.addAttribute("pappati", papPatiDao.getPapPatiByUsername(username));
        model.addAttribute("credentials", credentialsDao.getCredentials(username));
        return "admin/detallPapPati";
    }

    // =====================================================================
    // VALIDAR OVI USERS
    // =====================================================================

    @RequestMapping("/validarOviUsers")
    public String validarOviUsers(Model model) {
        model.addAttribute("oviusers", oviUserDao.getOviUsersPendents());
        return "admin/validarOviUsers";
    }

    @Transactional
    @RequestMapping(value = "/validarOviUsers/{username}/activar", method = RequestMethod.POST)
    public String activarOviUser(@PathVariable("username") String username) {
        credentialsDao.activateCredentials(username);
        oviUserDao.activateOviUser(username);
        return "redirect:/admin/validarOviUsers";
    }

    @Transactional
    @RequestMapping(value = "/validarOviUsers/{username}/rebutjar", method = RequestMethod.POST)
    public String rebutjarOviUser(@PathVariable("username") String username,
                                  @RequestParam("rejectionReason") String rejectionReason) {
        credentialsDao.rejectCredentials(username, rejectionReason);
        oviUserDao.rejectOviUser(username);
        return "redirect:/admin/validarOviUsers";
    }

    // =====================================================================
    // GESTIONAR OVI USERS (acceptats i rebutjats)
    // =====================================================================

    @RequestMapping("/gestionarOviUsers")
    public String gestionarOviUsers(Model model) {
        model.addAttribute("oviusers", oviUserDao.getOviUsersGestionats());
        return "admin/gestionarOviUsers";
    }

    @RequestMapping("/gestionarOviUsers/{username}")
    public String detallOviUser(@PathVariable("username") String username, Model model) {
        model.addAttribute("oviuser", oviUserDao.getOviUserByUsername(username));
        model.addAttribute("credentials", credentialsDao.getCredentials(username));
        return "admin/detallOviUser";
    }

    // =====================================================================
    // ESTADÍSTIQUES
    // =====================================================================

    @RequestMapping("/estadistiques")
    public String estadistiques(Model model) {
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