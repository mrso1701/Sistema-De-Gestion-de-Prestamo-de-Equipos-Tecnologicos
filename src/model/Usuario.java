package model;

public class Usuario {
    private Long id;
    private String documentoIdentidad;
    private String nombre;
    private String apellido;
    private String correoInstitucional;
    private String telefono;
    private String tipoUsuario;
    private String estado;

    public Usuario(){

    }

    public Usuario(String apellido, String correoInstitucional, String documentoIdentidad, String estado, Long id, String nombre, String telefono, String tipoUsuario) {
        this.apellido = apellido;
        this.correoInstitucional = correoInstitucional;
        this.documentoIdentidad = documentoIdentidad;
        this.estado = estado;
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipoUsuario = tipoUsuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {this.correoInstitucional = correoInstitucional;}

    public String getTelefono() {return telefono;}

    public void setTelefono(String telefono) {this.telefono = telefono;}

    public String getTipoUsuario() {return tipoUsuario;}

    public void setTipoUsuario(String tipoUsuario) {this.tipoUsuario = tipoUsuario;}

    public String getEstado() {return estado;}

    public void setEstado(String estado) {this.estado = estado;}


    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", documentoIdentidad='" + documentoIdentidad + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", correoInstitucional='" + correoInstitucional + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
    
    
}
