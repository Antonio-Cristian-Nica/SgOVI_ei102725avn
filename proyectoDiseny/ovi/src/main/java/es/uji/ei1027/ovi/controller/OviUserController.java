package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.Credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.OviUser.OviUserDao;
import es.uji.ei1027.ovi.model.Credentials;
import es.uji.ei1027.ovi.model.OviUserRegistration;
import es.uji.ei1027.ovi.utils.PasswordUtils;
import es.uji.ei1027.ovi.validator.OviUserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/oviUser")
public class OviUserController {

    private OviUserDao oviUserDao;
    private CredentialsDao credentialsDao;

    @Autowired
    public void setOviUserDao(OviUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    @Autowired
    public void setCredentialsDao(CredentialsDao credentialsDao) {
        this.credentialsDao = credentialsDao;
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
        return "oviuser/registerSuccess";
    }

    @RequestMapping("/portal")
    public String portal(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "oviuser/portal";
    }
}