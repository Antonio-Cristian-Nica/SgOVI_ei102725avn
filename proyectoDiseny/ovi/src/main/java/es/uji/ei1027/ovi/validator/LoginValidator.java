package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.Credentials;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/*
LoginValidator implementa Validator.
supports() declara que valida la clase Credentials.
validate() comprueba que username y password no estén vacíos.
Si fallan, registra el error en errors → el BindingResult en el controlador llevará hasErrors()==true
y la vista login.html mostrará el mensaje en el div th:errors.
 */

public class LoginValidator implements Validator {
    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return Credentials.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        Credentials credentials = (Credentials) target;

        if (credentials.getUsername() == null || credentials.getUsername().trim().isEmpty()) {
            errors.rejectValue("username", "obligatori", "Has d'introduir el nom d'usuari");
        }

        if (credentials.getPassword() == null || credentials.getPassword().trim().isEmpty()) {
            errors.rejectValue("password", "obligatori", "Has d'introduir la contrasenya");
        }
    }
}