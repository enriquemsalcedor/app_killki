package modelos;

public class Organizacion {
    private String nombre;
    private String email;
    private String direccion;
    private String telefonos;
    private String logo;

    public Organizacion() {
    }

    public Organizacion(String nombre, String email, String direccion, String telefonos, String logo) {
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.telefonos = telefonos;
        this.logo = logo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
