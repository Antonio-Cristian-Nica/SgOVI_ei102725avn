package es.uji.ei1027.ovi.validator; // Ajusta el paquete según tu estructura

import es.uji.ei1027.ovi.model.OviUser;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class OviUserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        // Indica que este validador solo sirve para objetos de tipo OviUser
        return OviUser.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OviUser oviUser = (OviUser) target;

        // 1. Comprobar que los campos de texto no están vacíos (Como en la pág. 1 del PDF)
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nameAndSurname", "obligatori", "⚠️ Error: El nom és obligatori");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "obligatori", "⚠️ Error: El telèfon és obligatori");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "homeAddress", "obligatori", "⚠️ Error: L'adreça és obligatòria");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "obligatori", "⚠️ Error: L'email és obligatori");

        // 2. Validar formato de Teléfono (solo números)
        if (oviUser.getPhoneNumber() != null && !oviUser.getPhoneNumber().matches("^[0-9]+$")) {
            errors.rejectValue("phoneNumber", "format_incorrecte", "⚠️ Error: El telèfon només pot contenir números");
        }

        // 3. Validar formato de Email (que contenga un @)
        if (oviUser.getEmailAddress() != null && !oviUser.getEmailAddress().contains("@")) {
            errors.rejectValue("emailAddress", "format_incorrecte", "⚠️ Error: El format del correu no és vàlid");
        }

        // 4. Validar Fecha (que se haya seleccionado)
        if (oviUser.getBirthDate() == null) {
            errors.rejectValue("birthDate", "obligatori", "⚠️ Error: La data de naixement és obligatòria");
        }

        // 5. Validar Checkbox (LOPD)
        // Si tienes el LOPDAcceptance como 'boolean' en OviUser (isLOPDAcceptance)
        if (!oviUser.isLOPDAcceptance()) {
            errors.rejectValue("LOPDAcceptance", "obligatori", "⚠️ Error: Has d'acceptar la LOPD");
        }
    }
}