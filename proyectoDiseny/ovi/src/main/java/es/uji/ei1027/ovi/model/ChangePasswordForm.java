package es.uji.ei1027.ovi.model;

/*
ChangePasswordForm es el DTO del formulario de cambio de contraseña.
Tiene tres campos: la actual (para verificar), la nueva, y su confirmación.
No es una entidad de BBDD, solo vive en memoria entre el formulario y el controlador.
 */

public class ChangePasswordForm {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
