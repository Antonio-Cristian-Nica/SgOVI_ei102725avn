package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.PapPati;
import es.uji.ei1027.ovi.model.PapPatiRegistration;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.time.LocalDate;
import java.time.Period;

public class PapPatiValidator implements Validator {
    private static final String ERROR_OBLIGATORI = "obligatori";
    private static final String ERROR_FORMAT = "format";
    private static final String ERROR_LONGITUD = "longitud";

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return PapPati.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        PapPati papPati = (PapPati) target;

        if (papPati.getNameAndSurname() == null || papPati.getNameAndSurname().trim().isEmpty()) {
            errors.rejectValue("nameAndSurname", ERROR_OBLIGATORI, "El nom i cognoms és obligatori");
        } else if (papPati.getNameAndSurname().length() > 100) {
            errors.rejectValue("nameAndSurname", ERROR_LONGITUD, "El nom i cognoms no pot superar els 100 caràcters");
        }

        if (papPati.getPhoneNumber() == null || papPati.getPhoneNumber().trim().isEmpty()) {
            errors.rejectValue("phoneNumber", ERROR_OBLIGATORI, "El telèfon és obligatori");
        } else if (!papPati.getPhoneNumber().matches("^(\\+34)?[0-9]{9}$")) {
            errors.rejectValue("phoneNumber", ERROR_FORMAT, "El telèfon ha de tenir 9 dígits");
        } else if (papPati.getPhoneNumber().length() > 20) {
            errors.rejectValue("phoneNumber", ERROR_LONGITUD, "El telèfon no pot superar els 20 caràcters");
        }

        if (papPati.getHomeAddress() == null || papPati.getHomeAddress().trim().isEmpty()) {
            errors.rejectValue("homeAddress", ERROR_OBLIGATORI, "L'adreça és obligatòria");
        } else if (papPati.getHomeAddress().length() > 200) {
            errors.rejectValue("homeAddress", ERROR_LONGITUD, "L'adreça no pot superar els 200 caràcters");
        }

        if (papPati.getLocality() == null || papPati.getLocality().trim().isEmpty()) {
            errors.rejectValue("locality", ERROR_OBLIGATORI, "La localitat és obligatòria");
        } else if (papPati.getLocality().length() > 100) {
            errors.rejectValue("locality", ERROR_LONGITUD, "La localitat no pot superar els 100 caràcters");
        }

        if (papPati.getEmailAddress() == null || papPati.getEmailAddress().trim().isEmpty()) {
            errors.rejectValue("emailAddress", ERROR_OBLIGATORI, "El correu electrònic és obligatori");
        } else if (!papPati.getEmailAddress().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            errors.rejectValue("emailAddress", ERROR_FORMAT, "El format del correu no és vàlid");
        } else if (papPati.getEmailAddress().length() > 100) {
            errors.rejectValue("emailAddress", ERROR_LONGITUD, "El correu no pot superar els 100 caràcters");
        }

        if (papPati.getBirthDate() == null) {
            errors.rejectValue("birthDate", ERROR_OBLIGATORI, "La data de naixement és obligatòria");
        } else {
            Period age = Period.between(papPati.getBirthDate(), LocalDate.now());
            if (age.getYears() < 18) {
                errors.rejectValue("birthDate", "menor", "El PAP/PATI ha de ser major d'edat (18 anys)");
            }
        }

        if (papPati.getAcademicBackground() == null || papPati.getAcademicBackground().trim().isEmpty()) {
            errors.rejectValue("academicBackground", ERROR_OBLIGATORI, "La formació acadèmica és obligatòria");
        }

        if (papPati.getProfessionalExperience() == null || papPati.getProfessionalExperience().trim().isEmpty()) {
            errors.rejectValue("professionalExperience", ERROR_OBLIGATORI, "L'experiència professional és obligatòria");
        }

        if (papPati.getSpecializationAreas() == null || papPati.getSpecializationAreas().trim().isEmpty()) {
            errors.rejectValue("specializationAreas", ERROR_OBLIGATORI, "Les àrees d'especialització són obligatòries");
        }

        if (papPati.getDocuments() == null || papPati.getDocuments().trim().isEmpty()) {
            errors.rejectValue("documents", ERROR_OBLIGATORI, "Has d'adjuntar almenys un document o enllaç");
        }

        if (!papPati.isLOPDAcceptance()) {
            errors.rejectValue("LOPDAcceptance", ERROR_OBLIGATORI, "Has d'acceptar la Llei de Protecció de Dades");
        }

        if (target instanceof PapPatiRegistration) {
            PapPatiRegistration registration = (PapPatiRegistration) target;

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
