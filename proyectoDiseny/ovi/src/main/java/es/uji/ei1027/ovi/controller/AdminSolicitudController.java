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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/solicitudes")
public class AdminSolicitudController {

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

    // LLISTAT DE TOTES LES SOL·LICITUDS
    @RequestMapping
    public String list(Model model) {
        model.addAttribute("solicitudes", assistanceRequestDao.getAssistanceRequests());
        return "admin/solicitudes/list";
    }

    // DETALL SOL·LICITUD
    @RequestMapping("/{requestID}")
    public String detail(@PathVariable int requestID, Model model) {
        return carregarDetail(requestID, model, null);
    }

    // AFEGIR PAP/PATI RECOMANAT
    @RequestMapping(value = "/{requestID}/recomanar", method = RequestMethod.POST)
    public String recomanar(@PathVariable int requestID,
                            @RequestParam("papID") String papIDStr,
                            Model model) {
        if (papIDStr == null || papIDStr.isEmpty()) {
            return carregarDetail(requestID, model, "Has de seleccionar un PAP/PATI");
        }
        int papID = Integer.parseInt(papIDStr);
        RecommendedPapPati rec = new RecommendedPapPati();
        rec.setRequestID(requestID);
        rec.setPapID(papID);
        recommendedPapPatiDao.addRecommendedPapPati(rec);
        return "redirect:/admin/solicitudes/" + requestID;
    }

    private String carregarDetail(int requestID, Model model, String errorPapID) {
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(requestID);
        List<RecommendedPapPati> recomanats = recommendedPapPatiDao.getRecommendedByRequest(requestID);

        // Obtenim els IDs compatibles i excloem els ja recomanats
        List<Integer> papIDsCompatibles = scheduleDao.getPapPatiIDsCompatibles(requestID);
        List<Integer> papIDsRecomanats = recomanats.stream()
                .map(RecommendedPapPati::getPapID)
                .collect(java.util.stream.Collectors.toList());
        papIDsCompatibles.removeAll(papIDsRecomanats);

        List<PapPati> totsPapPatis = papIDsCompatibles.isEmpty()
                ? new java.util.ArrayList<>()
                : papPatiDao.getPapPatisByIDs(papIDsCompatibles);

        OviUser oviUser = oviUserDao.getOviUser(solicitud.getOviID());

        Map<Integer, List<Schedule>> horarisPapPati = new HashMap<>();
        for (PapPati pap : totsPapPatis) {
            horarisPapPati.put(pap.getPapID(), scheduleDao.getSchedulesByPap(pap.getPapID()));
        }

        Map<Integer, Boolean> teNegociacio = new HashMap<>();
        for (RecommendedPapPati rec : recomanats) {
            teNegociacio.put(rec.getPapID(),
                    recommendedPapPatiDao.hasNegotiation(requestID, rec.getPapID()));
        }

        Map<Integer, PapPati> infoPapPatiRecomanats = new HashMap<>();
        for (RecommendedPapPati rec : recomanats) {
            PapPati pap = papPatiDao.getPapPati(rec.getPapID());
            infoPapPatiRecomanats.put(rec.getPapID(), pap);
        }

        Map<Integer, Contract> contractesPerPap = new HashMap<>();
        for (RecommendedPapPati rec : recomanats) {
            Negotiation neg = negotiationDao.getNegotiationByRequestAndPap(requestID, rec.getPapID());
            if (neg != null) {
                Contract contract = contractDao.getContractByNegotiation(neg.getNegotiationID());
                if (contract != null) {
                    contractesPerPap.put(rec.getPapID(), contract);
                }
            }
        }

        model.addAttribute("contractesPerPap", contractesPerPap);
        model.addAttribute("infoPapPatiRecomanats", infoPapPatiRecomanats);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("horaris", horaris);
        model.addAttribute("recomanats", recomanats);
        model.addAttribute("totsPapPatis", totsPapPatis);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("teNegociacio", teNegociacio);
        model.addAttribute("horarisPapPati", horarisPapPati);
        if (errorPapID != null) {
            model.addAttribute("errorPapID", errorPapID);
        }
        return "admin/solicitudes/detail";
    }

    @RequestMapping(value = "/{requestID}/acceptar", method = RequestMethod.POST)
    public String acceptar(@PathVariable int requestID) {
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        solicitud.setStatus("accepted");
        assistanceRequestDao.updateAssistanceRequest(solicitud);
        return "redirect:/admin/solicitudes/" + requestID;
    }

    @RequestMapping(value = "/{requestID}/rebutjar", method = RequestMethod.POST)
    public String rebutjar(@PathVariable int requestID) {
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        solicitud.setStatus("rejected");
        assistanceRequestDao.updateAssistanceRequest(solicitud);
        return "redirect:/admin/solicitudes/" + requestID;
    }

    // ELIMINAR PAP/PATI RECOMANAT
    @RequestMapping(value = "/{requestID}/recomanar/delete/{papID}", method = RequestMethod.POST)
    public String deleteRecomanat(@PathVariable int requestID,
                                  @PathVariable int papID) {
        recommendedPapPatiDao.deleteRecommendedPapPati(requestID, papID);
        return "redirect:/admin/solicitudes/" + requestID;
    }
}
