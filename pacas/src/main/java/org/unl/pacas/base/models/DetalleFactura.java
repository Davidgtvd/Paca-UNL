package org.unl.pacas.base.models;

public class DetalleFactura {
    private Integer id;
    private Double total;
    private Integer cantidad;
    private Double precioUnitario;
    private Integer id_compra;
    private Integer id_producto;
    

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getTotal() {
        return this.total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return this.precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getId_compra() {
        return this.id_compra;
    }

    public void setId_compra(Integer id_compra) {
        this.id_compra = id_compra;
    }

    public Integer getId_producto() {
        return this.id_producto;
    }

    public void setId_producto(Integer id_producto) {
        this.id_producto = id_producto;
    }


}
