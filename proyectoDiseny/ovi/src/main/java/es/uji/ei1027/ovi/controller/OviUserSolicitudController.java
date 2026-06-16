package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.assistancerequest.AssistanceRequestDao;
import es.uji.ei1027.ovi.dao.contract.ContractDao;
import es.uji.ei1027.ovi.dao.negotiation.NegotiationDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.dao.recommendedpappati.RecommendedPapPatiDao;
import es.uji.ei1027.ovi.dao.requestschedule.RequestScheduleDao;
import es.uji.ei1027.ovi.model.*;
import es.uji.ei1027.ovi.validator.AssistanceRequestValidator;
import es.uji.ei1027.ovi.validator.RequestScheduleValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/oviUser/solicitudes")
public class OviUserSolicitudController {

    private static final String USER_ATTR = "user";
    private static final String REDIRECT_LIST = "redirect:/oviUser/solicitudes";

    private AssistanceRequestDao assistanceRequestDao;
    private RequestScheduleDao requestScheduleDao;
    private OviUserDao oviUserDao;
    private RecommendedPapPatiDao recommendedPapPatiDao;
    private PapPatiDao papPatiDao;
    private NegotiationDao negotiationDao;
    private ContractDao contractDao;

    @Autowired
    public void setAssistanceRequestDao(AssistanceRequestDao assistanceRequestDao) {
        this.assistanceRequestDao = assistanceRequestDao;
    }

    @Autowired
    public void setRequestScheduleDao(RequestScheduleDao requestScheduleDao) {
        this.requestScheduleDao = requestScheduleDao;
    }

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setRecommendedPapPatiDao(RecommendedPapPatiDao recommendedPapPatiDao) {
        this.recommendedPapPatiDao = recommendedPapPatiDao;
    }

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao) {
        this.negotiationDao = negotiationDao;
    }

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    /**
     * Comprova que la sol·licitud pertany a l'usuari logat. Retorna la sol·licitud
     * si tot és correcte, o null si no existeix o no és seua.
     */
    private AssistanceRequest getOwnedRequest(int requestID, HttpSession session) {
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        if (solicitud == null) {
            return null;
        }
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        if (solicitud.getOviID() != oviUser.getOviID()) {
            return null;
        }
        return solicitud;
    }

    // =====================================================================
    // LLISTAT
    // =====================================================================

    @RequestMapping
    public String list(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        List<AssistanceRequest> solicitudes = assistanceRequestDao.getAssistanceRequestsByUser(oviUser.getOviID());
        model.addAttribute("solicitudes", solicitudes);
        return "oviuser/solicitudes/list";
    }

    // =====================================================================
    // ALTA
    // =====================================================================

    @RequestMapping("/nova")
    public String triarTipus() {
        return "oviuser/solicitudes/tipus";
    }

    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("solicitud", new AssistanceRequest());
        return "oviuser/solicitudes/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAdd(@ModelAttribute("solicitud") AssistanceRequest solicitud,
                             BindingResult bindingResult,
                             HttpSession session) {

        AssistanceRequestValidator validator = new AssistanceRequestValidator();
        validator.validate(solicitud, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviuser/solicitudes/add";
        }

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        solicitud.setOviID(oviUser.getOviID());
        solicitud.setStatus("inProgress");
        solicitud.setType("rigid");
        assistanceRequestDao.addAssistanceRequest(solicitud);

        int requestID = assistanceRequestDao.getLastInsertedId();
        return "redirect:/oviUser/solicitudes/" + requestID + "/horaris?nova=true";
    }

    @RequestMapping("/addFlexible")
    public String addFlexible(Model model) {
        AssistanceRequest solicitud = new AssistanceRequest();
        solicitud.setType("flexible");
        model.addAttribute("solicitud", solicitud);
        return "oviuser/solicitudes/addFlexible";
    }

    @RequestMapping(value = "/addFlexible", method = RequestMethod.POST)
    public String processAddFlexible(@ModelAttribute("solicitud") AssistanceRequest solicitud,
                                     BindingResult bindingResult,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        // Marquem el tipus abans de validar perquè el validator aplique
        // les comprovacions específiques de les sol·licituds flexibles.
        solicitud.setType("flexible");

        AssistanceRequestValidator validator = new AssistanceRequestValidator();
        validator.validate(solicitud, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviuser/solicitudes/addFlexible";
        }

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        solicitud.setOviID(oviUser.getOviID());
        solicitud.setStatus("inProgress");
        assistanceRequestDao.addAssistanceRequest(solicitud);

        redirectAttributes.addFlashAttribute("successMessage",
                "La sol·licitud flexible s'ha creat correctament");
        return REDIRECT_LIST;
    }

    // =====================================================================
    // EDICIÓ
    // =====================================================================

    @RequestMapping("/edit/{requestID}")
    public String edit(@PathVariable int requestID, HttpSession session, Model model) {
        AssistanceRequest solicitud = getOwnedRequest(requestID, session);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }
        model.addAttribute("solicitud", solicitud);
        return "oviuser/solicitudes/edit";
    }

    @RequestMapping(value = "/edit/{requestID}", method = RequestMethod.POST)
    public String processEdit(@PathVariable int requestID,
                              @ModelAttribute("solicitud") AssistanceRequest solicitud,
                              BindingResult bindingResult,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        AssistanceRequest existent = getOwnedRequest(requestID, session);
        if (existent == null) {
            return REDIRECT_LIST;
        }

        AssistanceRequestValidator validator = new AssistanceRequestValidator();
        validator.validate(solicitud, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviuser/solicitudes/edit";
        }

        // Forçar identitat de la sol·licitud des de BBDD
        solicitud.setRequestID(existent.getRequestID());
        solicitud.setOviID(existent.getOviID());
        solicitud.setStatus(existent.getStatus());
        solicitud.setCreationDate(existent.getCreationDate());
        solicitud.setType(existent.getType());
        // En rígides forcem dates a null des de BBDD; en flexibles conservem
        // les que ha editat l'usuari (ja validades pel validator).
        if (!"flexible".equals(existent.getType())) {
            solicitud.setStartServiceDate(existent.getStartServiceDate());
            solicitud.setEndServiceDate(existent.getEndServiceDate());
        }

        assistanceRequestDao.updateAssistanceRequest(solicitud);

        redirectAttributes.addFlashAttribute("successMessage",
                "La sol·licitud s'ha actualitzat correctament");
        return "redirect:/oviUser/solicitudes/" + requestID;
    }

    // =====================================================================
    // ELIMINAR
    // =====================================================================

    @Transactional
    @RequestMapping(value = "/delete/{requestID}", method = RequestMethod.POST)
    public String delete(@PathVariable int requestID,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        AssistanceRequest solicitud = getOwnedRequest(requestID, session);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }

        requestScheduleDao.deleteSchedulesByRequest(requestID);
        assistanceRequestDao.deleteAssistanceRequest(requestID);

        redirectAttributes.addFlashAttribute("successMessage",
                "La sol·licitud s'ha eliminat correctament");
        return REDIRECT_LIST;
    }

    // =====================================================================
    // DETALL
    // =====================================================================

    @RequestMapping("/{requestID}")
    public String detail(@PathVariable int requestID, @RequestParam(value = "from", required = false) String from, HttpSession session, Model model) {
        AssistanceRequest solicitud = getOwnedRequest(requestID, session);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }

        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(requestID);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("horaris", horaris);
        model.addAttribute("from", from);

        if (solicitud.getStatus().equals("accepted") ||
                solicitud.getStatus().equals("closedWithContract") ||
                solicitud.getStatus().equals("closedContractEnded")) {
            List<RecommendedPapPati> recomanats = recommendedPapPatiDao.getRecommendedByRequest(requestID);
            Map<Integer, PapPati> infoPapPatis = new HashMap<>();
            Map<Integer, Negotiation> negotiations = new HashMap<>();
            Map<Integer, Contract> contractesPerPap = new HashMap<>();

            for (RecommendedPapPati rec : recomanats) {
                infoPapPatis.put(rec.getPapID(), papPatiDao.getPapPati(rec.getPapID()));
                Negotiation neg = negotiationDao.getNegotiationByRequestAndPap(requestID, rec.getPapID());
                if (neg != null) {
                    negotiations.put(rec.getPapID(), neg);
                    Contract contract = contractDao.getContractByNegotiation(neg.getNegotiationID());
                    if (contract != null) {
                        contractesPerPap.put(rec.getPapID(), contract);
                    }
                }
            }
            model.addAttribute("recomanats", recomanats);
            model.addAttribute("infoPapPatis", infoPapPatis);
            model.addAttribute("negotiations", negotiations);
            model.addAttribute("contractesPerPap", contractesPerPap);
        }

        return "oviuser/solicitudes/detail";
    }

    // =====================================================================
    // HORARIS DE LA SOL·LICITUD
    // =====================================================================

    @RequestMapping("/{requestID}/horaris")
    public String horaris(@PathVariable int requestID,
                          @RequestParam(value = "nova", defaultValue = "false") boolean nova,
                          HttpSession session,
                          Model model) {
        AssistanceRequest solicitud = getOwnedRequest(requestID, session);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }

        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(requestID);
        model.addAttribute("horaris", horaris);
        model.addAttribute("requestID", requestID);
        model.addAttribute("newHorari", new RequestSchedule());
        model.addAttribute("esNova", nova);
        return "oviuser/solicitudes/horaris";
    }

    @RequestMapping(value = "/{requestID}/horaris/add", method = RequestMethod.POST)
    public String addHorari(@PathVariable int requestID,
                            @ModelAttribute("newHorari") RequestSchedule horari,
                            BindingResult bindingResult,
                            HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        AssistanceRequest solicitud = getOwnedRequest(requestID, session);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }

        RequestScheduleValidator validator = new RequestScheduleValidator();
        validator.validate(horari, bindingResult);

        if (bindingResult.hasErrors()) {
            List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(requestID);
            model.addAttribute("horaris", horaris);
            model.addAttribute("requestID", requestID);
            model.addAttribute("esNova", true);
            return "oviuser/solicitudes/horaris";
        }

        horari.setRequestID(requestID);
        horari.setDayOfWeek(horari.getDate().getDayOfWeek().getValue());
        requestScheduleDao.addRequestSchedule(horari);

        return "redirect:/oviUser/solicitudes/" + requestID + "/horaris?nova=true";
    }

    @RequestMapping(value = "/{requestID}/horaris/delete/{horariID}", method = RequestMethod.POST)
    public String deleteHorari(@PathVariable int requestID,
                               @PathVariable int horariID,
                               @RequestParam(value = "nova", defaultValue = "false") boolean nova,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        AssistanceRequest solicitud = getOwnedRequest(requestID, session);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }

        requestScheduleDao.deleteRequestSchedule(horariID);

        redirectAttributes.addFlashAttribute("successMessage",
                "L'horari s'ha eliminat correctament");
        return "redirect:/oviUser/solicitudes/" + requestID + "/horaris?nova=" + nova;
    }

    // =====================================================================
    // INICIAR NEGOCIACIÓ
    // =====================================================================

    @RequestMapping(value = "/{requestID}/negociar/{papID}", method = RequestMethod.POST)
    public String iniciarNegociacio(@PathVariable int requestID,
                                    @PathVariable int papID,
                                    HttpSession session) {
        AssistanceRequest solicitud = getOwnedRequest(requestID, session);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }

        Negotiation neg = negotiationDao.getNegotiationByRequestAndPap(requestID, papID);
        if (neg == null) {
            neg = new Negotiation();
            neg.setRequestID(requestID);
            neg.setPapID(papID);
            neg.setStatus("inProgress");
            neg.setDateAndTime(LocalDateTime.now());
            neg.setConversation("");
            neg.setOviUserConfirmed(false);
            neg.setPapPatiConfirmed(false);
            negotiationDao.addNegotiation(neg);
            int negID = negotiationDao.getLastInsertedId();
            return "redirect:/oviUser/negociacio/" + negID;
        }
        return "redirect:/oviUser/negociacio/" + neg.getNegotiationID();
    }


    @RequestMapping(value = "/{requestID}/finalizar", method = RequestMethod.POST)
    public String finalizarSolicitud(@PathVariable int requestID,
                                     RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage",
                "La sol·licitud s'ha creat correctament");
        return "redirect:/oviUser/solicitudes";
    }

    // =====================================================================
    // PÀGINES INTERMÈDIES DE CONFIRMACIÓ (acions destructives)
    // =====================================================================

    @RequestMapping(value = "/delete/{requestID}/confirm", method = RequestMethod.GET)
    public String confirmDelete(@PathVariable int requestID,
                                @RequestParam(value = "nova", defaultValue = "false") boolean nova,
                                HttpSession session,
                                Model model) {
        AssistanceRequest solicitud = getOwnedRequest(requestID, session);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }

        if (nova) {
            // Cancel·lació durant la creació d'una sol·licitud nova
            model.addAttribute("titol", "Cancel·lar la creació de la sol·licitud");
            model.addAttribute("missatge",
                    "Estàs a punt de cancel·lar la creació d'aquesta sol·licitud.");
            model.addAttribute("detall",
                    "S'esborraran totes les dades i els horaris afegits fins ara. Aquesta acció no es pot desfer.");
            model.addAttribute("confirmLabel", "Sí, cancel·lar sol·licitud");
            model.addAttribute("actionUrl", "/oviUser/solicitudes/delete/" + requestID);
            model.addAttribute("cancelUrl", "/oviUser/solicitudes/" + requestID + "/horaris?nova=true");
        } else {
            // Eliminació d'una sol·licitud existent des del llistat
            model.addAttribute("titol", "Eliminar la sol·licitud");
            model.addAttribute("missatge",
                    "Estàs a punt d'eliminar aquesta sol·licitud d'assistència.");
            model.addAttribute("detall",
                    "S'esborraran la sol·licitud i tots els seus horaris associats. Aquesta acció no es pot desfer.");
            model.addAttribute("confirmLabel", "Sí, eliminar sol·licitud");
            model.addAttribute("actionUrl", "/oviUser/solicitudes/delete/" + requestID);
            model.addAttribute("cancelUrl", "/oviUser/solicitudes");
        }
        model.addAttribute("tipusAccio", "perillosa");
        return "fragments/confirm";
    }
}