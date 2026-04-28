package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.contract.ContractDao;
import es.uji.ei1027.ovi.dao.credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.negotiation.NegotiationDao;
import es.uji.ei1027.ovi.dao.oviuser.OviUserDao;
import es.uji.ei1027.ovi.dao.oviusertutor.TutorDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.model.*;
import es.uji.ei1027.ovi.utils.PasswordUtils;
import es.uji.ei1027.ovi.validator.ChangePasswordValidator;
import es.uji.ei1027.ovi.validator.OviUserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/oviUser")
public class OviUserController {
    private static final String CANVI_CONTRASENYA_VIEW = "oviuser/canviarContrasenya";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private OviUserDao oviUserDao;
    private CredentialsDao credentialsDao;
    private TutorDao tutorDao;
    private ContractDao contractDao;
    private NegotiationDao negotiationDao;
    private PapPatiDao papPatiDao;

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setCredentialsDao(CredentialsDao credentialsDao) {
        this.credentialsDao = credentialsDao;
    }

    @Autowired
    public void setTutorDao(TutorDao tutorDao) {
        this.tutorDao = tutorDao;
    }

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    @Autowired
    public void setNegotiationDao(NegotiationDao negotiationDao) {
        this.negotiationDao = negotiationDao;
    }

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @RequestMapping("/register")
    public String addOviUser(Model model) {
        model.addAttribute("oviuser", new OviUserRegistration());
        return "oviuser/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("oviuser") OviUserRegistration registration,
                                   BindingResult bindingResult) {

        OviUserValidator validator = new OviUserValidator();
        validator.validate(registration, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviuser/register";
        }

        registration.setStatus("approvalPending");

        // 1. Primero las credenciales
        Credentials credentials = new Credentials();
        credentials.setUsername(registration.getUsername());
        credentials.setPassword(PasswordUtils.encrypt(registration.getPassword()));
        credentials.setRole("user_ovi");
        credentials.setActivated(false);
        credentials.setId(0);
        credentialsDao.addCredentials(credentials);

        // 2. Luego el OviUser
        oviUserDao.addOviUser(registration);

        // 3. Actualizamos el ID en credenciales con el oviID real
        int oviID = oviUserDao.getLastInsertedId();
        credentialsDao.updateId(registration.getUsername(), oviID);

        return "redirect:registerSuccess";
    }

    @RequestMapping("/registerSuccess")
    public String registerSuccess() {
        return "registerSuccess";
    }

    private boolean isRejectedOrPending(HttpSession session) {
        Credentials credentials = (Credentials) session.getAttribute("user");
        if (credentials == null) return true;
        return !credentials.getActivated() || credentials.isRejected();
    }

    @RequestMapping("/portal")
    public String portal(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (isRejectedOrPending(session)) {
            return "redirect:/pending";
        }
        return "oviuser/portal";
    }

    @RequestMapping("/edit")
    public String editOviUser(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return REDIRECT_LOGIN;
        }
        Credentials credentials = (Credentials) session.getAttribute("user");
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        model.addAttribute("oviuser", oviUser);
        return "oviuser/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String processEditSubmit(@ModelAttribute("oviuser") OviUser oviUser,
                                    BindingResult bindingResult,
                                    HttpSession session) {
        if (session.getAttribute("user") == null) {
            return REDIRECT_LOGIN;
        }

        OviUserValidator validator = new OviUserValidator();
        validator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviuser/edit";
        }

        oviUserDao.updateOviUser(oviUser);
        return "redirect:portal";
    }

    @RequestMapping("/canviarContrasenya")
    public String canviarContrasenya(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return REDIRECT_LOGIN;
        }
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return CANVI_CONTRASENYA_VIEW;
    }

    @RequestMapping(value = "/canviarContrasenya", method = RequestMethod.POST)
    public String processCanviarContrasenya(@ModelAttribute("changePasswordForm") ChangePasswordForm form,
                                            BindingResult bindingResult,
                                            HttpSession session,
                                            Model model) {
        if (session.getAttribute("user") == null) {
            return REDIRECT_LOGIN;
        }

        ChangePasswordValidator validator = new ChangePasswordValidator();
        validator.validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return CANVI_CONTRASENYA_VIEW;
        }

        Credentials credentials = (Credentials) session.getAttribute("user");
        try {
            if (!PasswordUtils.check(form.getCurrentPassword(), credentials.getPassword())) {
                model.addAttribute("errorActual", "La contrasenya actual no és correcta");
                return CANVI_CONTRASENYA_VIEW;
            }
        } catch (Exception e) {
            if (!credentials.getPassword().equals(form.getCurrentPassword())) {
                model.addAttribute("errorActual", "La contrasenya actual no és correcta");
                return CANVI_CONTRASENYA_VIEW;
            }
        }

        String newEncryptedPassword = PasswordUtils.encrypt(form.getNewPassword());
        credentialsDao.updatePassword(credentials.getUsername(), newEncryptedPassword);

        credentials.setPassword(newEncryptedPassword);
        session.setAttribute("user", credentials);

        return "redirect:portal";
    }

    @RequestMapping("/tutor")
    public String tutor(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Credentials credentials = (Credentials) session.getAttribute("user");
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());

        if (oviUser.getTutorID() != null) {
            model.addAttribute("tutor", tutorDao.getTutor(oviUser.getTutorID()));
        }
        model.addAttribute("teTutor", oviUser.getTutorID() != null);
        return "oviuser/tutor";
    }

    @RequestMapping("/tutor/add")
    public String addTutor(Model model) {
        model.addAttribute("tutor", new Tutor());
        return "oviuser/tutorForm";
    }

    @RequestMapping(value = "/tutor/add", method = RequestMethod.POST)
    public String processAddTutor(@ModelAttribute("tutor") Tutor tutor,
                                  BindingResult bindingResult,
                                  HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "oviuser/tutorForm";
        }
        tutorDao.addTutor(tutor);
        int tutorID = tutorDao.getLastInsertedId();
        Credentials credentials = (Credentials) session.getAttribute("user");
        oviUserDao.updateTutorID(credentials.getUsername(), tutorID);
        return "redirect:/oviUser/tutor";
    }

    @RequestMapping("/tutor/edit")
    public String editTutor(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute("user");
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        model.addAttribute("tutor", tutorDao.getTutor(oviUser.getTutorID()));
        return "oviuser/tutorForm";
    }

    @RequestMapping(value = "/tutor/edit", method = RequestMethod.POST)
    public String processEditTutor(@ModelAttribute("tutor") Tutor tutor,
                                   BindingResult bindingResult,
                                   HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "oviuser/tutorForm";
        }
        Credentials credentials = (Credentials) session.getAttribute("user");
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        tutor.setTutorID(oviUser.getTutorID());
        tutorDao.updateTutor(tutor);
        return "redirect:/oviUser/tutor";
    }

    @RequestMapping(value = "/tutor/delete", method = RequestMethod.POST)
    public String deleteTutor(HttpSession session) {
        Credentials credentials = (Credentials) session.getAttribute("user");
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        tutorDao.deleteTutor(oviUser.getTutorID());
        oviUserDao.removeTutorID(credentials.getUsername());
        return "redirect:/oviUser/tutor";
    }

    @RequestMapping("/contractes")
    public String contractes(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute("user");
        OviUser oviUser = oviUserDao.getOviUserByUsername(credentials.getUsername());
        List<Contract> contractes = contractDao.getContractsByOviUser(oviUser.getOviID());

        Map<Integer, Negotiation> negotiations = new HashMap<>();
        Map<Integer, PapPati> papPatis = new HashMap<>();
        for (Contract c : contractes) {
            Negotiation neg = negotiationDao.getNegotiation(c.getNegotiationID());
            negotiations.put(c.getContractID(), neg);
            papPatis.put(c.getContractID(), papPatiDao.getPapPati(neg.getPapID()));
        }

        model.addAttribute("contractes", contractes);
        model.addAttribute("negotiations", negotiations);
        model.addAttribute("papPatis", papPatis);
        return "oviuser/contractes";
    }
}