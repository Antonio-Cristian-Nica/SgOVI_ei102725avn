package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.assistancerequest.AssistanceRequestDao;
import es.uji.ei1027.ovi.dao.contract.ContractDao;
import es.uji.ei1027.ovi.dao.credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.model.OviUser;
import es.uji.ei1027.ovi.model.PapPati;
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
    private AssistanceRequestDao assistanceRequestDao;
    private ContractDao contractDao;

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

    @Autowired
    public void setAssistanceRequestDao(AssistanceRequestDao assistanceRequestDao) {
        this.assistanceRequestDao = assistanceRequestDao;
    }

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
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

    @RequestMapping(value = "/validarPapPati/{username}/activar/confirm", method = RequestMethod.GET)
    public String confirmActivarPapPati(@PathVariable("username") String username,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        PapPati pap = papPatiDao.getPapPatiByUsername(username);
        if (pap == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest PAP/PATI no existeix");
            return "redirect:/admin/validarPapPati";
        }

        model.addAttribute("titol", "Confirmar activació del compte de PAP/PATI");
        model.addAttribute("missatge",
                "Estàs a punt d'activar el compte de " + pap.getNameAndSurname() + ".");
        model.addAttribute("detall",
                "El candidat podrà iniciar sessió i començar a rebre sol·licituds.");
        model.addAttribute("actionUrl", "/admin/validarPapPati/" + username + "/activar");
        model.addAttribute("cancelUrl", "/admin/gestionarPapPati/" + username + "?from=/admin/validarPapPati");
        model.addAttribute("confirmLabel", "Sí, activar compte");
        model.addAttribute("tipusAccio", "normal");
        return "fragments/confirm";
    }

    @RequestMapping(value = "/validarPapPati/{username}/rebutjar/confirm", method = RequestMethod.GET)
    public String confirmRebutjarPapPati(@PathVariable("username") String username,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        PapPati pap = papPatiDao.getPapPatiByUsername(username);
        if (pap == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest PAP/PATI no existeix");
            return "redirect:/admin/validarPapPati";
        }

        model.addAttribute("titol", "Confirmar rebuig del compte de PAP/PATI");
        model.addAttribute("missatge",
                "Estàs a punt de rebutjar el compte de " + pap.getNameAndSurname() + ".");
        model.addAttribute("detall",
                "El candidat veurà el motiu en intentar iniciar sessió. Aquesta acció no es pot desfer.");
        model.addAttribute("actionUrl", "/admin/validarPapPati/" + username + "/rebutjar");
        model.addAttribute("cancelUrl", "/admin/gestionarPapPati/" + username + "?from=/admin/validarPapPati");
        model.addAttribute("confirmLabel", "Sí, rebutjar compte");
        model.addAttribute("tipusAccio", "perillosa");
        model.addAttribute("textareaName", "rejectionReason");
        model.addAttribute("textareaLabel", "Motiu del rebuig");
        model.addAttribute("textareaPlaceholder", "Explica per què rebutges aquest candidat...");
        model.addAttribute("textareaRequired", true);
        return "fragments/confirm";
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
    public String detallPapPati(@PathVariable("username") String username,
                                @RequestParam(value = "from", required = false) String from,
                                Model model) {
        model.addAttribute("pappati", papPatiDao.getPapPatiByUsername(username));
        model.addAttribute("credentials", credentialsDao.getCredentials(username));
        model.addAttribute("from", from);
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

    @RequestMapping(value = "/gestionarPapPati/{username}/desactivar/confirm", method = RequestMethod.GET)
    public String confirmDesactivarPapPati(@PathVariable("username") String username,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        PapPati pap = papPatiDao.getPapPatiByUsername(username);
        if (pap == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest PAP/PATI no existeix");
            return "redirect:/admin/gestionarPapPati";
        }

        model.addAttribute("titol", "Confirmar desactivació del compte de PAP/PATI");
        model.addAttribute("missatge",
                "Estàs a punt de desactivar el compte de " + pap.getNameAndSurname() + ".");
        model.addAttribute("detall",
                "El PAP/PATI ja no podrà iniciar sessió ni rebre noves sol·licituds fins que el compte siga reactivat.");
        model.addAttribute("actionUrl", "/admin/gestionarPapPati/" + username + "/desactivar");
        model.addAttribute("cancelUrl", "/admin/gestionarPapPati/" + username);
        model.addAttribute("confirmLabel", "Sí, desactivar compte");
        model.addAttribute("tipusAccio", "perillosa");
        model.addAttribute("textareaName", "rejectionReason");
        model.addAttribute("textareaLabel", "Motiu de la desactivació (opcional)");
        model.addAttribute("textareaPlaceholder", "Indica el motiu de la desactivació...");
        model.addAttribute("textareaRequired", false);
        return "fragments/confirm";
    }

    @RequestMapping(value = "/gestionarPapPati/{username}/activar/confirm", method = RequestMethod.GET)
    public String confirmReactivarPapPati(@PathVariable("username") String username,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        PapPati pap = papPatiDao.getPapPatiByUsername(username);
        if (pap == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest PAP/PATI no existeix");
            return "redirect:/admin/gestionarPapPati";
        }

        model.addAttribute("titol", "Confirmar reactivació del compte de PAP/PATI");
        model.addAttribute("missatge",
                "Estàs a punt de reactivar el compte de " + pap.getNameAndSurname() + ".");
        model.addAttribute("detall",
                "El PAP/PATI podrà tornar a iniciar sessió i rebre sol·licituds.");
        model.addAttribute("actionUrl", "/admin/gestionarPapPati/" + username + "/activar");
        model.addAttribute("cancelUrl", "/admin/gestionarPapPati/" + username);
        model.addAttribute("confirmLabel", "Sí, reactivar compte");
        model.addAttribute("tipusAccio", "normal");
        return "fragments/confirm";
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

    @RequestMapping(value = "/validarOviUsers/{username}/activar/confirm", method = RequestMethod.GET)
    public String confirmActivarOviUser(@PathVariable("username") String username,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        OviUser user = oviUserDao.getOviUserByUsername(username);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest usuari no existeix");
            return "redirect:/admin/validarOviUsers";
        }

        model.addAttribute("titol", "Confirmar activació del compte d'usuari OVI");
        model.addAttribute("missatge",
                "Estàs a punt d'activar el compte de " + user.getNameAndSurname() + ".");
        model.addAttribute("detall",
                "L'usuari podrà iniciar sessió i accedir al portal complet.");
        model.addAttribute("actionUrl", "/admin/validarOviUsers/" + username + "/activar");
        model.addAttribute("cancelUrl", "/admin/validarOviUsers");
        model.addAttribute("confirmLabel", "Sí, activar compte");
        model.addAttribute("tipusAccio", "normal");
        return "fragments/confirm";
    }

    @RequestMapping(value = "/validarOviUsers/{username}/rebutjar/confirm", method = RequestMethod.GET)
    public String confirmRebutjarOviUser(@PathVariable("username") String username,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        OviUser user = oviUserDao.getOviUserByUsername(username);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest usuari no existeix");
            return "redirect:/admin/validarOviUsers";
        }

        model.addAttribute("titol", "Confirmar rebuig del compte d'usuari OVI");
        model.addAttribute("missatge",
                "Estàs a punt de rebutjar el compte de " + user.getNameAndSurname() + ".");
        model.addAttribute("detall",
                "L'usuari veurà el motiu en intentar iniciar sessió. Aquesta acció no es pot desfer.");
        model.addAttribute("actionUrl", "/admin/validarOviUsers/" + username + "/rebutjar");
        model.addAttribute("cancelUrl", "/admin/validarOviUsers");
        model.addAttribute("confirmLabel", "Sí, rebutjar compte");
        model.addAttribute("tipusAccio", "perillosa");
        model.addAttribute("textareaName", "rejectionReason");
        model.addAttribute("textareaLabel", "Motiu del rebuig");
        model.addAttribute("textareaPlaceholder", "Explica per què rebutges aquest compte...");
        model.addAttribute("textareaRequired", true);
        return "fragments/confirm";
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

    @RequestMapping(value = "/gestionarOviUsers/{username}/desactivar/confirm", method = RequestMethod.GET)
    public String confirmDesactivarOviUser(@PathVariable("username") String username,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        OviUser user = oviUserDao.getOviUserByUsername(username);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest usuari no existeix");
            return "redirect:/admin/gestionarOviUsers";
        }

        model.addAttribute("titol", "Confirmar desactivació del compte d'usuari OVI");
        model.addAttribute("missatge",
                "Estàs a punt de desactivar el compte de " + user.getNameAndSurname() + ".");
        model.addAttribute("detall",
                "L'usuari ja no podrà iniciar sessió fins que el compte siga reactivat.");
        model.addAttribute("actionUrl", "/admin/gestionarOviUsers/" + username + "/desactivar");
        model.addAttribute("cancelUrl", "/admin/gestionarOviUsers/" + username);
        model.addAttribute("confirmLabel", "Sí, desactivar compte");
        model.addAttribute("tipusAccio", "perillosa");
        model.addAttribute("textareaName", "rejectionReason");
        model.addAttribute("textareaLabel", "Motiu de la desactivació (opcional)");
        model.addAttribute("textareaPlaceholder", "Indica el motiu de la desactivació...");
        model.addAttribute("textareaRequired", false);
        return "fragments/confirm";
    }

    @RequestMapping(value = "/gestionarOviUsers/{username}/activar/confirm", method = RequestMethod.GET)
    public String confirmReactivarOviUser(@PathVariable("username") String username,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        OviUser user = oviUserDao.getOviUserByUsername(username);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest usuari no existeix");
            return "redirect:/admin/gestionarOviUsers";
        }

        model.addAttribute("titol", "Confirmar reactivació del compte d'usuari OVI");
        model.addAttribute("missatge",
                "Estàs a punt de reactivar el compte de " + user.getNameAndSurname() + ".");
        model.addAttribute("detall",
                "L'usuari podrà tornar a iniciar sessió i accedir al portal.");
        model.addAttribute("actionUrl", "/admin/gestionarOviUsers/" + username + "/activar");
        model.addAttribute("cancelUrl", "/admin/gestionarOviUsers/" + username);
        model.addAttribute("confirmLabel", "Sí, reactivar compte");
        model.addAttribute("tipusAccio", "normal");
        return "fragments/confirm";
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

        // Sol·licituds
        model.addAttribute("solInRevisio", assistanceRequestDao.countByStatus("inProgress"));
        model.addAttribute("solAcceptades", assistanceRequestDao.countByStatus("accepted"));
        model.addAttribute("solRebutjades", assistanceRequestDao.countByStatus("rejected"));
        model.addAttribute("solAmbContracte", assistanceRequestDao.countByStatus("closedWithContract"));
        model.addAttribute("solFinalitzades", assistanceRequestDao.countByStatus("closedContractEnded"));

        // Contractes
        model.addAttribute("contractesActius", contractDao.countByStatus("active"));
        model.addAttribute("contractesFinalitzats", contractDao.countByStatus("ended"));
        model.addAttribute("contractesCancellats", contractDao.countByStatus("cancelled"));

        return "admin/estadistiques";
    }
}