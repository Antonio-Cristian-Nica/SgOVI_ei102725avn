package es.uji.ei1027.ovi.model;

/*
Esta clase representa una fila de la tabla CREDENTIALS. Spring lo usa de dos formas:
(1) Thymeleaf lo lee/escribe en los formularios de login,
y (2) JdbcTemplate lo rellena al consultar la BBDD a través del CredentialsRowMapper.
La contraseña se guarda cifrada con Jasypt; nunca en claro.
 */

public class Credentials {
    private String username;
    private String password;
    private String role;
    private int id;
    private boolean activated;
    private String rejectionReason;
    private boolean rejected;

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public boolean isRejected() { return rejected; }
    public void setRejected(boolean rejected) { this.rejected = rejected; }

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
}
