package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.assistancerequest.AssistanceRequestDao;
import es.uji.ei1027.ovi.dao.negotiation.NegotiationDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.dao.requestschedule.RequestScheduleDao;
import es.uji.ei1027.ovi.model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                    "Has confirmat l'acord. Esperant la confirmació del PAP/PATI.");
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
}