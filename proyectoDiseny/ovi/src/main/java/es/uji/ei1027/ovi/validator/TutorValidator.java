package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.Tutor;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.Period;

public class TutorValidator implements Validator {

    private static final String ERROR_OBLIGATORI = "obligatori";
    private static final String ERROR_FORMAT = "format";
    private static final String ERROR_LONGITUD = "longitud";

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return Tutor.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        Tutor tutor = (Tutor) target;

        if (tutor.getNameAndSurname() == null || tutor.getNameAndSurname().trim().isEmpty()) {
            errors.rejectValue("nameAndSurname", ERROR_OBLIGATORI, "El nom i cognoms són obligatoris");
        } else if (tutor.getNameAndSurname().length() > 100) {
            errors.rejectValue("nameAndSurname", ERROR_LONGITUD, "El nom i cognoms no pot superar els 100 caràcters");
        }

        if (tutor.getPhoneNumber() == null || tutor.getPhoneNumber().trim().isEmpty()) {
            errors.rejectValue("phoneNumber", ERROR_OBLIGATORI, "El telèfon és obligatori");
        } else if (!tutor.getPhoneNumber().matches("^(\\+34)?[0-9]{9}$")) {
            errors.rejectValue("phoneNumber", ERROR_FORMAT, "El telèfon ha de tenir 9 dígits");
        }

        if (tutor.getEmailAddress() == null || tutor.getEmailAddress().trim().isEmpty()) {
            errors.rejectValue("emailAddress", ERROR_OBLIGATORI, "El correu electrònic és obligatori");
        } else if (!tutor.getEmailAddress().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            errors.rejectValue("emailAddress", ERROR_FORMAT, "El format del correu no és vàlid");
        } else if (tutor.getEmailAddress().length() > 100) {
            errors.rejectValue("emailAddress", ERROR_LONGITUD, "El correu no pot superar els 100 caràcters");
        }

        if (tutor.getBirthDate() == null) {
            errors.rejectValue("birthDate", ERROR_OBLIGATORI, "La data de naixement és obligatòria");
        } else {
            Period age = Period.between(tutor.getBirthDate(), LocalDate.now());
            if (age.getYears() < 18) {
                errors.rejectValue("birthDate", "menor", "El tutor ha de ser major d'edat");
            }
        }

        if (tutor.getHomeAddress() == null || tutor.getHomeAddress().trim().isEmpty()) {
            errors.rejectValue("homeAddress", ERROR_OBLIGATORI, "L'adreça és obligatòria");
        } else if (tutor.getHomeAddress().length() > 200) {
            errors.rejectValue("homeAddress", ERROR_LONGITUD, "L'adreça no pot superar els 200 caràcters");
        }

        if (tutor.getRelationshipWithUser() == null || tutor.getRelationshipWithUser().trim().isEmpty()) {
            errors.rejectValue("relationshipWithUser", ERROR_OBLIGATORI, "Has d'indicar la relació amb l'usuari");
        } else if (tutor.getRelationshipWithUser().length() > 50) {
            errors.rejectValue("relationshipWithUser", ERROR_LONGITUD, "La relació no pot superar els 50 caràcters");
        }
    }
}
