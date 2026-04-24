package es.uji.ei1027.ovi.model;

public class OviUserRegistration extends OviUser {

    private String username;
    private String password;

    public OviUserRegistration() {
        super();
    }

    @Override
    public String getUsername() { return username; }

    @Override
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
