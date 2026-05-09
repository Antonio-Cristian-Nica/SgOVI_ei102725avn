package es.uji.ei1027.ovi.model;

/*
 * DTO per al formulari de registre. Hereta totes les dades de l'OviUser
 * i afegeix la contrasenya, que no pertany al model de domini.
 * El username s'hereta directament d'OviUser.
 */
public class OviUserRegistration extends OviUser {

    private String password;
    private String confirmPassword;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
