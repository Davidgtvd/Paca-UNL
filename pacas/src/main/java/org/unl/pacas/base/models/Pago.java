package org.unl.pacas.base.models;

public class Pago {
    private Integer id;
    private String codigo_seguridad;
    private MetodoPagoEnum metodoPago;
    private Boolean estado;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo_seguridad() {
        return this.codigo_seguridad;
    }

    public void setCodigo_seguridad(String codigo_seguridad) {
        this.codigo_seguridad = codigo_seguridad;
    }

    public MetodoPagoEnum getMetodoPago() {
        return this.metodoPago;
    }

    public void setMetodoPago(MetodoPagoEnum metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Boolean getEstado() {
        return this.estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
}
