package com.example.agendday.modelos;

public class Nota {
    String idNota;
    String uidNota;
    String correo;
    String fechaHoraActual;
    String titulo;
    String descripcion;
    String fechaNota;
    String estado;

    public Nota() {
    }

    public Nota(String idNota, String uidNota, String correo, String fechaHoraActual, String titulo, String descripcion, String fechaNota, String estado) {
        this.idNota = idNota;
        this.uidNota = uidNota;
        this.correo = correo;
        this.fechaHoraActual = fechaHoraActual;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaNota = fechaNota;
        this.estado = estado;
    }

    public String getIdNota() {
        return idNota;
    }

    public void setIdNota(String idNota) {
        this.idNota = idNota;
    }

    public String getUidNota() {
        return uidNota;
    }

    public void setUidNota(String uidNota) {
        this.uidNota = uidNota;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFechaHoraActual() {
        return fechaHoraActual;
    }

    public void setFechaHoraActual(String fechaHoraActual) {
        this.fechaHoraActual = fechaHoraActual;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaNota() {
        return fechaNota;
    }

    public void setFechaNota(String fechaNota) {
        this.fechaNota = fechaNota;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
