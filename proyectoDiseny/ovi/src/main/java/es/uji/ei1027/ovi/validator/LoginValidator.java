package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.Credentials;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class LoginValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Credentials.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Credentials credentials = (Credentials) target;

        if (credentials.getUsername() == null || credentials.getUsername().trim().isEmpty()) {
            errors.rejectValue("username", "obligatori", "Has d'introduir el nom d'usuari");
        }

        if (credentials.getPassword() == null || credentials.getPassword().trim().isEmpty()) {
            errors.rejectValue("password", "obligatori", "Has d'introduir la contrasenya");
        }
    }
}