package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.assistancerequest.AssistanceRequestDao;
import es.uji.ei1027.ovi.dao.contract.ContractDao;
import es.uji.ei1027.ovi.dao.negotiation.NegotiationDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.dao.recommendedpappati.RecommendedPapPatiDao;
import es.uji.ei1027.ovi.dao.requestschedule.RequestScheduleDao;
import es.uji.ei1027.ovi.model.*;
import es.uji.ei1027.ovi.validator.RequestScheduleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/oviUser/solicitudes")
public class OviUserSolicitudController {

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

    // LLISTAT DE SOL·LICITUDS
    @RequestMapping
    public String list(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute("user");
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        List<AssistanceRequest> solicitudes = assistanceRequestDao.getAssistanceRequestsByUser(oviUser.getOviID());
        model.addAttribute("solicitudes", solicitudes);
        return "oviuser/solicitudes/list";
    }

    // FORMULARI NOVA SOL·LICITUD
    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("solicitud", new AssistanceRequest());
        return "oviuser/solicitudes/add";
    }

    // GUARDAR NOVA SOL·LICITUD
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAdd(@ModelAttribute("solicitud") AssistanceRequest solicitud,
                             HttpSession session, Model model) {

        if (solicitud.getServiceLocation() == null || solicitud.getServiceLocation().trim().isEmpty()) {
            model.addAttribute("errorLocation", "La localització és obligatòria");
            return "oviuser/solicitudes/add";
        }
        if (solicitud.getRequiredAssistance() == null || solicitud.getRequiredAssistance().trim().isEmpty()) {
            model.addAttribute("errorAssistance", "La descripció és obligatòria");
            return "oviuser/solicitudes/add";
        }

        Credentials credentials = (Credentials) session.getAttribute("user");
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        solicitud.setOviID(oviUser.getOviID());
        solicitud.setStatus("inProgress");
        assistanceRequestDao.addAssistanceRequest(solicitud);

        int requestID = assistanceRequestDao.getLastInsertedId();
        return "redirect:/oviUser/solicitudes/" + requestID + "/horaris?nova=true";
    }

    // AFEGIR HORARIS A LA SOL·LICITUD
    @RequestMapping("/{requestID}/horaris")
    public String horaris(@PathVariable int requestID,
                          @RequestParam(value = "nova", defaultValue = "false") boolean nova,
                          Model model) {
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(requestID);
        model.addAttribute("horaris", horaris);
        model.addAttribute("requestID", requestID);
        model.addAttribute("newHorari", new RequestSchedule());
        model.addAttribute("esNova", nova);
        return "oviuser/solicitudes/horaris";
    }

    // GUARDAR HORARI
    @RequestMapping(value = "/{requestID}/horaris/add", method = RequestMethod.POST)
    public String addHorari(@PathVariable int requestID,
                            @ModelAttribute("newHorari") RequestSchedule horari,
                            BindingResult bindingResult,
                            Model model) {

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

    // ELIMINAR HORARI
    @RequestMapping(value = "/{requestID}/horaris/delete/{horariID}", method = RequestMethod.POST)
    public String deleteHorari(@PathVariable int requestID,
                               @PathVariable int horariID,
                               @RequestParam(value = "nova", defaultValue = "false") boolean nova) {
        requestScheduleDao.deleteRequestSchedule(horariID);
        return "redirect:/oviUser/solicitudes/" + requestID + "/horaris?nova=" + nova;
    }

    // DETALL SOL·LICITUD
    @RequestMapping("/{requestID}")
    public String detail(@PathVariable int requestID, Model model) {
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(requestID);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("horaris", horaris);

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

    // ELIMINAR SOL·LICITUD
    @RequestMapping("/delete/{requestID}")
    public String delete(@PathVariable int requestID) {
        requestScheduleDao.deleteSchedulesByRequest(requestID);
        assistanceRequestDao.deleteAssistanceRequest(requestID);
        return "redirect:/oviUser/solicitudes";
    }

    // FORMULARI EDITAR SOL·LICITUD
    @RequestMapping("/edit/{requestID}")
    public String edit(@PathVariable int requestID, Model model) {
        AssistanceRequest solicitud = assistanceRequestDao.getAssistanceRequest(requestID);
        model.addAttribute("solicitud", solicitud);
        return "oviuser/solicitudes/edit";
    }

    // GUARDAR EDICIÓ SOL·LICITUD
    @RequestMapping(value = "/edit/{requestID}", method = RequestMethod.POST)
    public String processEdit(@PathVariable int requestID,
                              @ModelAttribute("solicitud") AssistanceRequest solicitud,
                              Model model) {

        if (solicitud.getServiceLocation() == null || solicitud.getServiceLocation().trim().isEmpty()) {
            model.addAttribute("errorLocation", "La localització és obligatòria");
            return "oviuser/solicitudes/edit";
        }
        if (solicitud.getRequiredAssistance() == null || solicitud.getRequiredAssistance().trim().isEmpty()) {
            model.addAttribute("errorAssistance", "La descripció és obligatòria");
            return "oviuser/solicitudes/edit";
        }

        solicitud.setRequestID(requestID);
        assistanceRequestDao.updateAssistanceRequest(solicitud);
        return "redirect:/oviUser/solicitudes/" + requestID;
    }

    @RequestMapping(value = "/{requestID}/negociar/{papID}", method = RequestMethod.POST)
    public String iniciarNegociacio(@PathVariable int requestID,
                                    @PathVariable int papID) {
        Negotiation neg = negotiationDao.getNegotiationByRequestAndPap(requestID, papID);
        if (neg == null) {
            Negotiation nova = new Negotiation();
            nova.setRequestID(requestID);
            nova.setPapID(papID);
            nova.setStatus("inProgress");
            nova.setDateAndTime(java.time.LocalDateTime.now());
            nova.setConversation("");
            nova.setOviUserConfirmed(false);
            nova.setPapPatiConfirmed(false);
            negotiationDao.addNegotiation(nova);
        }
        int negID = negotiationDao.getNegotiationByRequestAndPap(requestID, papID).getNegotiationID();
        return "redirect:/oviUser/negociacio/" + negID;
    }
}
