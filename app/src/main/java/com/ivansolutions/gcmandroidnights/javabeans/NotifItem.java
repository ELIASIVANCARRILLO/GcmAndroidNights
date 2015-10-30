package com.ivansolutions.gcmandroidnights.javabeans;

import java.util.Date;

public class NotifItem {

    private long id;
    private String contenido;
    private Date fecha;

    public NotifItem() {
    }

    public NotifItem(long id, String contenido, Date fecha) {
        this.id = id;
        this.contenido = contenido;
        this.fecha = fecha;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
