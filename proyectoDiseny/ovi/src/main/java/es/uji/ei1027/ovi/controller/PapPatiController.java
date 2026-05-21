package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.assistancerequest.AssistanceRequestDao;
import es.uji.ei1027.ovi.dao.contract.ContractDao;
import es.uji.ei1027.ovi.dao.credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.negotiation.NegotiationDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.dao.pappatischedule.ScheduleDao;
import es.uji.ei1027.ovi.dao.recommendedpappati.RecommendedPapPatiDao;
import es.uji.ei1027.ovi.dao.requestschedule.RequestScheduleDao;
import es.uji.ei1027.ovi.model.*;
import es.uji.ei1027.ovi.utils.PasswordUtils;
import es.uji.ei1027.ovi.validator.ChangePasswordValidator;
import es.uji.ei1027.ovi.validator.PapPatiValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/papPati")
public class PapPatiController {

    private static final String CANVI_CONTRASENYA_PAPPATI_VIEW = "papPati/canviarContrasenya";
    private static final String USER_ATTR = "user";

    private PapPatiDao papPatiDao;
    private CredentialsDao credentialsDao;
    private ScheduleDao scheduleDao;
    private AssistanceRequestDao assistanceRequestDao;
    private NegotiationDao negotiationDao;
    private RecommendedPapPatiDao recommendedPapPatiDao;
    private OviUserDao oviUserDao;
    private ContractDao contractDao;
    private RequestScheduleDao requestScheduleDao;

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @Autowired
    public void setCredentialsDao(CredentialsDao credentialsDao) {
        this.credentialsDao = credentialsDao;
    }

    @Autowired
    public void setScheduleDao(ScheduleDao scheduleDao) { this.scheduleDao = scheduleDao; }

    @Autowired
    public void setAssistanceRequestDao(AssistanceRequestDao assistanceRequestDao) {
        this.assistanceRequestDao = assistanceRequestDao;
    }

    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao) {
        this.negotiationDao = negotiationDao;
    }

    @Autowired
    public void setRecommendedPapPatiDao(RecommendedPapPatiDao recommendedPapPatiDao) {
        this.recommendedPapPatiDao = recommendedPapPatiDao;
    }

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    @Autowired
    public void setRequestScheduleDao(RequestScheduleDao requestScheduleDao) {
        this.requestScheduleDao = requestScheduleDao;
    }

    // =====================================================================
    // REGISTRE
    // =====================================================================

    @RequestMapping("/register")
    public String addPapPati(Model model) {
        model.addAttribute("pappati", new PapPatiRegistration());
        return "papPati/register";
    }

    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("pappati") PapPatiRegistration registration,
                                   BindingResult bindingResult) {

        PapPatiValidator validator = new PapPatiValidator();
        validator.validate(registration, bindingResult);

        // Comprovació de duplicats abans d'inserir
        if (registration.getUsername() != null
                && credentialsDao.getCredentials(registration.getUsername()) != null) {
            bindingResult.rejectValue("username", "duplicat",
                    "Aquest nom d'usuari ja està agafat");
        }

        if (registration.getEmailAddress() != null
                && papPatiDao.existsEmail(registration.getEmailAddress())) {
            bindingResult.rejectValue("emailAddress", "duplicat",
                    "Aquest correu electrònic ja està registrat");
        }

        if (bindingResult.hasErrors()) {
            return "papPati/register";
        }

        registration.setStatus("approvalPending");

        Credentials credentials = new Credentials();
        credentials.setUsername(registration.getUsername());
        credentials.setPassword(PasswordUtils.encrypt(registration.getPassword()));
        credentials.setRole("pap_pati");
        credentials.setActivated(false);
        credentials.setId(0);
        credentialsDao.addCredentials(credentials);

        papPatiDao.addPapPati(registration);

        int papID = papPatiDao.getLastInsertedId();
        credentialsDao.updateId(registration.getUsername(), papID);

        return "redirect:registerSuccess";
    }

    @RequestMapping("/registerSuccess")
    public String registerSuccess() {
        return "registerSuccess";
    }

    // =====================================================================
    // PORTAL I DADES PERSONALS
    // =====================================================================

    @RequestMapping("/portal")
    public String portal(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        model.addAttribute("teHoraris", scheduleDao.hasSchedules(papPati.getPapID()));
        return "papPati/portal";
    }

    @RequestMapping("/edit")
    public String editPapPati(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        model.addAttribute("pappati", papPati);
        return "papPati/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String processEditSubmit(@ModelAttribute("pappati") PapPati papPati,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        PapPatiValidator validator = new PapPatiValidator();
        validator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            return "papPati/edit";
        }

        // Forçar que els camps d'identitat vinguen de BBDD, no del formulari
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        PapPati dbPapPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        papPati.setPapID(dbPapPati.getPapID());
        papPati.setUsername(dbPapPati.getUsername());
        papPati.setStatus(dbPapPati.getStatus());

        papPatiDao.updatePapPati(papPati);

        redirectAttributes.addFlashAttribute("successMessage",
                "Les teues dades s'han guardat correctament");
        return "redirect:portal";
    }

    // =====================================================================
    // CANVI DE CONTRASENYA
    // =====================================================================

    @RequestMapping("/canviarContrasenya")
    public String canviarContrasenya(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return CANVI_CONTRASENYA_PAPPATI_VIEW;
    }

    @RequestMapping(value = "/canviarContrasenya", method = RequestMethod.POST)
    public String processCanviarContrasenya(@ModelAttribute("changePasswordForm") ChangePasswordForm form,
                                            BindingResult bindingResult,
                                            HttpSession session,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {

        ChangePasswordValidator validator = new ChangePasswordValidator();
        validator.validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return CANVI_CONTRASENYA_PAPPATI_VIEW;
        }

        // Llegim la contrasenya actual de BBDD (no de la sessió, que està sanititzada)
        Credentials sessionCredentials = (Credentials) session.getAttribute(USER_ATTR);
        Credentials dbCredentials = credentialsDao.getCredentials(sessionCredentials.getUsername());

        if (!PasswordUtils.check(form.getCurrentPassword(), dbCredentials.getPassword())) {
            model.addAttribute("errorActual", "La contrasenya actual no és correcta");
            return CANVI_CONTRASENYA_PAPPATI_VIEW;
        }

        String newEncryptedPassword = PasswordUtils.encrypt(form.getNewPassword());
        credentialsDao.updatePassword(sessionCredentials.getUsername(), newEncryptedPassword);

        redirectAttributes.addFlashAttribute("successMessage",
                "La teua contrasenya s'ha canviat correctament");
        return "redirect:portal";
    }

    // =====================================================================
    // HORARIS
    // =====================================================================

    @RequestMapping("/horaris")
    public String horaris(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        List<Schedule> schedules = scheduleDao.getSchedulesByPap(papPati.getPapID());
        model.addAttribute("schedules", schedules);
        model.addAttribute("papID", papPati.getPapID());
        return "papPati/horaris";
    }

    @RequestMapping("/horaris/add")
    public String addHorari(Model model) {
        model.addAttribute("schedule", new Schedule());
        return "papPati/horarisForm";
    }

    @RequestMapping(value = "/horaris/add", method = RequestMethod.POST)
    public String processAddHorari(@ModelAttribute("schedule") Schedule schedule,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        boolean hasErrors = false;

        if (schedule.getDayOfWeek() == 0) {
            model.addAttribute("errorDia", "El dia és obligatori");
            hasErrors = true;
        }

        if (schedule.getStartHour() == null) {
            model.addAttribute("errorInici", "L'hora d'inici és obligatòria");
            hasErrors = true;
        }

        if (schedule.getEndHour() == null) {
            model.addAttribute("errorFi", "L'hora de fi és obligatòria");
            hasErrors = true;
        }

        if (schedule.getStartHour() != null && schedule.getEndHour() != null
                && !schedule.getEndHour().isAfter(schedule.getStartHour())) {
            model.addAttribute("errorFi", "L'hora de fi ha de ser posterior a l'hora d'inici");
            hasErrors = true;
        }

        if (hasErrors) {
            return "papPati/horarisForm";
        }

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        schedule.setPapID(papPati.getPapID());
        scheduleDao.addSchedule(schedule);

        redirectAttributes.addFlashAttribute("successMessage",
                "L'horari s'ha afegit correctament");
        return "redirect:/papPati/horaris";
    }

    @RequestMapping(value = "/horaris/delete/{scheduleID}", method = RequestMethod.POST)
    public String deleteHorari(@PathVariable("scheduleID") int scheduleID,
                               RedirectAttributes redirectAttributes) {
        scheduleDao.deleteSchedule(scheduleID);

        redirectAttributes.addFlashAttribute("successMessage",
                "L'horari s'ha eliminat correctament");
        return "redirect:/papPati/horaris";
    }

    // =====================================================================
    // SOL·LICITUDS RECOMANADES (Bloc 3)
    // =====================================================================

    @RequestMapping("/solicitudes")
    public String solicitudes(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());

        List<RecommendedPapPati> recomanacions = recommendedPapPatiDao.getRecommendedByPap(papPati.getPapID());
        Map<Integer, AssistanceRequest> solicituds = new HashMap<>();
        Map<Integer, Negotiation> negotiations = new HashMap<>();
        Map<Integer, OviUser> oviUsers = new HashMap<>();

        for (RecommendedPapPati rec : recomanacions) {
            AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(rec.getRequestID());
            solicituds.put(rec.getRequestID(), sol);
            OviUser oviUser = oviUserDao.getOviUser(sol.getOviID());
            oviUsers.put(rec.getRequestID(), oviUser);
            Negotiation neg = negotiationDao.getNegotiationByRequestAndPap(rec.getRequestID(), papPati.getPapID());
            if (neg != null) {
                negotiations.put(rec.getRequestID(), neg);
            }
        }

        Map<Integer, Contract> contractesPerSolicitud = new HashMap<>();
        for (RecommendedPapPati rec : recomanacions) {
            Negotiation neg = negotiationDao.getNegotiationByRequestAndPap(rec.getRequestID(), papPati.getPapID());
            if (neg != null) {
                Contract contract = contractDao.getContractByNegotiation(neg.getNegotiationID());
                if (contract != null) {
                    contractesPerSolicitud.put(rec.getRequestID(), contract);
                }
            }
        }

        model.addAttribute("contractesPerSolicitud", contractesPerSolicitud);
        model.addAttribute("recomanacions", recomanacions);
        model.addAttribute("solicituds", solicituds);
        model.addAttribute("negotiations", negotiations);
        model.addAttribute("oviUsers", oviUsers);
        return "papPati/solicitudes";
    }

    @RequestMapping("/contractes")
    public String contractes(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        List<Contract> contractes = contractDao.getContractsByPapPati(papPati.getPapID());

        Map<Integer, Negotiation> negotiations = new HashMap<>();
        Map<Integer, OviUser> oviUsers = new HashMap<>();
        for (Contract c : contractes) {
            Negotiation neg = negotiationDao.getNegotiation(c.getNegotiationID());
            negotiations.put(c.getContractID(), neg);
            AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
            oviUsers.put(c.getContractID(), oviUserDao.getOviUser(sol.getOviID()));
        }

        model.addAttribute("contractes", contractes);
        model.addAttribute("negotiations", negotiations);
        model.addAttribute("oviUsers", oviUsers);
        return "papPati/contractes";
    }

    // Mostra el detall d'un contracte concret del PAP/PATI
    @RequestMapping("/contractes/{contractID}")
    public String contracteDetail(@PathVariable int contractID,
                                  @RequestParam(value = "from", required = false) String from,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());

        Contract contract = contractDao.getContract(contractID);
        if (contract == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest contracte no existeix");
            return "redirect:/papPati/contractes";
        }

        // Verificar que el contracte pertany a aquest PAP/PATI
        Negotiation neg = negotiationDao.getNegotiation(contract.getNegotiationID());
        if (neg.getPapID() != papPati.getPapID()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No tens permís per a veure aquest contracte");
            return "redirect:/papPati/contractes";
        }

        AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        OviUser oviUser = oviUserDao.getOviUser(sol.getOviID());
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(neg.getRequestID());

        model.addAttribute("contract", contract);
        model.addAttribute("neg", neg);
        model.addAttribute("sol", sol);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("horaris", horaris);
        model.addAttribute("from", from);

        return "papPati/contracteDetail";
    }
}