package model;

import java.time.LocalDateTime;

public class Prestamo {
    private int id;
    private String codigoPrestamo;           
    private int usuarioId;                
    private int equipoId;                  
    private int encargadoEntregaId;       
    private int encargadoRecepcionId;      
    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraPactadaDevolucion;
    private LocalDateTime fechaHoraDevolucionReal; 
    private String estadoPrestamo;           
    private String observacionesEntrega;     
    private String observacionesDevolucion;  

    public Prestamo() {
    }

    
    public Prestamo(int id, String codigoPrestamo, int usuarioId, int equipoId, 
                    int encargadoEntregaId, int encargadoRecepcionId, 
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoPrestamo() {
        return codigoPrestamo;
    }

    public void setCodigoPrestamo(String codigoPrestamo) {
        this.codigoPrestamo = codigoPrestamo;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(int equipoId) {
        this.equipoId = equipoId;
    }

    public int getEncargadoEntregaId() {
        return encargadoEntregaId;
    }

    public void setEncargadoEntregaId(int encargadoEntregaId) {
        this.encargadoEntregaId = encargadoEntregaId;
    }

    public int getEncargadoRecepcionId() {
        return encargadoRecepcionId;
    }

    public void setEncargadoRecepcionId(int encargadoRecepcionId) {
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