package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.assistancerequest.AssistanceRequestDao;
import es.uji.ei1027.ovi.dao.contract.ContractDao;
import es.uji.ei1027.ovi.dao.credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.negotiation.NegotiationDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.oviusertutor.TutorDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.dao.requestschedule.RequestScheduleDao;
import es.uji.ei1027.ovi.model.*;
import es.uji.ei1027.ovi.utils.PasswordUtils;
import es.uji.ei1027.ovi.validator.ChangePasswordValidator;
import es.uji.ei1027.ovi.validator.OviUserValidator;
import es.uji.ei1027.ovi.validator.TutorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/oviUser")
public class OviUserController {

    private static final String CANVI_CONTRASENYA_VIEW = "oviuser/canviarContrasenya";
    private static final String USER_ATTR = "user";

    private OviUserDao oviUserDao;
    private CredentialsDao credentialsDao;
    private TutorDao tutorDao;
    private ContractDao contractDao;
    private NegotiationDao negotiationDao;
    private PapPatiDao papPatiDao;
    private AssistanceRequestDao assistanceRequestDao;
    private RequestScheduleDao requestScheduleDao;

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setCredentialsDao(CredentialsDao credentialsDao) {
        this.credentialsDao = credentialsDao;
    }

    @Autowired
    public void setTutorDao(TutorDao tutorDao) {
        this.tutorDao = tutorDao;
    }

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao) {
        this.negotiationDao = negotiationDao;
    }

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @Autowired
    public void setAssistanceRequestDao(AssistanceRequestDao assistanceRequestDao) {
        this.assistanceRequestDao = assistanceRequestDao;
    }

    @Autowired
    public void setRequestScheduleDao(RequestScheduleDao requestScheduleDao) {
        this.requestScheduleDao = requestScheduleDao;
    }

    // =====================================================================
    // REGISTRE
    // =====================================================================

    // Mostra el formulari de registre d'usuari OVI
    @RequestMapping("/register")
    public String addOviUser(Model model) {
        model.addAttribute("oviuser", new OviUserRegistration());
        return "oviuser/register";
    }

    // Processa el registre d'un nou usuari OVI
    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("oviuser") OviUserRegistration registration,
                                   BindingResult bindingResult) {

        OviUserValidator validator = new OviUserValidator();
        validator.validate(registration, bindingResult);

        if (registration.getUsername() != null
                && credentialsDao.getCredentials(registration.getUsername()) != null) {
            bindingResult.rejectValue("username", "duplicat",
                    "Aquest nom d'usuari ja està agafat");
        }

        if (registration.getEmailAddress() != null
                && oviUserDao.existsEmail(registration.getEmailAddress())) {
            bindingResult.rejectValue("emailAddress", "duplicat",
                    "Aquest correu electrònic ja està registrat");
        }

        if (bindingResult.hasErrors()) {
            return "oviuser/register";
        }

        registration.setStatus("approvalPending");

        Credentials credentials = new Credentials();
        credentials.setUsername(registration.getUsername());
        credentials.setPassword(PasswordUtils.encrypt(registration.getPassword()));
        credentials.setRole("user_ovi");
        credentials.setActivated(false);
        credentials.setId(0);
        credentialsDao.addCredentials(credentials);

        oviUserDao.addOviUser(registration);

        int oviID = oviUserDao.getLastInsertedId();
        credentialsDao.updateId(registration.getUsername(), oviID);

        return "redirect:registerSuccess";
    }

    // Mostra la pàgina de registre completat
    @RequestMapping("/registerSuccess")
    public String registerSuccess() {
        return "registerSuccess";
    }

    // =====================================================================
    // PORTAL I DADES PERSONALS
    // =====================================================================

    // Mostra el portal de l'usuari OVI
    @RequestMapping("/portal")
    public String portal() {
        return "oviuser/portal";
    }

    // Mostra el formulari d'edició de l'usuari OVI
    @RequestMapping("/edit")
    public String editOviUser(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        model.addAttribute("oviuser", oviUser);
        return "oviuser/edit";
    }

    // Processa l'edició de les dades de l'usuari OVI
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String processEditSubmit(@ModelAttribute("oviuser") OviUser oviUser,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        OviUserValidator validator = new OviUserValidator();
        validator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviuser/edit";
        }

        // Forçar que els camps d'identitat vinguen de BBDD, no del formulari, millora de seguretat
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser dbUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        oviUser.setOviID(dbUser.getOviID());
        oviUser.setUsername(dbUser.getUsername());
        oviUser.setTutorID(dbUser.getTutorID());
        oviUser.setStatus(dbUser.getStatus());

        oviUserDao.updateOviUser(oviUser);

        redirectAttributes.addFlashAttribute("successMessage",
                "Les teues dades s'han guardat correctament");
        return "redirect:portal";
    }

    // =====================================================================
    // CANVI DE CONTRASENYA
    // =====================================================================

    // Mostra el formulari per a canviar la contrasenya
    @RequestMapping("/canviarContrasenya")
    public String canviarContrasenya(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return CANVI_CONTRASENYA_VIEW;
    }

    // Processa el canvi de contrasenya
    @RequestMapping(value = "/canviarContrasenya", method = RequestMethod.POST)
    public String processCanviarContrasenya(@ModelAttribute("changePasswordForm") ChangePasswordForm form,
                                            BindingResult bindingResult,
                                            HttpSession session,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {

        ChangePasswordValidator validator = new ChangePasswordValidator();
        validator.validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return CANVI_CONTRASENYA_VIEW;
        }

        // Llegim la contrasenya actual de BBDD (no de la sessió, que està sanititzada)
        Credentials sessionCredentials = (Credentials) session.getAttribute(USER_ATTR);
        Credentials dbCredentials = credentialsDao.getCredentials(sessionCredentials.getUsername());

        if (!PasswordUtils.check(form.getCurrentPassword(), dbCredentials.getPassword())) {
            model.addAttribute("errorActual", "La contrasenya actual no és correcta");
            return CANVI_CONTRASENYA_VIEW;
        }

        String newEncryptedPassword = PasswordUtils.encrypt(form.getNewPassword());
        credentialsDao.updatePassword(sessionCredentials.getUsername(), newEncryptedPassword);
        redirectAttributes.addFlashAttribute("successMessage",
                "La teua contrasenya s'ha canviat correctament");
        return "redirect:portal";
    }

    // =====================================================================
    // TUTOR
    // =====================================================================

    // Mostra la informació del tutor associat
    @RequestMapping("/tutor")
    public String tutor(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());

        if (oviUser.getTutorID() != null) {
            model.addAttribute("tutor", tutorDao.getTutor(oviUser.getTutorID()));
        }
        model.addAttribute("teTutor", oviUser.getTutorID() != null);
        return "oviuser/tutor";
    }

    // Mostra el formulari per a afegir un tutor
    @RequestMapping("/tutor/add")
    public String addTutor(Model model) {
        model.addAttribute("tutor", new Tutor());
        return "oviuser/tutorForm";
    }

    // Processa l'alta d'un nou tutor
    @Transactional
    @RequestMapping(value = "/tutor/add", method = RequestMethod.POST)
    public String processAddTutor(@ModelAttribute("tutor") Tutor tutor,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        TutorValidator validator = new TutorValidator();
        validator.validate(tutor, bindingResult);

        // Comprovació de duplicats abans d'inserir
        if (tutor.getEmailAddress() != null
                && tutorDao.existsEmail(tutor.getEmailAddress())) {
            bindingResult.rejectValue("emailAddress", "duplicat",
                    "Aquest correu electrònic ja està registrat com a tutor");
        }

        if (bindingResult.hasErrors()) {
            return "oviuser/tutorForm";
        }

        tutorDao.addTutor(tutor);
        int tutorID = tutorDao.getLastInsertedId();
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        oviUserDao.updateTutorID(credentials.getUsername(), tutorID);

        redirectAttributes.addFlashAttribute("successMessage",
                "El tutor s'ha afegit correctament");
        return "redirect:/oviUser/tutor";
    }

    // Mostra el formulari per a editar el tutor
    @RequestMapping("/tutor/edit")
    public String editTutor(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());

        if (oviUser.getTutorID() == null) {
            return "redirect:/oviUser/tutor";
        }

        model.addAttribute("tutor", tutorDao.getTutor(oviUser.getTutorID()));
        return "oviuser/tutorForm";
    }

    // Processa l'edició del tutor
    @RequestMapping(value = "/tutor/edit", method = RequestMethod.POST)
    public String processEditTutor(@ModelAttribute("tutor") Tutor tutor,
                                   BindingResult bindingResult,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        TutorValidator validator = new TutorValidator();
        validator.validate(tutor, bindingResult);

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());

        if (oviUser.getTutorID() == null) {
            return "redirect:/oviUser/tutor";
        }

        // Comprovació de correu duplicat (excloent el tutor actual)
        if (tutor.getEmailAddress() != null
                && tutorDao.existsEmailExcluding(tutor.getEmailAddress(), oviUser.getTutorID())) {
            bindingResult.rejectValue("emailAddress", "duplicat",
                    "Aquest correu electrònic ja està registrat com a tutor");
        }

        if (bindingResult.hasErrors()) {
            return "oviuser/tutorForm";
        }

        tutor.setTutorID(oviUser.getTutorID());
        tutorDao.updateTutor(tutor);

        redirectAttributes.addFlashAttribute("successMessage",
                "Les dades del tutor s'han actualitzat correctament");
        return "redirect:/oviUser/tutor";
    }

    // Mostra la pàgina de confirmació d'eliminació del tutor
    @RequestMapping("/tutor/delete/confirm")
    public String confirmDeleteTutor(HttpSession session, Model model,
                                     RedirectAttributes redirectAttributes) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());

        if (oviUser.getTutorID() == null) {
            return "redirect:/oviUser/tutor";
        }

        Tutor tutor = tutorDao.getTutor(oviUser.getTutorID());

        model.addAttribute("titol", "Eliminar tutor legal");
        model.addAttribute("missatge",
                "Estàs segur/a que vols eliminar el tutor legal " + tutor.getNameAndSurname() + "?");
        model.addAttribute("detall", "Aquesta acció no es pot desfer.");
        model.addAttribute("actionUrl", "/oviUser/tutor/delete");
        model.addAttribute("cancelUrl", "/oviUser/tutor");
        model.addAttribute("confirmLabel", "Eliminar tutor");
        model.addAttribute("tipusAccio", "perillosa");

        return "fragments/confirm";
    }

    // Elimina el tutor associat a l'usuari
    @Transactional
    @RequestMapping(value = "/tutor/delete", method = RequestMethod.POST)
    public String deleteTutor(HttpSession session, RedirectAttributes redirectAttributes) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());

        if (oviUser.getTutorID() == null) {
            return "redirect:/oviUser/tutor";
        }

        Integer tutorID = oviUser.getTutorID();
        oviUserDao.removeTutorID(credentials.getUsername());
        tutorDao.deleteTutor(tutorID);

        redirectAttributes.addFlashAttribute("successMessage",
                "El tutor s'ha eliminat correctament");
        return "redirect:/oviUser/tutor";
    }

    // =====================================================================
    // CONTRACTES
    // =====================================================================

    // Mostra els contractes de l'usuari OVI
    @RequestMapping("/contractes")
    public String contractes(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        List<Contract> contractes = contractDao.getContractsByOviUser(oviUser.getOviID());

        Map<Integer, Negotiation> negotiations = new HashMap<>();
        Map<Integer, PapPati> papPatis = new HashMap<>();
        for (Contract c : contractes) {
            Negotiation neg = negotiationDao.getNegotiation(c.getNegotiationID());
            negotiations.put(c.getContractID(), neg);
            papPatis.put(c.getContractID(), papPatiDao.getPapPati(neg.getPapID()));
        }

        model.addAttribute("contractes", contractes);
        model.addAttribute("negotiations", negotiations);
        model.addAttribute("papPatis", papPatis);
        return "oviuser/contractes";
    }

    // Mostra el detall d'un contracte concret de l'usuari OVI
    @RequestMapping("/contractes/{contractID}")
    public String contracteDetail(@PathVariable int contractID,
                                  @RequestParam(value = "from", required = false) String from,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());

        Contract contract = contractDao.getContract(contractID);
        if (contract == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest contracte no existeix");
            return "redirect:/oviUser/contractes";
        }

        // Verificar que el contracte pertany a aquest usuari OVI
        Negotiation neg = negotiationDao.getNegotiation(contract.getNegotiationID());
        AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        if (sol.getOviID() != oviUser.getOviID()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No tens permís per a veure aquest contracte");
            return "redirect:/oviUser/contractes";
        }

        PapPati papPati = papPatiDao.getPapPati(neg.getPapID());
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(neg.getRequestID());

        model.addAttribute("contract", contract);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("neg", neg);
        model.addAttribute("sol", sol);
        model.addAttribute("papPati", papPati);
        model.addAttribute("horaris", horaris);
        model.addAttribute("from", from);

        return "oviuser/contracteDetail";
    }

}