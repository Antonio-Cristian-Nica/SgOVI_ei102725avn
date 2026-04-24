package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.PapPati;
import es.uji.ei1027.ovi.model.PapPatiRegistration; // ¡Importante importar el DTO!
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.time.LocalDate;
import java.time.Period;

public class PapPatiValidator implements Validator {
    private static final String ERROR_OBLIGATORI = "obligatori";
    private static final String ERROR_FORMAT = "format";

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        // CAMBIO CLAVE: Usamos isAssignableFrom para que acepte tanto PapPati como PapPatiRegistration
        // en un futuro nos ayudará cuando solo queramos editar los datos
        return PapPati.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        PapPati papPati = (PapPati) target;

        // 1. Campos de texto obligatorios (Validación manual)
        if (papPati.getNameAndSurname() == null || papPati.getNameAndSurname().trim().isEmpty()) {
            errors.rejectValue("nameAndSurname", ERROR_OBLIGATORI, "El nombre y apellidos son obligatorios");
        }

        if (papPati.getPhoneNumber() == null || papPati.getPhoneNumber().trim().isEmpty()) {
            errors.rejectValue("phoneNumber", ERROR_OBLIGATORI, "El teléfono es obligatorio");
        } else if (!papPati.getPhoneNumber().matches("^(\\+34)?[0-9]{9}$")) {
            errors.rejectValue("phoneNumber", ERROR_FORMAT, "El telèfon ha de tenir 9 dígits");
        }

        if (papPati.getHomeAddress() == null || papPati.getHomeAddress().trim().isEmpty()) {
            errors.rejectValue("homeAddress", ERROR_OBLIGATORI, "La dirección es obligatoria");
        }

        if (papPati.getAcademicBackground() == null || papPati.getAcademicBackground().trim().isEmpty()) {
            errors.rejectValue("academicBackground", ERROR_OBLIGATORI, "Los antecedentes académicos son obligatorios");
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

        // 2. Validación de Correo Electrónico (Obligatorio + Formato)
        if (papPati.getEmailAddress() == null || papPati.getEmailAddress().trim().isEmpty()) {
            errors.rejectValue("emailAddress", ERROR_OBLIGATORI, "El correo electrónico es obligatorio");
        } else if (!papPati.getEmailAddress().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            errors.rejectValue("emailAddress", ERROR_FORMAT, "El formato del correo no es válido");
        }

        // 3. Validación de Fecha de Nacimiento (Obligatoria + Mayor de 18 años)
        if (papPati.getBirthDate() == null) {
            errors.rejectValue("birthDate", ERROR_OBLIGATORI, "La fecha de nacimiento es obligatoria");
        } else {
            Period age = Period.between(papPati.getBirthDate(), LocalDate.now());
            if (age.getYears() < 18) {
                errors.rejectValue("birthDate", "menor", "El PAP/PATI debe ser mayor de edad (18 años)");
            }
        }

        // 4. Aceptación de LOPD (Debe ser true)
        if (!papPati.isLOPDAcceptance()) {
            errors.rejectValue("LOPDAcceptance", ERROR_OBLIGATORI, "Debes aceptar la Ley de Protección de Datos para registrarte");
        }

        // =====================================================================
        // 5. VALIDACIONES ESPECÍFICAS DE REGISTRO (Credenciales)
        // =====================================================================
        if (target instanceof PapPatiRegistration) {
            PapPatiRegistration registration = (PapPatiRegistration) target;

            // Validación del Usuario
            if (registration.getUsername() == null || registration.getUsername().trim().isEmpty()) {
                errors.rejectValue("username", ERROR_OBLIGATORI, "Debes elegir un nombre de usuario");
            } else if (registration.getUsername().contains(" ")) {
                errors.rejectValue("username", ERROR_FORMAT, "El nombre de usuario no puede contener espacios");
            }

            // Validación de la Contraseña
            if (registration.getPassword() == null || registration.getPassword().trim().isEmpty()) {
                errors.rejectValue("password", ERROR_OBLIGATORI, "La contraseña es obligatoria");
            } else if (registration.getPassword().length() < 6) {
                errors.rejectValue("password", ERROR_FORMAT, "La contraseña debe tener al menos 6 caracteres");
            }
        }
    }
}
