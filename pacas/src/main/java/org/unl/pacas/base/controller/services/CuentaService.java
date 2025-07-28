package org.unl.pacas.base.controller.services;

import java.util.Arrays;
import java.util.List;

import org.unl.pacas.base.controller.dao.dao_models.DaoCuenta;
import org.unl.pacas.base.controller.data_struct.list.LinkedList;
import org.unl.pacas.base.models.Cuenta;
import org.unl.pacas.base.models.Producto;
import org.unl.pacas.base.models.RolEnum;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@AnonymousAllowed
public class CuentaService {
    private DaoCuenta dao;

    public CuentaService() {
        dao = new DaoCuenta();
    }

    public Cuenta login(@NotEmpty String correo, @NotEmpty String clave) throws Exception {
        if (correo == null || correo.trim().isEmpty()) {
            throw new Exception("El correo es obligatorio");
        }
        if (clave == null || clave.trim().isEmpty()) {
            throw new Exception("La clave es obligatoria");
        }
        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new Exception("Formato de correo inválido");
        }
        Cuenta cuenta = dao.validarCredenciales(correo.trim(), clave.trim());
        if (cuenta == null) {
            throw new Exception("Credenciales incorrectas");
        }
        cuenta.setClave("");
        return cuenta;
    }

    public Cuenta registrarCliente(@NotEmpty String correo, @NotEmpty String clave) throws Exception {
        if (correo == null || correo.trim().isEmpty()) {
            throw new Exception("El correo es obligatorio");
        }
        if (clave == null || clave.trim().length() < 4) {
            throw new Exception("La clave debe tener al menos 4 caracteres");
        }
        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new Exception("Formato de correo inválido");
        }
        if (dao.findByEmail(correo.trim()) != null) {
            throw new Exception("Ya existe una cuenta con ese correo");
        }
        dao.getObj().setCorreoElectronico(correo.trim());
        dao.getObj().setClave(clave.trim());
        dao.getObj().setRol(RolEnum.CLIENTE);
        dao.getObj().setId_persona(null);
        if (!dao.save()) {
            throw new Exception("Error al crear la cuenta");
        }
        Cuenta nuevaCuenta = dao.findByEmail(correo.trim());
        if (nuevaCuenta != null) {
            nuevaCuenta.setClave("");
        }
        return nuevaCuenta;
    }

    public boolean esAdministrador(@NotEmpty String correo) {
        try {
            if (correo == null || correo.trim().isEmpty()) {
                return false;
            }
            Cuenta cuenta = dao.findByEmail(correo.trim());
            return cuenta != null && cuenta.getRol() == RolEnum.ADMINISTRADOR;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> listRoles() {
        return Arrays.stream(RolEnum.values())
                .map(Enum::toString)
                .toList();
    }

    public Cuenta getCuentaPorCorreo(@NotEmpty String correo) throws Exception {
        if (correo == null || correo.trim().isEmpty()) {
            throw new Exception("Correo no válido");
        }
        Cuenta cuenta = dao.findByEmail(correo.trim());
        if (cuenta == null) {
            throw new Exception("Cuenta no encontrada");
        }
        cuenta.setClave("");
        return cuenta;
    }

    public List<Cuenta> listAll(@NotEmpty String correoSolicitante) throws Exception {
        if (!esAdministrador(correoSolicitante)) {
            throw new Exception("Solo los administradores pueden ver todas las cuentas");
        }
        LinkedList<Cuenta> cuentas = dao.listAll();
        List<Cuenta> lista = Arrays.asList(cuentas.toArray());
        lista.forEach(c -> c.setClave(""));
        return lista;
    }

    public void cambiarClave(@NotEmpty String correo, @NotEmpty String claveActual, @NotEmpty String claveNueva) throws Exception {
        if (correo == null || correo.trim().isEmpty()) {
            throw new Exception("Correo no válido");
        }
        if (claveActual == null || claveActual.trim().isEmpty()) {
            throw new Exception("Clave actual obligatoria");
        }
        if (claveNueva == null || claveNueva.trim().length() < 4) {
            throw new Exception("La nueva clave debe tener al menos 4 caracteres");
        }
        Cuenta cuenta = dao.validarCredenciales(correo.trim(), claveActual.trim());
        if (cuenta == null) {
            throw new Exception("Clave actual incorrecta");
        }
        LinkedList<Cuenta> cuentas = dao.listAll();
        for (int i = 0; i < cuentas.getLength(); i++) {
            Cuenta c = cuentas.get(i);
            if (c != null && c.getId().equals(cuenta.getId())) {
                c.setClave(claveNueva.trim());
                dao.setObj(c);
                if (!dao.update(i)) {
                    throw new Exception("Error al actualizar la clave");
                }
                break;
            }
        }
    }

    // Métodos para productos en cuenta administrador

    public List<Producto> listarProductosDeAdmin(@NotEmpty String correoAdmin) throws Exception {
        if (!esAdministrador(correoAdmin)) {
            throw new Exception("Solo administradores pueden listar productos");
        }
        Cuenta admin = dao.findByEmail(correoAdmin.trim());
        if (admin == null) {
            throw new Exception("Cuenta administrador no encontrada");
        }
        List<Producto> productos = admin.getProductos();
        return productos != null ? productos : List.of();
    }

    public void agregarProducto(@NotEmpty String correoAdmin, Producto producto) throws Exception {
        if (!esAdministrador(correoAdmin)) {
            throw new Exception("Solo administradores pueden agregar productos");
        }
        Cuenta admin = dao.findByEmail(correoAdmin.trim());
        if (admin == null) {
            throw new Exception("Cuenta administrador no encontrada");
        }
        if (producto == null) {
            throw new Exception("Producto inválido");
        }
        if (!dao.agregarProductoACuentaAdmin(admin.getId(), producto)) {
            throw new Exception("Error al agregar producto");
        }
    }

    public void actualizarProducto(@NotEmpty String correoAdmin, Producto producto) throws Exception {
        if (!esAdministrador(correoAdmin)) {
            throw new Exception("Solo administradores pueden actualizar productos");
        }
        Cuenta admin = dao.findByEmail(correoAdmin.trim());
        if (admin == null) {
            throw new Exception("Cuenta administrador no encontrada");
        }
        if (producto == null || producto.getId() == null) {
            throw new Exception("Producto inválido");
        }
        if (!dao.actualizarProductoEnCuentaAdmin(admin.getId(), producto)) {
            throw new Exception("Error al actualizar producto");
        }
    }

    public void eliminarProducto(@NotEmpty String correoAdmin, Integer productoId) throws Exception {
        if (!esAdministrador(correoAdmin)) {
            throw new Exception("Solo administradores pueden eliminar productos");
        }
        Cuenta admin = dao.findByEmail(correoAdmin.trim());
        if (admin == null) {
            throw new Exception("Cuenta administrador no encontrada");
        }
        if (productoId == null) {
            throw new Exception("ID de producto inválido");
        }
        if (!dao.eliminarProductoDeCuentaAdmin(admin.getId(), productoId)) {
            throw new Exception("Error al eliminar producto");
        }
    }
}