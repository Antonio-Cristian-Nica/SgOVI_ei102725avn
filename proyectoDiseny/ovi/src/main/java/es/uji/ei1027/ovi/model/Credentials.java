package es.uji.ei1027.ovi.model;

public class Credentials {
    private String username;
    private String password;
    private String role;
    private int id;
    private boolean activated;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean getActivated() { return activated; }
    public void setActivated(boolean activated) { this.activated = activated; }

    @Override
    public String toString() {
        return "Credentials{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", activated=" + activated +
                '}';
    }
}
