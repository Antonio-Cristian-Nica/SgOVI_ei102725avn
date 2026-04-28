package es.uji.ei1027.ovi.controller;

import es.uji.ei1027.ovi.dao.credentials.CredentialsDao;
import es.uji.ei1027.ovi.dao.pappati.PapPatiDao;
import es.uji.ei1027.ovi.dao.pappatischedule.ScheduleDao;
import es.uji.ei1027.ovi.model.*;
import es.uji.ei1027.ovi.utils.PasswordUtils;
import es.uji.ei1027.ovi.validator.ChangePasswordValidator;
import es.uji.ei1027.ovi.validator.PapPatiValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/papPati")
public class PapPatiController {
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String CANVI_CONTRASENYA_PAPPATI_VIEW = "papPati/canviarContrasenya";
    private PapPatiDao papPatiDao;
    private CredentialsDao credentialsDao;
    private ScheduleDao scheduleDao;

    @Autowired
    public void setPapPatiDao(PapPatiDao papPatiDao) {
        this.papPatiDao = papPatiDao;
    }

    @Autowired
    public void setCredentialsDao(CredentialsDao credentialsDao) {
        this.credentialsDao = credentialsDao;
    }

    @Autowired
    public void setScheduleDao(ScheduleDao scheduleDao) { this.scheduleDao = scheduleDao; }

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
    public String portal(HttpSession session, Model model) {
        Credentials credentials = (Credentials) session.getAttribute("user");
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        model.addAttribute("teHoraris", scheduleDao.hasSchedules(papPati.getPapID()));
        return "papPati/portal";
    }

    @RequestMapping("/edit")
    public String editPapPati(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return REDIRECT_LOGIN;
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
            return REDIRECT_LOGIN;
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
            return REDIRECT_LOGIN;
        }
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return CANVI_CONTRASENYA_PAPPATI_VIEW;
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
            return CANVI_CONTRASENYA_PAPPATI_VIEW;
        }

        // Comprobamos que la contraseña actual es correcta
        Credentials credentials = (Credentials) session.getAttribute("user");
        try {
            if (!PasswordUtils.check(form.getCurrentPassword(), credentials.getPassword())) {
                model.addAttribute("errorActual", "La contrasenya actual no és correcta");
                return CANVI_CONTRASENYA_PAPPATI_VIEW;
            }
        } catch (Exception e) {
            if (!credentials.getPassword().equals(form.getCurrentPassword())) {
                model.addAttribute("errorActual", "La contrasenya actual no és correcta");
                return CANVI_CONTRASENYA_PAPPATI_VIEW;
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

    @RequestMapping("/horaris")
    public String horaris(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Credentials credentials = (Credentials) session.getAttribute("user");
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        List<Schedule> schedules = scheduleDao.getSchedulesByPap(papPati.getPapID());
        model.addAttribute("schedules", schedules);
        model.addAttribute("papID", papPati.getPapID());
        return "papPati/horaris";
    }

    @RequestMapping("/horaris/add")
    public String addHorari(Model model) {
        model.addAttribute("schedule", new Schedule());
        return "papPati/horarisForm";
    }

    @RequestMapping(value = "/horaris/add", method = RequestMethod.POST)
    public String processAddHorari(@ModelAttribute("schedule") Schedule schedule,
                                   HttpSession session,
                                   Model model) {
        boolean hasErrors = false;

        if (schedule.getDayOfWeek() == 0) {
            model.addAttribute("errorDia", "El dia és obligatori");
            hasErrors = true;
        }

        if (schedule.getStartHour() == null) {
            model.addAttribute("errorInici", "L'hora d'inici és obligatòria");
            hasErrors = true;
        }

        if (schedule.getEndHour() == null) {
            model.addAttribute("errorFi", "L'hora de fi és obligatòria");
            hasErrors = true;
        }

        if (schedule.getStartHour() != null && schedule.getEndHour() != null
                && !schedule.getEndHour().isAfter(schedule.getStartHour())) {
            model.addAttribute("errorFi", "L'hora de fi ha de ser posterior a l'hora d'inici");
            hasErrors = true;
        }

        if (hasErrors) {
            return "papPati/horarisForm";
        }

        Credentials credentials = (Credentials) session.getAttribute("user");
        PapPati papPati = papPatiDao.getPapPatiByUsername(credentials.getUsername());
        schedule.setPapID(papPati.getPapID());
        scheduleDao.addSchedule(schedule);
        return "redirect:/papPati/horaris";
    }

    @RequestMapping(value = "/horaris/delete/{scheduleID}", method = RequestMethod.POST)
    public String deleteHorari(@PathVariable("scheduleID") int scheduleID) {
        scheduleDao.deleteSchedule(scheduleID);
        return "redirect:/papPati/horaris";
    }
}
