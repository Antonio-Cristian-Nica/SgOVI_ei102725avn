package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.OviUser;
import es.uji.ei1027.ovi.model.OviUserRegistration;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.time.LocalDate;
import java.time.Period;

public class OviUserValidator implements Validator {
    private static final String ERROR_OBLIGATORI = "obligatori";
    private static final String ERROR_FORMAT = "format";
    private static final String ERROR_LONGITUD = "longitud";

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return OviUser.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        OviUser oviUser = (OviUser) target;

        if (oviUser.getNameAndSurname() == null || oviUser.getNameAndSurname().trim().isEmpty()) {
            errors.rejectValue("nameAndSurname", ERROR_OBLIGATORI, "El nom i cognoms són obligatoris");
        } else if (oviUser.getNameAndSurname().length() > 100) {
            errors.rejectValue("nameAndSurname", ERROR_LONGITUD, "El nom i cognoms no pot superar els 100 caràcters");
        }

        if (oviUser.getPhoneNumber() == null || oviUser.getPhoneNumber().trim().isEmpty()) {
            errors.rejectValue("phoneNumber", ERROR_OBLIGATORI, "El telèfon és obligatori");
        } else if (!oviUser.getPhoneNumber().matches("^(\\+34)?[0-9]{9}$")) {
            errors.rejectValue("phoneNumber", ERROR_FORMAT, "El telèfon ha de tenir 9 dígits");
        } else if (oviUser.getPhoneNumber().length() > 20) {
            errors.rejectValue("phoneNumber", ERROR_LONGITUD, "El telèfon no pot superar els 20 caràcters");
        }

        if (oviUser.getHomeAddress() == null || oviUser.getHomeAddress().trim().isEmpty()) {
            errors.rejectValue("homeAddress", ERROR_OBLIGATORI, "L'adreça és obligatòria");
        } else if (oviUser.getHomeAddress().length() > 200) {
            errors.rejectValue("homeAddress", ERROR_LONGITUD, "L'adreça no pot superar els 200 caràcters");
        }

        if (oviUser.getEmailAddress() == null || oviUser.getEmailAddress().trim().isEmpty()) {
            errors.rejectValue("emailAddress", ERROR_OBLIGATORI, "El correu electrònic és obligatori");
        } else if (!oviUser.getEmailAddress().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            errors.rejectValue("emailAddress", ERROR_FORMAT, "El format del correu no és vàlid");
        } else if (oviUser.getEmailAddress().length() > 100) {
            errors.rejectValue("emailAddress", ERROR_LONGITUD, "El correu no pot superar els 100 caràcters");
        }

        if (oviUser.getBirthDate() == null) {
            errors.rejectValue("birthDate", ERROR_OBLIGATORI, "La data de naixement és obligatòria");
        } else {
            Period age = Period.between(oviUser.getBirthDate(), LocalDate.now());
            if (age.getYears() < 3) {
                errors.rejectValue("birthDate", "menor", "L'usuari ha de tindre almenys 3 anys");
            }
        }

        if (oviUser.getFunctionalDiversity() != null && oviUser.getFunctionalDiversity().length() > 150) {
            errors.rejectValue("functionalDiversity", ERROR_LONGITUD, "La diversitat funcional no pot superar els 150 caràcters");
        }

        if (!oviUser.isLOPDAcceptance()) {
            errors.rejectValue("LOPDAcceptance", ERROR_OBLIGATORI, "Has d'acceptar la Llei de Protecció de Dades");
        }

        if (target instanceof OviUserRegistration) {
            OviUserRegistration registration = (OviUserRegistration) target;

            if (registration.getUsername() == null || registration.getUsername().trim().isEmpty()) {
                errors.rejectValue("username", ERROR_OBLIGATORI, "Has de triar un nom d'usuari");
            } else if (registration.getUsername().contains(" ")) {
                errors.rejectValue("username", ERROR_FORMAT, "El nom d'usuari no pot contindre espais");
            } else if (registration.getUsername().length() > 50) {
                errors.rejectValue("username", ERROR_LONGITUD, "El nom d'usuari no pot superar els 50 caràcters");
            }

            if (registration.getPassword() == null || registration.getPassword().trim().isEmpty()) {
                errors.rejectValue("password", ERROR_OBLIGATORI, "La contrasenya és obligatòria");
            } else if (registration.getPassword().length() < 6) {
                errors.rejectValue("password", ERROR_FORMAT, "La contrasenya ha de tindre almenys 6 caràcters");
            } else if (registration.getPassword().length() > 100) {
                errors.rejectValue("password", ERROR_LONGITUD, "La contrasenya no pot superar els 100 caràcters");
            }
        }
    }
}