package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.OviUser;
import es.uji.ei1027.ovi.model.OviUserRegistration;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.time.LocalDate;
import java.time.Period;

public class OviUserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return OviUser.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OviUser oviUser = (OviUser) target;

        if (oviUser.getNameAndSurname() == null || oviUser.getNameAndSurname().trim().isEmpty()) {
            errors.rejectValue("nameAndSurname", "obligatori", "El nom i cognoms són obligatoris");
        }

        if (oviUser.getPhoneNumber() == null || oviUser.getPhoneNumber().trim().isEmpty()) {
            errors.rejectValue("phoneNumber", "obligatori", "El telèfon és obligatori");
        } else if (!oviUser.getPhoneNumber().matches("^(\\+34)?[0-9]{9}$")) {
            errors.rejectValue("phoneNumber", "format", "El telèfon ha de tenir 9 dígits");
        }

        if (oviUser.getHomeAddress() == null || oviUser.getHomeAddress().trim().isEmpty()) {
            errors.rejectValue("homeAddress", "obligatori", "L'adreça és obligatòria");
        }

        if (oviUser.getEmailAddress() == null || oviUser.getEmailAddress().trim().isEmpty()) {
            errors.rejectValue("emailAddress", "obligatori", "El correu electrònic és obligatori");
        } else if (!oviUser.getEmailAddress().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            errors.rejectValue("emailAddress", "format", "El format del correu no és vàlid");
        }

        if (oviUser.getBirthDate() == null) {
            errors.rejectValue("birthDate", "obligatori", "La data de naixement és obligatòria");
        } else {
            Period age = Period.between(oviUser.getBirthDate(), LocalDate.now());
            if (age.getYears() < 3) {
                errors.rejectValue("birthDate", "menor", "L'usuari ha de tindre almenys 3 anys");
            }
        }

        if (!oviUser.isLOPDAcceptance()) {
            errors.rejectValue("LOPDAcceptance", "obligatori", "Has d'acceptar la Llei de Protecció de Dades");
        }

        // Validaciones específicas de registro
        if (target instanceof OviUserRegistration) {
            OviUserRegistration registration = (OviUserRegistration) target;

            if (registration.getUsername() == null || registration.getUsername().trim().isEmpty()) {
                errors.rejectValue("username", "obligatori", "Has de triar un nom d'usuari");
            } else if (registration.getUsername().contains(" ")) {
                errors.rejectValue("username", "format", "El nom d'usuari no pot contindre espais");
            }

            if (registration.getPassword() == null || registration.getPassword().trim().isEmpty()) {
                errors.rejectValue("password", "obligatori", "La contrasenya és obligatòria");
            } else if (registration.getPassword().length() < 6) {
                errors.rejectValue("password", "format", "La contrasenya ha de tindre almenys 6 caràcters");
            }
        }
    }
}