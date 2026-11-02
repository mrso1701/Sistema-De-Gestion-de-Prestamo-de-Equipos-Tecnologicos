package model;

public class Equipo {
    private Long id;
    private String codigoInventario; 
    private String marca;
    private String modelo;
    private String tipoEquipo;      
    private String estado;           
    private String observacionesEstado;

    public Equipo(){

    }

    public Equipo(String codigoInventario, String estado, Long id, String marca, String modelo, String observacionesEstado, String tipoEquipo) {
        this.codigoInventario = codigoInventario;
        this.estado = estado;
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.observacionesEstado = observacionesEstado;
        this.tipoEquipo = tipoEquipo;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getCodigoInventario() {return codigoInventario;}

    public void setCodigoInventario(String codigoInventario) {this.codigoInventario = codigoInventario;}

    public String getMarca() {return marca;}

    public void setMarca(String marca) {this.marca = marca;}

    public String getModelo() {return modelo;}

    public void setModelo(String modelo) {this.modelo = modelo;}

    public String getTipoEquipo() {return tipoEquipo;}

    public void setTipoEquipo(String tipoEquipo) {this.tipoEquipo = tipoEquipo;}

    public String getEstado() {return estado;}

    public void setEstado(String estado) {this.estado = estado;}

    public String getObservacionesEstado() {return observacionesEstado;}

    public void setObservacionesEstado(String observacionesEstado) {this.observacionesEstado = observacionesEstado;}

    @Override
    public String toString() {
        return "Equipo{" +
                "id=" + id +
                ", codigoInventario='" + codigoInventario + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", tipoEquipo='" + tipoEquipo + '\'' +
                ", estado='" + estado + '\'' +
                ", observacionesEstado='" + observacionesEstado + '\'' +
                '}';
    }

}
