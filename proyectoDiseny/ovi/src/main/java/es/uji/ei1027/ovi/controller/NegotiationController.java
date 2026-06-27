package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.assistancerequest.AssistanceRequestDao;
import es.uji.ei1027.ovi.dao.contract.ContractDao;
import es.uji.ei1027.ovi.dao.negotiation.NegotiationDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.dao.requestschedule.RequestScheduleDao;
import es.uji.ei1027.ovi.model.*;
import es.uji.ei1027.ovi.validator.ContractValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class NegotiationController {

    private static final String USER_ATTR = "user";
    private static final String ROL_OVI = "oviUser";
    private static final String ROL_PAP = "papPati";

    private NegotiationDao negotiationDao;
    private OviUserDao oviUserDao;
    private PapPatiDao papPatiDao;
    private AssistanceRequestDao assistanceRequestDao;
    private RequestScheduleDao requestScheduleDao;
    private ContractDao contractDao;

    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao) {
        this.negotiationDao = negotiationDao;
    }

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
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

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    // =====================================================================
    // HELPERS
    // =====================================================================

    /*
     * Comprova que la negociació pertany a l'OviUser logat. Retorna la
     * negociació si tot és correcte, o null si no existeix o no és seua.
     */
    private Negotiation getOwnedNegotiationOvi(int negotiationID, HttpSession session) {
        Negotiation neg = negotiationDao.getNegotiation(negotiationID);
        if (neg == null) return null;

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());

        AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        if (sol == null || sol.getOviID() != oviUser.getOviID()) {
            return null;
        }
        return neg;
    }

    /*
     * Comprova que la negociació pertany al PAP/PATI logat.
     */
    private Negotiation getOwnedNegotiationPap(int negotiationID, HttpSession session) {
        Negotiation neg = negotiationDao.getNegotiation(negotiationID);
        if (neg == null) return null;

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());

        if (papPati == null || neg.getPapID() != papPati.getPapID()) {
            return null;
        }
        return neg;
    }

    // =====================================================================
    // DETALL DE LA NEGOCIACIÓ
    // =====================================================================

    @RequestMapping("/oviUser/negociacio/{negociacioID}")
    public String detallOviUser(@PathVariable int negociacioID,
                                HttpSession session, Model model) {
        Negotiation neg = getOwnedNegotiationOvi(negociacioID, session);
        if (neg == null) {
            return "redirect:/oviUser/solicitudes";
        }

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        PapPati papPati = papPatiDao.getPapPati(neg.getPapID());
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(neg.getRequestID());

        model.addAttribute("neg", neg);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("papPati", papPati);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("horaris", horaris);
        model.addAttribute("rol", ROL_OVI);
        return "negociacio/detail";
    }

    @RequestMapping("/papPati/negociacio/{negociacioID}")
    public String detallPapPati(@PathVariable int negociacioID,
                                HttpSession session, Model model) {
        Negotiation neg = getOwnedNegotiationPap(negociacioID, session);
        if (neg == null) {
            return "redirect:/papPati/portal";
        }

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        OviUser oviUser = oviUserDao.getOviUser(solicitud.getOviID());
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(neg.getRequestID());

        model.addAttribute("neg", neg);
        model.addAttribute("papPati", papPati);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("horaris", horaris);
        model.addAttribute("rol", ROL_PAP);
        return "negociacio/detail";
    }

    // =====================================================================
    // AFEGIR MISSATGE A LA CONVERSA
    // =====================================================================

    @RequestMapping(value = "/oviUser/negociacio/{negociacioID}/missatge", method = RequestMethod.POST)
    public String afegirMissatgeOvi(@PathVariable int negociacioID,
                                    @RequestParam("missatge") String missatge,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Negotiation neg = getOwnedNegotiationOvi(negociacioID, session);
        if (neg == null) {
            return "redirect:/oviUser/solicitudes";
        }
        return afegirMissatgeIntern(neg, missatge, ROL_OVI, session, redirectAttributes);
    }

    @RequestMapping(value = "/papPati/negociacio/{negociacioID}/missatge", method = RequestMethod.POST)
    public String afegirMissatgePap(@PathVariable int negociacioID,
                                    @RequestParam("missatge") String missatge,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Negotiation neg = getOwnedNegotiationPap(negociacioID, session);
        if (neg == null) {
            return "redirect:/papPati/portal";
        }
        return afegirMissatgeIntern(neg, missatge, ROL_PAP, session, redirectAttributes);
    }

    private String afegirMissatgeIntern(Negotiation neg, String missatge, String rol,
                                        HttpSession session, RedirectAttributes redirectAttributes) {
        // Només es poden afegir missatges si la negociació està en curs
        if (!"inProgress".equals(neg.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació ja està tancada");
            return "redirect:/" + rol + "/negociacio/" + neg.getNegotiationID();
        }

        // Validar missatge no buit
        if (missatge == null || missatge.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "El missatge no pot estar buit");
            return "redirect:/" + rol + "/negociacio/" + neg.getNegotiationID();
        }

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        String prefix = rol.equals(ROL_OVI)
                ? "[" + credentials.getUsername() + " - OVI User] "
                : "[" + credentials.getUsername() + " - PAP/PATI] ";

        String conversacioActual = neg.getConversation() == null ? "" : neg.getConversation();
        String novaConversacio = conversacioActual.isEmpty()
                ? prefix + missatge
                : conversacioActual + "\n" + prefix + missatge;

        neg.setConversation(novaConversacio);
        negotiationDao.updateNegotiation(neg);

        return "redirect:/" + rol + "/negociacio/" + neg.getNegotiationID();
    }

    // =====================================================================
    // CONFIRMAR ACORD
    // =====================================================================

    @Transactional
    @RequestMapping(value = "/oviUser/negociacio/{negociacioID}/confirmar", method = RequestMethod.POST)
    public String confirmarOviUser(@PathVariable int negociacioID,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Negotiation neg = getOwnedNegotiationOvi(negociacioID, session);
        if (neg == null) {
            return "redirect:/oviUser/solicitudes";
        }

        if (!"inProgress".equals(neg.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació ja està tancada");
            return "redirect:/oviUser/negociacio/" + negociacioID;
        }

        neg.setOviUserConfirmed(true);
        if (neg.isPapPatiConfirmed()) {
            neg.setStatus("finished");
            tancarAltresNegociacions(neg.getRequestID(), negociacioID);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Acord confirmat. La negociació s'ha tancat amb èxit.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Has confirmat l'acord. Esperant la confirmació del professional d'assistència.");
        }
        negotiationDao.updateNegotiation(neg);
        return "redirect:/oviUser/negociacio/" + negociacioID;
    }

    @Transactional
    @RequestMapping(value = "/papPati/negociacio/{negociacioID}/confirmar", method = RequestMethod.POST)
    public String confirmarPapPati(@PathVariable int negociacioID,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Negotiation neg = getOwnedNegotiationPap(negociacioID, session);
        if (neg == null) {
            return "redirect:/papPati/portal";
        }

        if (!"inProgress".equals(neg.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació ja està tancada");
            return "redirect:/papPati/negociacio/" + negociacioID;
        }

        neg.setPapPatiConfirmed(true);
        if (neg.isOviUserConfirmed()) {
            neg.setStatus("finished");
            tancarAltresNegociacions(neg.getRequestID(), negociacioID);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Acord confirmat. La negociació s'ha tancat amb èxit.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Has confirmat l'acord. Esperant la confirmació de l'usuari OVI.");
        }
        negotiationDao.updateNegotiation(neg);
        return "redirect:/papPati/negociacio/" + negociacioID;
    }

    /*
     * Quan una negociació es tanca amb acord, es marquen com 'noAgreement' la
     * resta de negociacions de la mateixa sol·licitud que encara estan en curs.
     */
    private void tancarAltresNegociacions(int requestID, int negociacioID) {
        List<Negotiation> altres = negotiationDao.getNegotiationsByRequest(requestID);
        for (Negotiation altra : altres) {
            if (altra.getNegotiationID() != negociacioID
                    && "inProgress".equals(altra.getStatus())) {
                altra.setStatus("noAgreement");
                negotiationDao.updateNegotiation(altra);
            }
        }
    }

    // =====================================================================
    // NO ACORD
    // =====================================================================

    @RequestMapping(value = "/oviUser/negociacio/{negociacioID}/noAcord", method = RequestMethod.POST)
    public String noAcordOviUser(@PathVariable int negociacioID,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Negotiation neg = getOwnedNegotiationOvi(negociacioID, session);
        if (neg == null) {
            return "redirect:/oviUser/solicitudes";
        }

        if (!"inProgress".equals(neg.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació ja està tancada");
            return "redirect:/oviUser/negociacio/" + negociacioID;
        }

        neg.setStatus("noAgreement");
        negotiationDao.updateNegotiation(neg);

        redirectAttributes.addFlashAttribute("successMessage",
                "Has marcat la negociació com 'sense acord'");
        return "redirect:/oviUser/negociacio/" + negociacioID;
    }

    @RequestMapping(value = "/papPati/negociacio/{negociacioID}/noAcord", method = RequestMethod.POST)
    public String noAcordPapPati(@PathVariable int negociacioID,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Negotiation neg = getOwnedNegotiationPap(negociacioID, session);
        if (neg == null) {
            return "redirect:/papPati/portal";
        }

        if (!"inProgress".equals(neg.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació ja està tancada");
            return "redirect:/papPati/negociacio/" + negociacioID;
        }

        neg.setStatus("noAgreement");
        negotiationDao.updateNegotiation(neg);

        redirectAttributes.addFlashAttribute("successMessage",
                "Has marcat la negociació com 'sense acord'");
        return "redirect:/papPati/negociacio/" + negociacioID;
    }

    // =====================================================================
// PÀGINES INTERMÈDIES DE CONFIRMACIÓ (acions destructives)
// =====================================================================

    @RequestMapping(value = "/oviUser/negociacio/{negociacioID}/confirmar/confirm", method = RequestMethod.GET)
    public String confirmAcordOvi(@PathVariable int negociacioID,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        Negotiation neg = getOwnedNegotiationOvi(negociacioID, session);
        if (neg == null) {
            return "redirect:/oviUser/solicitudes";
        }
        if (!"inProgress".equals(neg.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació ja està tancada");
            return "redirect:/oviUser/negociacio/" + negociacioID;
        }

        PapPati papPati = papPatiDao.getPapPati(neg.getPapID());

        model.addAttribute("titol", "Confirmar acord amb el PAP/PATI");
        model.addAttribute("missatge",
                "Estàs a punt de confirmar l'acord amb " + papPati.getNameAndSurname() + ".");
        model.addAttribute("detall",
                "Una vegada confirmat per ambdues parts, la negociació es tancarà i el tècnic OVI podrà formalitzar el contracte. La resta de negociacions actives d'aquesta sol·licitud es tancaran automàticament sense acord.");
        model.addAttribute("actionUrl", "/oviUser/negociacio/" + negociacioID + "/confirmar");
        model.addAttribute("cancelUrl", "/oviUser/negociacio/" + negociacioID);
        model.addAttribute("confirmLabel", "Sí, confirmar acord");
        model.addAttribute("tipusAccio", "normal");
        return "fragments/confirm";
    }

    @RequestMapping(value = "/oviUser/negociacio/{negociacioID}/noAcord/confirm", method = RequestMethod.GET)
    public String confirmNoAcordOvi(@PathVariable int negociacioID,
                                    HttpSession session,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        Negotiation neg = getOwnedNegotiationOvi(negociacioID, session);
        if (neg == null) {
            return "redirect:/oviUser/solicitudes";
        }
        if (!"inProgress".equals(neg.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació ja està tancada");
            return "redirect:/oviUser/negociacio/" + negociacioID;
        }

        PapPati papPati = papPatiDao.getPapPati(neg.getPapID());

        model.addAttribute("titol", "Confirmar finalització sense acord");
        model.addAttribute("missatge",
                "Estàs a punt de donar per finalitzada la negociació amb " + papPati.getNameAndSurname() + " sense acord.");
        model.addAttribute("detall",
                "La negociació quedarà marcada com a tancada sense acord. Aquesta acció no es pot desfer.");
        model.addAttribute("actionUrl", "/oviUser/negociacio/" + negociacioID + "/noAcord");
        model.addAttribute("cancelUrl", "/oviUser/negociacio/" + negociacioID);
        model.addAttribute("confirmLabel", "Sí, finalitzar sense acord");
        model.addAttribute("tipusAccio", "perillosa");
        return "fragments/confirm";
    }

    @RequestMapping(value = "/papPati/negociacio/{negociacioID}/confirmar/confirm", method = RequestMethod.GET)
    public String confirmAcordPap(@PathVariable int negociacioID,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        Negotiation neg = getOwnedNegotiationPap(negociacioID, session);
        if (neg == null) {
            return "redirect:/papPati/portal";
        }
        if (!"inProgress".equals(neg.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació ja està tancada");
            return "redirect:/papPati/negociacio/" + negociacioID;
        }

        AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        OviUser oviUser = oviUserDao.getOviUser(sol.getOviID());

        model.addAttribute("titol", "Confirmar acord amb l'usuari OVI");
        model.addAttribute("missatge",
                "Estàs a punt de confirmar l'acord amb " + oviUser.getNameAndSurname() + ".");
        model.addAttribute("detall",
                "Una vegada confirmat per ambdues parts, la negociació es tancarà i el tècnic OVI podrà formalitzar el contracte.");
        model.addAttribute("actionUrl", "/papPati/negociacio/" + negociacioID + "/confirmar");
        model.addAttribute("cancelUrl", "/papPati/negociacio/" + negociacioID);
        model.addAttribute("confirmLabel", "Sí, confirmar acord");
        model.addAttribute("tipusAccio", "normal");
        return "fragments/confirm";
    }

    @RequestMapping(value = "/papPati/negociacio/{negociacioID}/noAcord/confirm", method = RequestMethod.GET)
    public String confirmNoAcordPap(@PathVariable int negociacioID,
                                    HttpSession session,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        Negotiation neg = getOwnedNegotiationPap(negociacioID, session);
        if (neg == null) {
            return "redirect:/papPati/portal";
        }
        if (!"inProgress".equals(neg.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació ja està tancada");
            return "redirect:/papPati/negociacio/" + negociacioID;
        }

        AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        OviUser oviUser = oviUserDao.getOviUser(sol.getOviID());

        model.addAttribute("titol", "Confirmar finalització sense acord");
        model.addAttribute("missatge",
                "Estàs a punt de donar per finalitzada la negociació amb " + oviUser.getNameAndSurname() + " sense acord.");
        model.addAttribute("detall",
                "La negociació quedarà marcada com a tancada sense acord. Aquesta acció no es pot desfer.");
        model.addAttribute("actionUrl", "/papPati/negociacio/" + negociacioID + "/noAcord");
        model.addAttribute("cancelUrl", "/papPati/negociacio/" + negociacioID);
        model.addAttribute("confirmLabel", "Sí, finalitzar sense acord");
        model.addAttribute("tipusAccio", "perillosa");
        return "fragments/confirm";
    }

    @RequestMapping(value = "/oviUser/negociacio/{negociacioID}/contracte/add", method = RequestMethod.GET)
    public String showAddContract(@PathVariable int negociacioID, HttpSession session, Model model) {

        Credentials credentials = (Credentials) session.getAttribute(USER_ATTR);
        if (credentials == null) {
            return "redirect:/login";
        }

        Negotiation neg = getOwnedNegotiationOvi(negociacioID, session);
        if (neg == null) {
            return "redirect:/oviUser/solicitudes";
        }

        if (!"finished".equals(neg.getStatus())) {
            return "redirect:/oviUser/negociacio/" + negociacioID;
        }

        AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        if (sol == null) {
            return "redirect:/oviUser/solicitudes";
        }

        OviUser oviUser = oviUserDao.getOviUser(sol.getOviID());
        PapPati papPati = papPatiDao.getPapPati(neg.getPapID());

        List<RequestSchedule> horaris =
                requestScheduleDao.getRequestSchedulesByRequest(neg.getRequestID());

        model.addAttribute("neg", neg);
        model.addAttribute("sol", sol);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("papPati", papPati);
        model.addAttribute("horaris", horaris);
        model.addAttribute("contract", new Contract());

        return "oviUser/contractes/add";
    }

    @Transactional
    @RequestMapping(
            value = "/oviUser/negociacio/{negociacioID}/contracte/add",
            method = RequestMethod.POST)
    public String addContractOvi(@PathVariable int negociacioID,
                                 @ModelAttribute("contract") Contract contract,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        Negotiation neg = getOwnedNegotiationOvi(negociacioID, session);

        if (neg == null) {
            return "redirect:/oviUser/solicitudes";
        }

        if (!"finished".equals(neg.getStatus())) {
            return "redirect:/oviUser/negociacio/" + negociacioID;
        }

        ContractValidator validator = new ContractValidator();
        validator.validate(contract, bindingResult);

        if (bindingResult.hasErrors()) {
            AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());

            model.addAttribute("neg", neg);
            model.addAttribute("sol", sol);
            model.addAttribute("oviUser", oviUserDao.getOviUser(sol.getOviID()));
            model.addAttribute("papPati", papPatiDao.getPapPati(neg.getPapID()));
            model.addAttribute("horaris",
                    requestScheduleDao.getRequestSchedulesByRequest(neg.getRequestID()));

            return "oviUser/contractes/add";
        }

        // Creación
        contract.setNegotiationID(negociacioID);
        contract.setVersion(1);
        contract.setCreationDate(LocalDate.now());
        contract.setStatus("active");

        contractDao.addContract(contract);

        // cerrar solicitud
        assistanceRequestDao.updateStatus(neg.getRequestID(), "closedWithContract");

        redirectAttributes.addFlashAttribute("successMessage", "Contracte creat correctament");

        return "redirect:/oviUser/negociacio/" + negociacioID;
    }
}