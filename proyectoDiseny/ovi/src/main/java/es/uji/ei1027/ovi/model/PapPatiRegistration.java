package es.uji.ei1027.ovi.model;

/*
 * DTO per al formulari de registre de PAP/PATI. Hereta totes les dades
 * de PapPati i afegeix la contrasenya, que no pertany al model de domini.
 * El username s'hereta directament de PapPati.
 */
public class PapPatiRegistration extends PapPati {

    private String password;
    private String confirmPassword;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
