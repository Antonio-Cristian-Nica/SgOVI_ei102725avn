package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.assistancerequest.AssistanceRequestDao;
import es.uji.ei1027.ovi.dao.contract.ContractDao;
import es.uji.ei1027.ovi.dao.negotiation.NegotiationDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/contractes")
public class AdminContractController {

    // Controlador per a gestionar els contractes des de l'administració
    private ContractDao contractDao;
    private NegotiationDao negotiationDao;
    private AssistanceRequestDao assistanceRequestDao;
    private OviUserDao oviUserDao;
    private PapPatiDao papPatiDao;

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao) {
        this.negotiationDao = negotiationDao;
    }

    @Autowired
    public void setAssistanceRequestDao(AssistanceRequestDao assistanceRequestDao) {
        this.assistanceRequestDao = assistanceRequestDao;
    }

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    // Mostra les negociacions finalitzades i els seus contractes
    @RequestMapping
    public String list(Model model) {
        List<Negotiation> negociacions = negotiationDao.getNegotiationsFinished();

        Map<Integer, Contract> contractes = new HashMap<>();
        Map<Integer, OviUser> oviUsers = new HashMap<>();
        Map<Integer, PapPati> papPatis = new HashMap<>();

        for (Negotiation neg : negociacions) {
            Contract contract = contractDao.getContractByNegotiation(neg.getNegotiationID());
            if (contract != null) {
                contractes.put(neg.getNegotiationID(), contract);
            }
            AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
            oviUsers.put(neg.getNegotiationID(), oviUserDao.getOviUser(sol.getOviID()));
            papPatis.put(neg.getNegotiationID(), papPatiDao.getPapPati(neg.getPapID()));
        }

        model.addAttribute("negociacions", negociacions);
        model.addAttribute("contractes", contractes);
        model.addAttribute("oviUsers", oviUsers);
        model.addAttribute("papPatis", papPatis);
        return "admin/contractes/list";
    }

    // Carrega el formulari per a afegir un contracte
    @RequestMapping("/add/{negociacioID}")
    public String add(@PathVariable int negociacioID, Model model) {
        Negotiation neg = negotiationDao.getNegotiation(negociacioID);
        AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        OviUser oviUser = oviUserDao.getOviUser(sol.getOviID());
        PapPati papPati = papPatiDao.getPapPati(neg.getPapID());

        model.addAttribute("neg", neg);
        model.addAttribute("sol", sol);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("papPati", papPati);
        model.addAttribute("contract", new Contract());
        return "admin/contractes/add";
    }

    // Valida i guarda un nou contracte
    @RequestMapping(value = "/add/{negociacioID}", method = RequestMethod.POST)
    public String processAdd(@PathVariable int negociacioID,
                             @ModelAttribute("contract") Contract contract,
                             Model model) {

        boolean hasErrors = false;

        if (contract.getStartServiceDate() == null) {
            model.addAttribute("errorStart", "La data d'inici és obligatòria");
            hasErrors = true;
        }
        if (contract.getEndServiceDate() == null) {
            model.addAttribute("errorEnd", "La data de fi és obligatòria");
            hasErrors = true;
        }
        if (contract.getStartServiceDate() != null && contract.getEndServiceDate() != null
                && contract.getEndServiceDate().isBefore(contract.getStartServiceDate())) {
            model.addAttribute("errorEnd", "La data de fi ha de ser posterior a la d'inici");
            hasErrors = true;
        }
        if (contract.getDocumentURL() == null || contract.getDocumentURL().trim().isEmpty()) {
            model.addAttribute("errorURL", "L'URL del document és obligatòria");
            hasErrors = true;
        }

        if (hasErrors) {
            Negotiation neg = negotiationDao.getNegotiation(negociacioID);
            AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
            model.addAttribute("neg", neg);
            model.addAttribute("sol", sol);
            model.addAttribute("oviUser", oviUserDao.getOviUser(sol.getOviID()));
            model.addAttribute("papPati", papPatiDao.getPapPati(neg.getPapID()));
            return "admin/contractes/add";
        }

        contract.setNegotiationID(negociacioID);
        contract.setVersion(1);
        contract.setCreationDate(LocalDate.now());
        contract.setStatus("active");
        contractDao.addContract(contract);

        Negotiation neg = negotiationDao.getNegotiation(negociacioID);
        AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        sol.setStatus("closedWithContract");
        assistanceRequestDao.updateAssistanceRequest(sol);

        return "redirect:/admin/contractes";
    }

    // Mostra el detall d'un contracte
    @RequestMapping("/{contractID}")
    public String detail(@PathVariable int contractID, Model model) {
        Contract contract = contractDao.getContract(contractID);
        Negotiation neg = negotiationDao.getNegotiation(contract.getNegotiationID());
        AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        OviUser oviUser = oviUserDao.getOviUser(sol.getOviID());
        PapPati papPati = papPatiDao.getPapPati(neg.getPapID());

        model.addAttribute("contract", contract);
        model.addAttribute("neg", neg);
        model.addAttribute("sol", sol);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("papPati", papPati);
        return "admin/contractes/detail";
    }
}