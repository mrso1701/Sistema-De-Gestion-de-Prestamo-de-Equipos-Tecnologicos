package model;

import java.time.LocalDateTime;

public class Sancion {
    private Long id;
    private Long prestamoId;       
    private Long usuarioId;         
    private String motivo;          
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;          
    private String observaciones;   

    public Sancion() {
    }

    public Sancion(Long id, Long prestamoId, Long usuarioId, String motivo, 
                   LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado, String observaciones) {
        this.id = id;
        this.prestamoId = prestamoId;
        this.usuarioId = usuarioId;
        this.motivo = motivo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.observaciones = observaciones;
    }

   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(Long prestamoId) {
        this.prestamoId = prestamoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Sancion{" +
                "id=" + id +
                ", prestamoId=" + prestamoId +
                ", usuarioId=" + usuarioId +
                ", motivo='" + motivo + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", estado='" + estado + '\'' +
                '}';
    }
}

