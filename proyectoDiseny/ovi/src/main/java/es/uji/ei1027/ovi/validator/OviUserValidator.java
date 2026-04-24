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

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return OviUser.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        OviUser oviUser = (OviUser) target;

        if (oviUser.getNameAndSurname() == null || oviUser.getNameAndSurname().trim().isEmpty()) {
            errors.rejectValue("nameAndSurname", ERROR_OBLIGATORI, "El nom i cognoms són obligatoris");
        }

        if (oviUser.getPhoneNumber() == null || oviUser.getPhoneNumber().trim().isEmpty()) {
            errors.rejectValue("phoneNumber", ERROR_OBLIGATORI, "El telèfon és obligatori");
        } else if (!oviUser.getPhoneNumber().matches("^(\\+34)?[0-9]{9}$")) {
            errors.rejectValue("phoneNumber", ERROR_FORMAT, "El telèfon ha de tenir 9 dígits");
        }

        if (oviUser.getHomeAddress() == null || oviUser.getHomeAddress().trim().isEmpty()) {
            errors.rejectValue("homeAddress", ERROR_OBLIGATORI, "L'adreça és obligatòria");
        }

        if (oviUser.getEmailAddress() == null || oviUser.getEmailAddress().trim().isEmpty()) {
            errors.rejectValue("emailAddress", ERROR_OBLIGATORI, "El correu electrònic és obligatori");
        } else if (!oviUser.getEmailAddress().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            errors.rejectValue("emailAddress", ERROR_FORMAT, "El format del correu no és vàlid");
        }

        if (oviUser.getBirthDate() == null) {
            errors.rejectValue("birthDate", ERROR_OBLIGATORI, "La data de naixement és obligatòria");
        } else {
            Period age = Period.between(oviUser.getBirthDate(), LocalDate.now());
            if (age.getYears() < 3) {
                errors.rejectValue("birthDate", "menor", "L'usuari ha de tindre almenys 3 anys");
            }
        }

        if (!oviUser.isLOPDAcceptance()) {
            errors.rejectValue("LOPDAcceptance", ERROR_OBLIGATORI, "Has d'acceptar la Llei de Protecció de Dades");
        }

        // Validaciones específicas de registro
        if (target instanceof OviUserRegistration) {
            OviUserRegistration registration = (OviUserRegistration) target;

            if (registration.getUsername() == null || registration.getUsername().trim().isEmpty()) {
                errors.rejectValue("username", ERROR_OBLIGATORI, "Has de triar un nom d'usuari");
            } else if (registration.getUsername().contains(" ")) {
                errors.rejectValue("username", ERROR_FORMAT, "El nom d'usuari no pot contindre espais");
            }

            if (registration.getPassword() == null || registration.getPassword().trim().isEmpty()) {
                errors.rejectValue("password", ERROR_OBLIGATORI, "La contrasenya és obligatòria");
            } else if (registration.getPassword().length() < 6) {
                errors.rejectValue("password", ERROR_FORMAT, "La contrasenya ha de tindre almenys 6 caràcters");
            }
        }
    }
}