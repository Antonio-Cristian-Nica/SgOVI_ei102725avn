package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.assistancerequest.AssistanceRequestDao;
import es.uji.ei1027.ovi.dao.contract.ContractDao;
import es.uji.ei1027.ovi.dao.negotiation.NegotiationDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.dao.pappatischedule.ScheduleDao;
import es.uji.ei1027.ovi.dao.recommendedpappati.RecommendedPapPatiDao;
import es.uji.ei1027.ovi.dao.requestschedule.RequestScheduleDao;
import es.uji.ei1027.ovi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/solicitudes")
public class AdminSolicitudController {

    private static final String REDIRECT_LIST = "redirect:/admin/solicitudes";

    private AssistanceRequestDao assistanceRequestDao;
    private RequestScheduleDao requestScheduleDao;
    private RecommendedPapPatiDao recommendedPapPatiDao;
    private PapPatiDao papPatiDao;
    private OviUserDao oviUserDao;
    private ScheduleDao scheduleDao;
    private ContractDao contractDao;
    private NegotiationDao negotiationDao;

    @Autowired
    public void setAssistanceRequestDao(AssistanceRequestDao assistanceRequestDao) {
        this.assistanceRequestDao = assistanceRequestDao;
    }

    @Autowired
    public void setRequestScheduleDao(RequestScheduleDao requestScheduleDao) {
        this.requestScheduleDao = requestScheduleDao;
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
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setScheduleDao(ScheduleDao scheduleDao) {
        this.scheduleDao = scheduleDao;
    }

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao) {
        this.negotiationDao = negotiationDao;
    }

    // =====================================================================
    // LLISTAT
    // =====================================================================

    @RequestMapping
    public String list(Model model) {
        model.addAttribute("solicitudes", assistanceRequestDao.getAssistanceRequests());
        return "admin/solicitudes/list";
    }

    // =====================================================================
    // DETALL
    // =====================================================================

    @RequestMapping("/{requestID}")
    public String detail(@PathVariable int requestID, Model model) {
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }
        return carregarDetail(requestID, model, null);
    }

    /**
     * Carrega tota la informació necessària per a la vista de detall.
     * Si errorPapID no és null, l'afegeix com a missatge d'error per al formulari de recomanació.
     */
    private String carregarDetail(int requestID, Model model, String errorPapID) {
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(requestID);
        List<RecommendedPapPati> recomanats = recommendedPapPatiDao.getRecommendedByRequest(requestID);

        List<Integer> papIDsRecomanats = recomanats.stream()
                .map(RecommendedPapPati::getPapID)
                .collect(Collectors.toList());

        // Rígides: filtratge automàtic per disponibilitat i contractes solapats.
        // Flexibles: es mostren tots els PAP/PATIs actius, ja que els horaris
        // concrets s'acorden en negociació i no es poden creuar amb disponibilitat.
        List<PapPati> totsPapPatis;
        if ("flexible".equals(solicitud.getType())) {
            totsPapPatis = papPatiDao.getActivePapPatis().stream()
                    .filter(p -> !papIDsRecomanats.contains(p.getPapID()))
                    .collect(Collectors.toList());
        } else {
            List<Integer> papIDsCompatibles = scheduleDao.getPapPatiIDsCompatibles(requestID);
            papIDsCompatibles.removeAll(papIDsRecomanats);
            totsPapPatis = papIDsCompatibles.isEmpty()
                    ? new ArrayList<>()
                    : papPatiDao.getPapPatisByIDs(papIDsCompatibles);
        }

        OviUser oviUser = oviUserDao.getOviUser(solicitud.getOviID());

        Map<Integer, List<Schedule>> horarisPapPati = new HashMap<>();
        for (PapPati pap : totsPapPatis) {
            horarisPapPati.put(pap.getPapID(), scheduleDao.getSchedulesByPap(pap.getPapID()));
        }

        Map<Integer, Boolean> teNegociacio = new HashMap<>();
        Map<Integer, PapPati> infoPapPatiRecomanats = new HashMap<>();
        Map<Integer, Contract> contractesPerPap = new HashMap<>();

        for (RecommendedPapPati rec : recomanats) {
            teNegociacio.put(rec.getPapID(),
                    recommendedPapPatiDao.hasNegotiation(requestID, rec.getPapID()));
            infoPapPatiRecomanats.put(rec.getPapID(), papPatiDao.getPapPati(rec.getPapID()));

            Negotiation neg = negotiationDao.getNegotiationByRequestAndPap(requestID, rec.getPapID());
            if (neg != null) {
                Contract contract = contractDao.getContractByNegotiation(neg.getNegotiationID());
                if (contract != null) {
                    contractesPerPap.put(rec.getPapID(), contract);
                }
            }
        }

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("horaris", horaris);
        model.addAttribute("recomanats", recomanats);
        model.addAttribute("totsPapPatis", totsPapPatis);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("teNegociacio", teNegociacio);
        model.addAttribute("horarisPapPati", horarisPapPati);
        model.addAttribute("infoPapPatiRecomanats", infoPapPatiRecomanats);
        model.addAttribute("contractesPerPap", contractesPerPap);
        if (errorPapID != null) {
            model.addAttribute("errorPapID", errorPapID);
        }
        return "admin/solicitudes/detail";
    }

    // =====================================================================
    // ACCEPTAR / REBUTJAR
    // =====================================================================

    @RequestMapping(value = "/{requestID}/acceptar", method = RequestMethod.POST)
    public String acceptar(@PathVariable int requestID,
                           RedirectAttributes redirectAttributes) {
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }

        // Només permetre acceptar des de 'inProgress'
        if (!"inProgress".equals(solicitud.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta sol·licitud ja no està pendent de revisió");
            return "redirect:/admin/solicitudes/" + requestID;
        }

        // Comprovar que té al menys una recomanació
        List<RecommendedPapPati> recomanats = recommendedPapPatiDao.getRecommendedByRequest(requestID);
        if (recomanats.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Has d'afegir almenys un PAP/PATI recomanat abans d'acceptar la sol·licitud");
            return "redirect:/admin/solicitudes/" + requestID;
        }

        assistanceRequestDao.updateStatus(requestID, "accepted");

        redirectAttributes.addFlashAttribute("successMessage",
                "La sol·licitud s'ha acceptat correctament");
        return "redirect:/admin/solicitudes/" + requestID;
    }

    @RequestMapping(value = "/{requestID}/rebutjar", method = RequestMethod.POST)
    public String rebutjar(@PathVariable int requestID,
                           RedirectAttributes redirectAttributes) {
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }

        if (!"inProgress".equals(solicitud.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta sol·licitud ja no està pendent de revisió");
            return "redirect:/admin/solicitudes/" + requestID;
        }

        assistanceRequestDao.updateStatus(requestID, "rejected");

        redirectAttributes.addFlashAttribute("successMessage",
                "La sol·licitud s'ha rebutjat correctament");
        return "redirect:/admin/solicitudes/" + requestID;
    }

    // =====================================================================
    // RECOMANAR PAP/PATI
    // =====================================================================

    @RequestMapping(value = "/{requestID}/recomanar", method = RequestMethod.POST)
    public String recomanar(@PathVariable int requestID,
                            @RequestParam(value = "papID", required = false) Integer papID,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }

        if (papID == null) {
            return carregarDetail(requestID, model, "Has de seleccionar un PAP/PATI");
        }

        // Només es poden recomanar PAP/PATIs mentre la sol·licitud està en revisió
        if (!"inProgress".equals(solicitud.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Només es poden recomanar PAP/PATIs en sol·licituds en revisió");
            return "redirect:/admin/solicitudes/" + requestID;
        }

        // Comprovar que el PAP/PATI existeix i està actiu
        PapPati pap = papPatiDao.getPapPati(papID);
        if (pap == null || !"active".equals(pap.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "El PAP/PATI seleccionat no és vàlid");
            return "redirect:/admin/solicitudes/" + requestID;
        }

        // Comprovar que no està ja recomanat
        if (recommendedPapPatiDao.getRecommendedPapPati(requestID, papID) != null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest PAP/PATI ja està recomanat per a aquesta sol·licitud");
            return "redirect:/admin/solicitudes/" + requestID;
        }

        RecommendedPapPati rec = new RecommendedPapPati();
        rec.setRequestID(requestID);
        rec.setPapID(papID);
        recommendedPapPatiDao.addRecommendedPapPati(rec);

        redirectAttributes.addFlashAttribute("successMessage",
                "El PAP/PATI s'ha recomanat correctament");
        return "redirect:/admin/solicitudes/" + requestID;
    }

    @RequestMapping(value = "/{requestID}/recomanar/delete/{papID}", method = RequestMethod.POST)
    public String deleteRecomanat(@PathVariable int requestID,
                                  @PathVariable int papID,
                                  RedirectAttributes redirectAttributes) {

        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        if (solicitud == null) {
            return REDIRECT_LIST;
        }

        // Només es poden eliminar recomanacions si la sol·licitud encara està en revisió
        if (!"inProgress".equals(solicitud.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No es poden eliminar recomanacions una vegada acceptada la sol·licitud");
            return "redirect:/admin/solicitudes/" + requestID;
        }

        // Si ja té negociació, no es pot eliminar (defensa addicional)
        if (recommendedPapPatiDao.hasNegotiation(requestID, papID)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No es pot eliminar la recomanació perquè ja té una negociació iniciada");
            return "redirect:/admin/solicitudes/" + requestID;
        }

        recommendedPapPatiDao.deleteRecommendedPapPati(requestID, papID);

        redirectAttributes.addFlashAttribute("successMessage",
                "La recomanació s'ha eliminat correctament");
        return "redirect:/admin/solicitudes/" + requestID;
    }
}