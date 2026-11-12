package model;

public class Usuario {
    private int id;
    private String codigoInstitucional;
    private String documentoIdentidad;
    private String nombre;
    private String apellido;
    private String correoInstitucional;
    private String telefono;
    private String tipoUsuario;
    private String estado;

    public Usuario(){

    }

    public Usuario(String apellido, String correoInstitucional, String documentoIdentidad, String estado, int id, String nombre, String telefono, String tipoUsuario) {
        this.apellido = apellido;
        this.correoInstitucional = correoInstitucional;
        this.documentoIdentidad = documentoIdentidad;
        this.estado = estado;
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipoUsuario = tipoUsuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getCodigoInstitucional() {
        return codigoInstitucional;
    }

    public void setCodigoInstitucional(String codigoInstitucional) {
        this.codigoInstitucional = codigoInstitucional;
    }

    
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", codigoInstitucional='" + codigoInstitucional + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correoInstitucional='" + correoInstitucional + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
       
}
