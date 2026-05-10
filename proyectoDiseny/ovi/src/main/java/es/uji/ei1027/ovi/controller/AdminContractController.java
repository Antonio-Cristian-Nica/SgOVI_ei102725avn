package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.assistancerequest.AssistanceRequestDao;
import es.uji.ei1027.ovi.dao.contract.ContractDao;
import es.uji.ei1027.ovi.dao.negotiation.NegotiationDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.dao.requestschedule.RequestScheduleDao;
import es.uji.ei1027.ovi.model.*;
import es.uji.ei1027.ovi.validator.ContractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/contractes")
public class AdminContractController {

    private static final String REDIRECT_LIST = "redirect:/admin/contractes";

    private ContractDao contractDao;
    private NegotiationDao negotiationDao;
    private AssistanceRequestDao assistanceRequestDao;
    private OviUserDao oviUserDao;
    private PapPatiDao papPatiDao;
    private RequestScheduleDao requestScheduleDao;

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

    @Autowired
    public void setRequestScheduleDao(RequestScheduleDao requestScheduleDao) {this.requestScheduleDao = requestScheduleDao;}

    // =====================================================================
    // LLISTAT GENERAL DE CONTRACTES
    // =====================================================================

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

    // =====================================================================
    // LLISTATS FILTRATS PER USUARI
    // =====================================================================

    /*
     * Llistat de contractes d'un OviUser concret. Accés des del detall de l'usuari.
     */
    @RequestMapping("/oviUser/{oviID}")
    public String listByOviUser(@PathVariable int oviID, Model model,
                                RedirectAttributes redirectAttributes) {
        OviUser oviUser = oviUserDao.getOviUser(oviID);
        if (oviUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest usuari no existeix");
            return "redirect:/admin/gestionarOviUsers";
        }

        List<Contract> contractes = contractDao.getContractsByOviUser(oviID);

        // Cargar PAP/PATIs per a cada contracte (a través de la negociació)
        Map<Integer, PapPati> papPatis = new HashMap<>();
        Map<Integer, Negotiation> negotiations = new HashMap<>();
        for (Contract c : contractes) {
            Negotiation neg = negotiationDao.getNegotiation(c.getNegotiationID());
            negotiations.put(c.getContractID(), neg);
            papPatis.put(c.getContractID(), papPatiDao.getPapPati(neg.getPapID()));
        }

        model.addAttribute("contractes", contractes);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("papPatis", papPatis);
        model.addAttribute("negotiations", negotiations);
        model.addAttribute("titol", "Contractes de " + oviUser.getNameAndSurname());
        model.addAttribute("backUrl", "/admin/gestionarOviUsers/" + oviUser.getUsername());
        return "admin/contractes/listByUser";
    }

    /*
     * Llistat de contractes d'un PAP/PATI concret. Accés des del detall del PAP/PATI.
     */
    @RequestMapping("/papPati/{papID}")
    public String listByPapPati(@PathVariable int papID, Model model,
                                RedirectAttributes redirectAttributes) {
        PapPati papPati = papPatiDao.getPapPati(papID);
        if (papPati == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest PAP/PATI no existeix");
            return "redirect:/admin/gestionarPapPati";
        }

        List<Contract> contractes = contractDao.getContractsByPapPati(papID);

        // Cargar OviUsers per a cada contracte (a través de la negociació i la sol·licitud)
        Map<Integer, OviUser> oviUsers = new HashMap<>();
        Map<Integer, Negotiation> negotiations = new HashMap<>();
        for (Contract c : contractes) {
            Negotiation neg = negotiationDao.getNegotiation(c.getNegotiationID());
            negotiations.put(c.getContractID(), neg);
            AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
            oviUsers.put(c.getContractID(), oviUserDao.getOviUser(sol.getOviID()));
        }

        model.addAttribute("contractes", contractes);
        model.addAttribute("papPati", papPati);
        model.addAttribute("oviUsers", oviUsers);
        model.addAttribute("negotiations", negotiations);
        model.addAttribute("titol", "Contractes de " + papPati.getNameAndSurname());
        model.addAttribute("backUrl", "/admin/gestionarPapPati/" + papPati.getUsername());
        return "admin/contractes/listByUser";
    }

    // =====================================================================
    // ALTA DE CONTRACTE
    // =====================================================================

    @RequestMapping("/add/{negociacioID}")
    public String add(@PathVariable int negociacioID, Model model,
                      RedirectAttributes redirectAttributes) {
        Negotiation neg = negotiationDao.getNegotiation(negociacioID);
        if (neg == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació no existeix");
            return REDIRECT_LIST;
        }

        // Només es pot crear contracte a partir d'una negociació finalitzada
        if (!"finished".equals(neg.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Només es pot crear un contracte a partir d'una negociació finalitzada");
            return REDIRECT_LIST;
        }

        // Comprovar que no existeix ja un contracte per a aquesta negociació
        if (contractDao.getContractByNegotiation(negociacioID) != null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació ja té un contracte associat");
            return REDIRECT_LIST;
        }

        AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        OviUser oviUser = oviUserDao.getOviUser(sol.getOviID());
        PapPati papPati = papPatiDao.getPapPati(neg.getPapID());
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(neg.getRequestID());

        model.addAttribute("horaris", horaris);
        model.addAttribute("neg", neg);
        model.addAttribute("sol", sol);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("papPati", papPati);
        model.addAttribute("contract", new Contract());
        return "admin/contractes/add";
    }

    @Transactional
    @RequestMapping(value = "/add/{negociacioID}", method = RequestMethod.POST)
    public String processAdd(@PathVariable int negociacioID,
                             @ModelAttribute("contract") Contract contract,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        Negotiation neg = negotiationDao.getNegotiation(negociacioID);
        if (neg == null || !"finished".equals(neg.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Negociació no vàlida per a crear contracte");
            return REDIRECT_LIST;
        }

        if (contractDao.getContractByNegotiation(negociacioID) != null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquesta negociació ja té un contracte associat");
            return REDIRECT_LIST;
        }

        ContractValidator validator = new ContractValidator();
        validator.validate(contract, bindingResult);

        if (bindingResult.hasErrors()) {
            AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
            List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(neg.getRequestID());
            model.addAttribute("neg", neg);
            model.addAttribute("sol", sol);
            model.addAttribute("oviUser", oviUserDao.getOviUser(sol.getOviID()));
            model.addAttribute("papPati", papPatiDao.getPapPati(neg.getPapID()));
            model.addAttribute("horaris", horaris);
            return "admin/contractes/add";
        }

        contract.setNegotiationID(negociacioID);
        contract.setVersion(1);
        contract.setCreationDate(LocalDate.now());
        contract.setStatus("active");
        contractDao.addContract(contract);

        // Actualitzar el status de la sol·licitud associada
        assistanceRequestDao.updateStatus(neg.getRequestID(), "closedWithContract");

        redirectAttributes.addFlashAttribute("successMessage",
                "El contracte s'ha creat correctament");
        return REDIRECT_LIST;
    }

    // =====================================================================
    // DETALL DE CONTRACTE
    // =====================================================================

    @RequestMapping("/{contractID}")
    public String detail(@PathVariable int contractID, Model model,
                         RedirectAttributes redirectAttributes) {
        Contract contract = contractDao.getContract(contractID);
        if (contract == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest contracte no existeix");
            return REDIRECT_LIST;
        }

        Negotiation neg = negotiationDao.getNegotiation(contract.getNegotiationID());
        AssistanceRequest sol = assistanceRequestDao.getAssistanceRequest(neg.getRequestID());
        OviUser oviUser = oviUserDao.getOviUser(sol.getOviID());
        PapPati papPati = papPatiDao.getPapPati(neg.getPapID());
        List<RequestSchedule> horaris = requestScheduleDao.getRequestSchedulesByRequest(neg.getRequestID());

        model.addAttribute("contract", contract);
        model.addAttribute("neg", neg);
        model.addAttribute("sol", sol);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("papPati", papPati);
        model.addAttribute("horaris", horaris);
        return "admin/contractes/detail";
    }

    // =====================================================================
    // FINALITZAR CONTRACTE
    // =====================================================================

    /*
     * Marca un contracte com 'ended' i la sol·licitud associada com 'closedContractEnded'.
     */
    @Transactional
    @RequestMapping(value = "/{contractID}/finalitzar", method = RequestMethod.POST)
    public String finalitzar(@PathVariable int contractID,
                             RedirectAttributes redirectAttributes) {
        Contract contract = contractDao.getContract(contractID);
        if (contract == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest contracte no existeix");
            return REDIRECT_LIST;
        }

        if (!"active".equals(contract.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Només es poden finalitzar contractes actius");
            return "redirect:/admin/contractes/" + contractID;
        }

        contract.setStatus("ended");
        contractDao.updateContract(contract);

        // Actualitzar el status de la sol·licitud associada
        Negotiation neg = negotiationDao.getNegotiation(contract.getNegotiationID());
        assistanceRequestDao.updateStatus(neg.getRequestID(), "closedContractEnded");

        redirectAttributes.addFlashAttribute("successMessage",
                "El contracte s'ha marcat com a finalitzat");
        return "redirect:/admin/contractes/" + contractID;
    }

    // =====================================================================
    // CANCEL·LAR CONTRACTE
    // =====================================================================

    /*
     * Marca un contracte com 'cancelled'. Útil quan l'acord no s'arriba a executar.
     */
    @Transactional
    @RequestMapping(value = "/{contractID}/cancellar", method = RequestMethod.POST)
    public String cancellar(@PathVariable int contractID,
                            RedirectAttributes redirectAttributes) {
        Contract contract = contractDao.getContract(contractID);
        if (contract == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Aquest contracte no existeix");
            return REDIRECT_LIST;
        }

        if (!"active".equals(contract.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Només es poden cancel·lar contractes actius");
            return "redirect:/admin/contractes/" + contractID;
        }

        contract.setStatus("cancelled");
        contractDao.updateContract(contract);

        redirectAttributes.addFlashAttribute("successMessage",
                "El contracte s'ha cancel·lat correctament");
        return "redirect:/admin/contractes/" + contractID;
    }
}