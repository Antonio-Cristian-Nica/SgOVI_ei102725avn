package es.uji.ei1027.ovi.model;

import es.uji.ei1027.ovi.model.PapPati;

public class PapPatiRegistration extends PapPati {

    private String username;
    private String password;

    public PapPatiRegistration() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
