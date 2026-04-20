package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.Credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.PapPati.PapPatiDao;
import es.uji.ei1027.ovi.model.Credentials;
import es.uji.ei1027.ovi.model.PapPatiRegistration;
import es.uji.ei1027.ovi.utils.PasswordUtils;
import es.uji.ei1027.ovi.validator.PapPatiValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/papPati")
public class PapPatiController {

    private PapPatiDao papPatiDao;
    private CredentialsDao credentialsDao;

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @Autowired
    public void setCredentialsDao(CredentialsDao credentialsDao) {
        this.credentialsDao = credentialsDao;
    }

    @RequestMapping("/register")
    public String addPapPati(Model model) {
        model.addAttribute("pappati", new PapPatiRegistration());
        return "papPati/register";
    }

    @RequestMapping(value="/register", method=RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("pappati") PapPatiRegistration registration,
                                   BindingResult bindingResult) {

        PapPatiValidator validator = new PapPatiValidator();
        validator.validate(registration, bindingResult);

        if (bindingResult.hasErrors()) {
            return "papPati/register";
        }

        // El status siempre es approvalPending al registrarse
        registration.setStatus("approvalPending");

        // Primero guardamos las credenciales
        Credentials credentials = new Credentials();
        credentials.setUsername(registration.getUsername());
        credentials.setPassword(PasswordUtils.encrypt(registration.getPassword()));
        credentials.setRole("pap_pati");
        credentials.setActivated(false);
        credentials.setId(0); // ID temporal, se actualizará después
        credentialsDao.addCredentials(credentials);

        // Luego guardamos el PAP/PATI con el username ya existente en CREDENTIALS
        papPatiDao.addPapPati(registration);

        // 3. Actualizamos el ID en credenciales con el papID real
        int papID = papPatiDao.getLastInsertedId();
        credentialsDao.updateId(registration.getUsername(), papID);

        return "redirect:registerSuccess";
    }

    @RequestMapping("/registerSuccess")
    public String registerSuccess() {
        return "papPati/registerSuccess";
    }

    @RequestMapping("/list")
    public String listPapPatis(Model model) {
        model.addAttribute("pappatis", papPatiDao.getPapPatis());
        return "papPati/list";
    }
}
