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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String activarPapPati(@PathVariable("username") String username,
                                 RedirectAttributes redirectAttributes) {
        credentialsDao.activateCredentials(username);
        papPatiDao.activatePapPati(username);

        redirectAttributes.addFlashAttribute("successMessage",
                "El PAP/PATI s'ha activat correctament");
        return "redirect:/admin/validarPapPati";
    }

    @Transactional
    @RequestMapping(value = "/validarPapPati/{username}/rebutjar", method = RequestMethod.POST)
    public String rebutjarPapPati(@PathVariable("username") String username,
                                  @RequestParam("rejectionReason") String rejectionReason,
                                  RedirectAttributes redirectAttributes) {
        credentialsDao.rejectCredentials(username, rejectionReason);
        papPatiDao.rejectPapPati(username);

        redirectAttributes.addFlashAttribute("successMessage",
                "El PAP/PATI s'ha rebutjat correctament");
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

    @Transactional
    @RequestMapping(value = "/gestionarPapPati/{username}/activar", method = RequestMethod.POST)
    public String reactivarPapPati(@PathVariable("username") String username,
                                   RedirectAttributes redirectAttributes) {
        credentialsDao.activateCredentials(username);
        papPatiDao.activatePapPati(username);
        credentialsDao.unrejectCredentials(username);

        redirectAttributes.addFlashAttribute("successMessage",
                "El PAP/PATI s'ha reactivat correctament");
        return "redirect:/admin/gestionarPapPati/" + username;
    }

    @Transactional
    @RequestMapping(value = "/gestionarPapPati/{username}/desactivar", method = RequestMethod.POST)
    public String desactivarPapPati(@PathVariable("username") String username,
                                    @RequestParam(value = "rejectionReason", required = false) String reason,
                                    RedirectAttributes redirectAttributes) {
        String motiu = (reason != null && !reason.trim().isEmpty())
                ? reason
                : "Desactivat pel tècnic OVI";
        credentialsDao.rejectCredentials(username, motiu);
        papPatiDao.rejectPapPati(username);

        redirectAttributes.addFlashAttribute("successMessage",
                "El PAP/PATI s'ha desactivat correctament");
        return "redirect:/admin/gestionarPapPati/" + username;
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
    public String activarOviUser(@PathVariable("username") String username,
                                 RedirectAttributes redirectAttributes) {
        credentialsDao.activateCredentials(username);
        oviUserDao.activateOviUser(username);

        redirectAttributes.addFlashAttribute("successMessage",
                "L'usuari OVI s'ha activat correctament");
        return "redirect:/admin/validarOviUsers";
    }

    @Transactional
    @RequestMapping(value = "/validarOviUsers/{username}/rebutjar", method = RequestMethod.POST)
    public String rebutjarOviUser(@PathVariable("username") String username,
                                  @RequestParam("rejectionReason") String rejectionReason,
                                  RedirectAttributes redirectAttributes) {
        credentialsDao.rejectCredentials(username, rejectionReason);
        oviUserDao.rejectOviUser(username);

        redirectAttributes.addFlashAttribute("successMessage",
                "L'usuari OVI s'ha rebutjat correctament");
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

    @Transactional
    @RequestMapping(value = "/gestionarOviUsers/{username}/activar", method = RequestMethod.POST)
    public String reactivarOviUser(@PathVariable("username") String username,
                                   RedirectAttributes redirectAttributes) {
        credentialsDao.activateCredentials(username);
        oviUserDao.activateOviUser(username);
        // També netegem el rejected i rejectionReason si l'usuari estava rebutjat
        credentialsDao.unrejectCredentials(username);

        redirectAttributes.addFlashAttribute("successMessage",
                "L'usuari s'ha reactivat correctament");
        return "redirect:/admin/gestionarOviUsers/" + username;
    }

    @Transactional
    @RequestMapping(value = "/gestionarOviUsers/{username}/desactivar", method = RequestMethod.POST)
    public String desactivarOviUser(@PathVariable("username") String username,
                                    @RequestParam(value = "rejectionReason", required = false) String reason,
                                    RedirectAttributes redirectAttributes) {
        String motiu = (reason != null && !reason.trim().isEmpty())
                ? reason
                : "Desactivat pel tècnic OVI";
        credentialsDao.rejectCredentials(username, motiu);
        oviUserDao.rejectOviUser(username);

        redirectAttributes.addFlashAttribute("successMessage",
                "L'usuari s'ha desactivat correctament");
        return "redirect:/admin/gestionarOviUsers/" + username;
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