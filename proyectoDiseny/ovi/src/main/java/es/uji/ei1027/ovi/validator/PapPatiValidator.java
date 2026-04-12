package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.PapPati;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.time.LocalDate;
import java.time.Period;

public class PapPatiValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PapPati.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PapPati papPati = (PapPati) target;

        // 1. Campos de texto obligatorios (Validación manual)
        if (papPati.getNameAndSurname() == null || papPati.getNameAndSurname().trim().isEmpty()) {
            errors.rejectValue("nameAndSurname", "obligatori", "El nombre y apellidos son obligatorios");
        }

        if (papPati.getPhoneNumber() == null || papPati.getPhoneNumber().trim().isEmpty()) {
            errors.rejectValue("phoneNumber", "obligatori", "El teléfono es obligatorio");
        }

        if (papPati.getHomeAddress() == null || papPati.getHomeAddress().trim().isEmpty()) {
            errors.rejectValue("homeAddress", "obligatori", "La dirección es obligatoria");
        }

        if (papPati.getAcademicBackground() == null || papPati.getAcademicBackground().trim().isEmpty()) {
            errors.rejectValue("academicBackground", "obligatori", "Los antecedentes académicos son obligatorios");
        }

        if (papPati.getStatus() == null || papPati.getStatus().trim().isEmpty()) {
            errors.rejectValue("status", "obligatori", "El estado inicial es obligatorio");
        }

        // 2. Validación de Correo Electrónico (Obligatorio + Formato)
        if (papPati.getEmailAddress() == null || papPati.getEmailAddress().trim().isEmpty()) {
            errors.rejectValue("emailAddress", "obligatori", "El correo electrónico es obligatorio");
        } else if (!papPati.getEmailAddress().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            errors.rejectValue("emailAddress", "format", "El formato del correo no es válido");
        }

        // 3. Validación de Fecha de Nacimiento (Obligatoria + Mayor de 18 años)
        if (papPati.getBirthDate() == null) {
            errors.rejectValue("birthDate", "obligatori", "La fecha de nacimiento es obligatoria");
        } else {
            Period age = Period.between(papPati.getBirthDate(), LocalDate.now());
            if (age.getYears() < 18) {
                errors.rejectValue("birthDate", "menor", "El PAP/PATI debe ser mayor de edad (18 años)");
            }
        }

        // 4. Aceptación de LOPD (Debe ser true)
        if (!papPati.isLOPDAcceptance()) {
            errors.rejectValue("LOPDAcceptance", "obligatori", "Debes aceptar la Ley de Protección de Datos para registrarte");
        }
    }
}
