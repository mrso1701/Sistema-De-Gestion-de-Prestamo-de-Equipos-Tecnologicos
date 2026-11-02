package model;

import java.time.LocalDateTime;

public class Prestamo {
    private Long id;
    private String codigoPrestamo;           
    private Long usuarioId;                
    private Long equipoId;                  
    private Long encargadoEntregaId;       
    private Long encargadoRecepcionId;      
    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraPactadaDevolucion;
    private LocalDateTime fechaHoraDevolucionReal; 
    private String estadoPrestamo;           
    private String observacionesEntrega;     
    private String observacionesDevolucion;  

    public Prestamo() {
    }

    
    public Prestamo(Long id, String codigoPrestamo, Long usuarioId, Long equipoId, 
                    Long encargadoEntregaId, Long encargadoRecepcionId, 
                    LocalDateTime fechaHoraSalida, LocalDateTime fechaHoraPactadaDevolucion, 
                    LocalDateTime fechaHoraDevolucionReal, String estadoPrestamo, 
                    String observacionesEntrega, String observacionesDevolucion) {
        this.id = id;
        this.codigoPrestamo = codigoPrestamo;
        this.usuarioId = usuarioId;
        this.equipoId = equipoId;
        this.encargadoEntregaId = encargadoEntregaId;
        this.encargadoRecepcionId = encargadoRecepcionId;
        this.fechaHoraSalida = fechaHoraSalida;
        this.fechaHoraPactadaDevolucion = fechaHoraPactadaDevolucion;
        this.fechaHoraDevolucionReal = fechaHoraDevolucionReal;
        this.estadoPrestamo = estadoPrestamo;
        this.observacionesEntrega = observacionesEntrega;
        this.observacionesDevolucion = observacionesDevolucion;
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoPrestamo() {
        return codigoPrestamo;
    }

    public void setCodigoPrestamo(String codigoPrestamo) {
        this.codigoPrestamo = codigoPrestamo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Long equipoId) {
        this.equipoId = equipoId;
    }

    public Long getEncargadoEntregaId() {
        return encargadoEntregaId;
    }

    public void setEncargadoEntregaId(Long encargadoEntregaId) {
        this.encargadoEntregaId = encargadoEntregaId;
    }

    public Long getEncargadoRecepcionId() {
        return encargadoRecepcionId;
    }

    public void setEncargadoRecepcionId(Long encargadoRecepcionId) {
        this.encargadoRecepcionId = encargadoRecepcionId;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public LocalDateTime getFechaHoraPactadaDevolucion() {
        return fechaHoraPactadaDevolucion;
    }

    public void setFechaHoraPactadaDevolucion(LocalDateTime fechaHoraPactadaDevolucion) {
        this.fechaHoraPactadaDevolucion = fechaHoraPactadaDevolucion;
    }

    public LocalDateTime getFechaHoraDevolucionReal() {
        return fechaHoraDevolucionReal;
    }

    public void setFechaHoraDevolucionReal(LocalDateTime fechaHoraDevolucionReal) {
        this.fechaHoraDevolucionReal = fechaHoraDevolucionReal;
    }

    public String getEstadoPrestamo() {
        return estadoPrestamo;
    }

    public void setEstadoPrestamo(String estadoPrestamo) {
        this.estadoPrestamo = estadoPrestamo;
    }

    public String getObservacionesEntrega() {
        return observacionesEntrega;
    }

    public void setObservacionesEntrega(String observacionesEntrega) {
        this.observacionesEntrega = observacionesEntrega;
    }

    public String getObservacionesDevolucion() {
        return observacionesDevolucion;
    }

    public void setObservacionesDevolucion(String observacionesDevolucion) {
        this.observacionesDevolucion = observacionesDevolucion;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", codigoPrestamo='" + codigoPrestamo + '\'' +
                ", usuarioId=" + usuarioId +
                ", equipoId=" + equipoId +
                ", fechaHoraSalida=" + fechaHoraSalida +
                ", fechaHoraPactadaDevolucion=" + fechaHoraPactadaDevolucion +
                ", estadoPrestamo='" + estadoPrestamo + '\'' +
                '}';
    }
}