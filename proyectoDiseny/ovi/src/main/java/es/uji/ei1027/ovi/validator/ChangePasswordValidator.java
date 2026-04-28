package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.ChangePasswordForm;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ChangePasswordValidator implements Validator {
    private static final String ERROR_OBLIGATORI = "obligatori";

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return ChangePasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        ChangePasswordForm form = (ChangePasswordForm) target;

        if (form.getCurrentPassword() == null || form.getCurrentPassword().trim().isEmpty()) {
            errors.rejectValue("currentPassword", ERROR_OBLIGATORI, "La contrasenya actual és obligatòria");
        }

        if (form.getConfirmPassword() == null || form.getConfirmPassword().trim().isEmpty()) {
            errors.rejectValue("confirmPassword", ERROR_OBLIGATORI, "Has de confirmar la nova contrasenya");
        } else if (form.getNewPassword() != null && !form.getNewPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "noCoincideix", "Les contrasenyes no coincideixen");
        }

        if (form.getNewPassword() == null || form.getNewPassword().trim().isEmpty()) {
            errors.rejectValue("newPassword", ERROR_OBLIGATORI, "La nova contrasenya és obligatòria");
        } else if (form.getNewPassword().length() < 6) {
            errors.rejectValue("newPassword", "format", "La nova contrasenya ha de tindre almenys 6 caràcters");
        } else if (form.getNewPassword().length() > 100) {
            errors.rejectValue("newPassword", "longitud", "La contrasenya no pot superar els 100 caràcters");
        }
    }
}