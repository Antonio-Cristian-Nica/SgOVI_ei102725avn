package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.Credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.PapPati.PapPatiDao;
import es.uji.ei1027.ovi.model.ChangePasswordForm;
import es.uji.ei1027.ovi.model.Credentials;
import es.uji.ei1027.ovi.model.PapPati;
import es.uji.ei1027.ovi.model.PapPatiRegistration;
import es.uji.ei1027.ovi.utils.PasswordUtils;
import es.uji.ei1027.ovi.validator.ChangePasswordValidator;
import es.uji.ei1027.ovi.validator.PapPatiValidator;
import jakarta.servlet.http.HttpSession;
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
        return "registerSuccess";
    }

    @RequestMapping("/list")
    public String listPapPatis(Model model) {
        model.addAttribute("pappatis", papPatiDao.getPapPatis());
        return "papPati/list";
    }

    @RequestMapping("/portal")
    public String portal(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "papPati/portal";
    }

    @RequestMapping("/edit")
    public String editPapPati(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Credentials credentials = (Credentials) session.getAttribute("user");
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        model.addAttribute("pappati", papPati);
        return "papPati/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String processEditSubmit(@ModelAttribute("pappati") PapPati papPati,
                                    BindingResult bindingResult,
                                    HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        PapPatiValidator validator = new PapPatiValidator();
        validator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            return "papPati/edit";
        }

        papPatiDao.updatePapPati(papPati);
        return "redirect:portal";
    }

    @RequestMapping("/canviarContrasenya")
    public String canviarContrasenya(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "papPati/canviarContrasenya";
    }

    @RequestMapping(value = "/canviarContrasenya", method = RequestMethod.POST)
    public String processCanviarContrasenya(@ModelAttribute("changePasswordForm") ChangePasswordForm form,
                                            BindingResult bindingResult,
                                            HttpSession session,
                                            Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        ChangePasswordValidator validator = new ChangePasswordValidator();
        validator.validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return "papPati/canviarContrasenya";
        }

        // Comprobamos que la contraseña actual es correcta
        Credentials credentials = (Credentials) session.getAttribute("user");
        try {
            if (!PasswordUtils.check(form.getCurrentPassword(), credentials.getPassword())) {
                model.addAttribute("errorActual", "La contrasenya actual no és correcta");
                return "papPati/canviarContrasenya";
            }
        } catch (Exception e) {
            if (!credentials.getPassword().equals(form.getCurrentPassword())) {
                model.addAttribute("errorActual", "La contrasenya actual no és correcta");
                return "papPati/canviarContrasenya";
            }
        }

        // Guardamos la nueva contraseña encriptada
        String newEncryptedPassword = PasswordUtils.encrypt(form.getNewPassword());
        credentialsDao.updatePassword(credentials.getUsername(), newEncryptedPassword);

        // Actualizamos la sesión con la nueva contraseña
        credentials.setPassword(newEncryptedPassword);
        session.setAttribute("user", credentials);

        return "redirect:portal";
    }
}
