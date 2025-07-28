package org.unl.pacas.base.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Modelo de Cuenta para usuarios del sistema.
 * Incluye soporte para roles ADMINISTRADOR y CLIENTE.
 */
public class Cuenta {
    private Integer id;
    private String correoElectronico;
    private String clave;
    private RolEnum rol;
    private Integer id_persona;

    // Lista de productos asociados a la cuenta (solo para administradores)
    private List<Producto> productos;

    // Constructor vacío
    public Cuenta() {
        this.productos = new ArrayList<>();
    }

    // Constructor completo
    public Cuenta(Integer id, String correoElectronico, String clave, RolEnum rol, Integer id_persona) {
        this.id = id;
        this.correoElectronico = correoElectronico;
        this.clave = clave;
        this.rol = rol;
        this.id_persona = id_persona;
        this.productos = new ArrayList<>();
    }

    // Getters y Setters
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCorreoElectronico() {
        return this.correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getClave() {
        return this.clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public RolEnum getRol() {
        return this.rol;
    }

    public void setRol(RolEnum rol) {
        this.rol = rol;
    }

    public Integer getId_persona() {
        return this.id_persona;
    }

    public void setId_persona(Integer id_persona) {
        this.id_persona = id_persona;
    }

    // Métodos para productos (solo para administradores)

    /**
     * Obtiene la lista de productos asociados a la cuenta.
     * Solo válido si es administrador.
     */
    public List<Producto> getProductos() {
        if (!esAdministrador()) {
            throw new UnsupportedOperationException("Solo administradores tienen productos asociados.");
        }
        return productos;
    }

    /**
     * Establece la lista completa de productos asociados.
     * Solo válido si es administrador.
     */
    public void setProductos(List<Producto> productos) {
        if (!esAdministrador()) {
            throw new UnsupportedOperationException("Solo administradores pueden modificar productos.");
        }
        this.productos = productos != null ? productos : new ArrayList<>();
    }

    /**
     * Agrega un producto a la lista.
     * Solo válido si es administrador.
     */
    public void agregarProducto(Producto producto) {
        if (!esAdministrador()) {
            throw new UnsupportedOperationException("Solo administradores pueden agregar productos.");
        }
        if (producto != null) {
            this.productos.add(producto);
        }
    }

    /**
     * Actualiza un producto existente en la lista por su id.
     * Solo válido si es administrador.
     */
    public boolean actualizarProducto(Producto productoActualizado) {
        if (!esAdministrador()) {
            throw new UnsupportedOperationException("Solo administradores pueden actualizar productos.");
        }
        if (productoActualizado == null || productoActualizado.getId() == null) {
            return false;
        }
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            if (Objects.equals(p.getId(), productoActualizado.getId())) {
                productos.set(i, productoActualizado);
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina un producto de la lista por su id.
     * Solo válido si es administrador.
     */
    public boolean eliminarProductoPorId(Integer idProducto) {
        if (!esAdministrador()) {
            throw new UnsupportedOperationException("Solo administradores pueden eliminar productos.");
        }
        return productos.removeIf(p -> Objects.equals(p.getId(), idProducto));
    }

    // Métodos utilitarios

    /**
     * Verifica si la cuenta es de administrador.
     */
    public boolean esAdministrador() {
        return this.rol == RolEnum.ADMINISTRADOR;
    }

    /**
     * Verifica si la cuenta es de cliente.
     */
    public boolean esCliente() {
        return this.rol == RolEnum.CLIENTE;
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "id=" + id +
                ", correoElectronico='" + correoElectronico + '\'' +
                ", rol=" + rol +
                ", id_persona=" + id_persona +
                ", productos=" + productos +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cuenta cuenta = (Cuenta) obj;
        return id != null && id.equals(cuenta.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}