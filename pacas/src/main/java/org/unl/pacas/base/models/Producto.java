package org.unl.pacas.base.models;

import java.util.HashMap;
import java.util.Objects;

public class Producto {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String imagen;
    private Double precio;
    private Integer stock;
    private Double pvp; // precio de venta
    private CategoriaEnum categoria;

    public Producto() {}

    public Producto(Integer id, String nombre, String descripcion, String imagen,
                   Double precio, Integer stock, Double pvp, CategoriaEnum categoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.precio = precio;
        this.stock = stock;
        this.pvp = pvp;
        this.categoria = categoria;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Double getPvp() { return pvp; }
    public void setPvp(Double pvp) { this.pvp = pvp; }

    public CategoriaEnum getCategoria() { return categoria; }
    public void setCategoria(CategoriaEnum categoria) { this.categoria = categoria; }

    public static Producto createCopy(Producto obj) {
        if (obj == null) return null;
        return new Producto(
            obj.getId(),
            obj.getNombre(),
            obj.getDescripcion(),
            obj.getImagen(),
            obj.getPrecio(),
            obj.getStock(),
            obj.getPvp(),
            obj.getCategoria()
        );
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", id != null ? id.toString() : "0");
        map.put("nombre", nombre != null ? nombre : "");
        map.put("descripcion", descripcion != null ? descripcion : "");
        map.put("imagen", imagen != null ? imagen : "");
        map.put("precio", precio != null ? String.valueOf(precio) : "0.0");
        map.put("stock", stock != null ? String.valueOf(stock) : "0");
        map.put("pvp", pvp != null ? String.valueOf(pvp) : "0.0");
        map.put("categoria", categoria != null ? categoria.toString() : "");
        return map;
    }

    public boolean isValid() {
        return nombre != null && !nombre.trim().isEmpty() &&
               descripcion != null && !descripcion.trim().isEmpty() &&
               imagen != null && !imagen.trim().isEmpty() &&
               precio != null && precio > 0 &&
               stock != null && stock >= 0 &&
               pvp != null && pvp > 0 &&
               categoria != null;
    }

    public String getImagenUrl() {
        if (imagen == null || imagen.trim().isEmpty()) return null;
        if (imagen.startsWith("http://") || imagen.startsWith("https://") || imagen.startsWith("/")) {
            return imagen;
        }
        return "/imagenes/" + imagen;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", imagen='" + imagen + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", pvp=" + pvp +
                ", categoria=" + categoria +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Producto)) return false;
        Producto producto = (Producto) obj;
        return Objects.equals(id, producto.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}