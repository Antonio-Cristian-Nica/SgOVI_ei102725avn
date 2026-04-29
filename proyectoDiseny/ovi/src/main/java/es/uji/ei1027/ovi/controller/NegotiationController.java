package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.assistancerequest.AssistanceRequestDao;
import es.uji.ei1027.ovi.dao.negotiation.NegotiationDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.dao.requestschedule.RequestScheduleDao;
import es.uji.ei1027.ovi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class NegotiationController {

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

    // Mostra el detall d'una negociació per a l'usuari OVI
    @RequestMapping("/oviUser/negociacio/{negociacioID}")
    public String detallOviUser(@PathVariable int negociacioID,
                                HttpSession session, Model model) {
        Negotiation neg = negotiationDao.getNegotiation(negociacioID);
        Credentials credentials = (Credentials) session.getAttribute("user");
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        PapPati papPati = papPatiDao.getPapPati(neg.getPapID());
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(neg.getRequestID());

        model.addAttribute("neg", neg);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("papPati", papPati);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("horaris", horaris);
        model.addAttribute("rol", "oviUser");
        return "negociacio/detail";
    }

    // Mostra el detall d'una negociació per al PAP/PATI
    @RequestMapping("/papPati/negociacio/{negociacioID}")
    public String detallPapPati(@PathVariable int negociacioID,
                                HttpSession session, Model model) {
        Negotiation neg = negotiationDao.getNegotiation(negociacioID);
        Credentials credentials = (Credentials) session.getAttribute("user");
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        OviUser oviUser = oviUserDao.getOviUser(solicitud.getOviID());
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(neg.getRequestID());

        model.addAttribute("neg", neg);
        model.addAttribute("papPati", papPati);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("horaris", horaris);
        model.addAttribute("rol", "papPati");
        return "negociacio/detail";
    }

    // Afig un missatge nou a la conversa de la negociació
    @RequestMapping(value = "/negociacio/{negociacioID}/missatge", method = RequestMethod.POST)
    public String afegirMissatge(@PathVariable int negociacioID,
                                 @RequestParam("missatge") String missatge,
                                 @RequestParam("rol") String rol,
                                 HttpSession session) {
        Negotiation neg = negotiationDao.getNegotiation(negociacioID);
        Credentials credentials = (Credentials) session.getAttribute("user");

        String prefix = rol.equals("oviUser") ?
                "[" + credentials.getUsername() + "] " :
                "[" + credentials.getUsername() + " - PAP/PATI] ";

        String conversacioActual = neg.getConversation() == null ? "" : neg.getConversation();
        String novaConversacio = conversacioActual.isEmpty() ?
                prefix + missatge :
                conversacioActual + "\n" + prefix + missatge;

        neg.setConversation(novaConversacio);
        negotiationDao.updateNegotiation(neg);

        return "redirect:/" + rol + "/negociacio/" + negociacioID;
    }

    // Confirma l'acord des de la part de l'usuari OVI
    @RequestMapping(value = "/oviUser/negociacio/{negociacioID}/confirmar", method = RequestMethod.POST)
    public String confirmarOviUser(@PathVariable int negociacioID) {
        Negotiation neg = negotiationDao.getNegotiation(negociacioID);
        neg.setOviUserConfirmed(true);
        if (neg.isPapPatiConfirmed()) {
            neg.setStatus("finished");
            tancarAltresNegociacions(neg.getRequestID(), negociacioID);
        }
        negotiationDao.updateNegotiation(neg);
        return "redirect:/oviUser/negociacio/" + negociacioID;
    }

    // Confirma l'acord des de la part del PAP/PATI
    @RequestMapping(value = "/papPati/negociacio/{negociacioID}/confirmar", method = RequestMethod.POST)
    public String confirmarPapPati(@PathVariable int negociacioID) {
        Negotiation neg = negotiationDao.getNegotiation(negociacioID);
        neg.setPapPatiConfirmed(true);
        if (neg.isOviUserConfirmed()) {
            neg.setStatus("finished");
            tancarAltresNegociacions(neg.getRequestID(), negociacioID);
        }
        negotiationDao.updateNegotiation(neg);
        return "redirect:/papPati/negociacio/" + negociacioID;
    }

    // Tanca la resta de negociacions actives de la mateixa sol·licitud
    private void tancarAltresNegociacions(int requestID, int negociacioID) {
        List<Negotiation> altres = negotiationDao.getNegotiationsByRequest(requestID);
        for (Negotiation altra : altres) {
            if (altra.getNegotiationID() != negociacioID
                    && altra.getStatus().equals("inProgress")) {
                altra.setStatus("noAgreement");
                negotiationDao.updateNegotiation(altra);
            }
        }
    }

    // Marca la negociació com a no acord des de la part de l'usuari OVI
    @RequestMapping(value = "/oviUser/negociacio/{negociacioID}/noAcord", method = RequestMethod.POST)
    public String noAcordOviUser(@PathVariable int negociacioID) {
        Negotiation neg = negotiationDao.getNegotiation(negociacioID);
        neg.setStatus("noAgreement");
        negotiationDao.updateNegotiation(neg);
        return "redirect:/oviUser/negociacio/" + negociacioID;
    }

    // Marca la negociació com a no acord des de la part del PAP/PATI
    @RequestMapping(value = "/papPati/negociacio/{negociacioID}/noAcord", method = RequestMethod.POST)
    public String noAcordPapPati(@PathVariable int negociacioID) {
        Negotiation neg = negotiationDao.getNegotiation(negociacioID);
        neg.setStatus("noAgreement");
        negotiationDao.updateNegotiation(neg);
        return "redirect:/papPati/negociacio/" + negociacioID;
    }
}