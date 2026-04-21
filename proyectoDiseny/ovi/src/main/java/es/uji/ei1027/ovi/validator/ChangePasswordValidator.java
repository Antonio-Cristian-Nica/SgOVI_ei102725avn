package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.ChangePasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ChangePasswordValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ChangePasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChangePasswordForm form = (ChangePasswordForm) target;

        if (form.getCurrentPassword() == null || form.getCurrentPassword().trim().isEmpty()) {
            errors.rejectValue("currentPassword", "obligatori", "La contrasenya actual és obligatòria");
        }

        if (form.getNewPassword() == null || form.getNewPassword().trim().isEmpty()) {
            errors.rejectValue("newPassword", "obligatori", "La nova contrasenya és obligatòria");
        } else if (form.getNewPassword().length() < 6) {
            errors.rejectValue("newPassword", "format", "La nova contrasenya ha de tindre almenys 6 caràcters");
        }

        if (form.getConfirmPassword() == null || form.getConfirmPassword().trim().isEmpty()) {
            errors.rejectValue("confirmPassword", "obligatori", "Has de confirmar la nova contrasenya");
        } else if (form.getNewPassword() != null && !form.getNewPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "noCoincideix", "Les contrasenyes no coincideixen");
        }
    }
}