package org.unl.pacas.base.models;

import java.util.Date;

public class Persona {
    private Integer id;
    private String nombre;
    private String apellido;
    private IdentificacionEnum identificacion;
    private String telefono;
    private SexoEnum sexo;
    private Date fecha_nacimiento;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public IdentificacionEnum getIdentificacion() {
        return this.identificacion;
    }

    public void setIdentificacion(IdentificacionEnum identificacion) {
        this.identificacion = identificacion;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public SexoEnum getSexo() {
        return this.sexo;
    }

    public void setSexo(SexoEnum sexo) {
        this.sexo = sexo;
    }

    public Date getFecha_nacimiento() {
        return this.fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

}