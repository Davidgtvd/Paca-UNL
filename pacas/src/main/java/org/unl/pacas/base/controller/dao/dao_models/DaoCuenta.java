package org.unl.pacas.base.controller.dao.dao_models;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.unl.pacas.base.controller.dao.AdapterDao;
import org.unl.pacas.base.controller.data_struct.list.LinkedList;
import org.unl.pacas.base.models.Cuenta;
import org.unl.pacas.base.models.Producto;
import org.unl.pacas.base.models.RolEnum;

import com.google.gson.Gson;

public class DaoCuenta extends AdapterDao<Cuenta> {
    private Cuenta obj;

    private static final String ADMIN_EMAIL = "maria@gmail.com";
    private static final String ADMIN_PASSWORD = "1234";

    public DaoCuenta() {
        super(Cuenta.class);
        createDataDirectory();
        crearAdminPorDefecto();
    }

    private void createDataDirectory() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                System.out.println("✅ Directorio 'data' creado para cuentas");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al crear directorio data: " + e.getMessage());
        }
    }

    private void crearAdminPorDefecto() {
        try {
            LinkedList<Cuenta> cuentas = listAll();
            boolean adminExiste = false;

            for (int i = 0; i < cuentas.getLength(); i++) {
                Cuenta c = cuentas.get(i);
                if (c != null && c.getCorreoElectronico() != null &&
                    c.getCorreoElectronico().equalsIgnoreCase(ADMIN_EMAIL)) {
                    adminExiste = true;
                    System.out.println("✅ Cuenta administrador ya existe");
                    break;
                }
            }

            if (!adminExiste) {
                Cuenta admin = new Cuenta();
                admin.setId(1);
                admin.setCorreoElectronico(ADMIN_EMAIL);
                admin.setClave(ADMIN_PASSWORD);
                admin.setRol(RolEnum.ADMINISTRADOR);
                admin.setId_persona(1);
                admin.setProductos(new ArrayList<>());

                cuentas.add(admin);
                saveList(cuentas);

                System.out.println("✅ Cuenta administrador creada automáticamente");
                System.out.println("   👑 Rol: ADMINISTRADOR");
            }

        } catch (Exception e) {
            System.err.println("❌ Error al crear admin por defecto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Cuenta getObj() {
        if (obj == null)
            this.obj = new Cuenta();
        return this.obj;
    }

    public void setObj(Cuenta obj) {
        this.obj = obj;
    }

    private boolean esCredencialAdmin(String correo, String clave) {
        return ADMIN_EMAIL.equalsIgnoreCase(correo) && ADMIN_PASSWORD.equals(clave);
    }

    public Boolean save() {
        try {
            if (obj == null) {
                System.err.println("❌ Error: Objeto cuenta es null");
                return false;
            }

            if (obj.getCorreoElectronico() == null || obj.getCorreoElectronico().trim().isEmpty()) {
                System.err.println("❌ Error: Correo electrónico es obligatorio");
                return false;
            }

            if (obj.getClave() == null || obj.getClave().trim().isEmpty()) {
                System.err.println("❌ Error: Clave es obligatoria");
                return false;
            }

            if (obj.getRol() == null) {
                System.err.println("❌ Error: Rol es obligatorio");
                return false;
            }

            LinkedList<Cuenta> lista = listAll();

            for (int i = 0; i < lista.getLength(); i++) {
                Cuenta c = lista.get(i);
                if (c != null && c.getCorreoElectronico() != null &&
                    c.getCorreoElectronico().equalsIgnoreCase(obj.getCorreoElectronico().trim())) {
                    System.err.println("❌ Error: Ya existe una cuenta con ese correo");
                    return false;
                }
            }

            int maxId = 0;
            for (int i = 0; i < lista.getLength(); i++) {
                Cuenta c = lista.get(i);
                if (c != null && c.getId() != null && c.getId() > maxId) {
                    maxId = c.getId();
                }
            }
            obj.setId(maxId + 1);

            if (obj.getRol() == RolEnum.ADMINISTRADOR) {
                if (!esCredencialAdmin(obj.getCorreoElectronico(), obj.getClave())) {
                    System.out.println("⚠️ Intento de crear admin con credenciales incorrectas. Cambiando a CLIENTE");
                    obj.setRol(RolEnum.CLIENTE);
                }
            }

            if (obj.getRol() == RolEnum.ADMINISTRADOR && obj.getProductos() == null) {
                obj.setProductos(new ArrayList<>());
            }

            this.persist(obj);

            System.out.println("✅ Cuenta guardada con ID: " + obj.getId());
            System.out.println("   👤 Rol: " + obj.getRol());

            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al guardar cuenta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Boolean update(Integer pos) {
        try {
            if (obj == null) {
                System.err.println("❌ Error: Objeto cuenta es null");
                return false;
            }

            if (pos == null || pos < 0) {
                System.err.println("❌ Error: Posición no válida");
                return false;
            }

            if (obj.getCorreoElectronico() == null || obj.getCorreoElectronico().trim().isEmpty()) {
                System.err.println("❌ Error: Correo electrónico es obligatorio");
                return false;
            }

            if (obj.getClave() == null || obj.getClave().trim().isEmpty()) {
                System.err.println("❌ Error: Clave es obligatoria");
                return false;
            }

            if (obj.getRol() == null) {
                System.err.println("❌ Error: Rol es obligatorio");
                return false;
            }

            LinkedList<Cuenta> lista = listAll();

            if (pos >= lista.getLength()) {
                System.err.println("❌ Error: Posición fuera de rango");
                return false;
            }

            for (int i = 0; i < lista.getLength(); i++) {
                if (i != pos) {
                    Cuenta c = lista.get(i);
                    if (c != null && c.getCorreoElectronico() != null &&
                        c.getCorreoElectronico().equalsIgnoreCase(obj.getCorreoElectronico().trim())) {
                        System.err.println("❌ Error: Ya existe otra cuenta con ese correo");
                        return false;
                    }
                }
            }

            if (obj.getRol() == RolEnum.ADMINISTRADOR) {
                if (!esCredencialAdmin(obj.getCorreoElectronico(), obj.getClave())) {
                    System.out.println("⚠️ Intento de actualizar a admin con credenciales incorrectas. Cambiando a CLIENTE");
                    obj.setRol(RolEnum.CLIENTE);
                }
            }

            if (obj.getRol() == RolEnum.ADMINISTRADOR && obj.getProductos() == null) {
                obj.setProductos(new ArrayList<>());
            }

            this.update(obj, pos);
            System.out.println("✅ Cuenta actualizada en posición: " + pos);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al actualizar cuenta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Boolean delete(int pos) {
        try {
            if (pos < 0) {
                System.err.println("❌ Error: Posición no válida");
                return false;
            }

            LinkedList<Cuenta> lista = listAll();

            if (pos >= lista.getLength()) {
                System.err.println("❌ Error: Posición fuera de rango");
                return false;
            }

            Cuenta cuentaAEliminar = lista.get(pos);
            if (cuentaAEliminar != null && cuentaAEliminar.getCorreoElectronico() != null &&
                cuentaAEliminar.getCorreoElectronico().equalsIgnoreCase(ADMIN_EMAIL)) {
                System.err.println("❌ Error: No se puede eliminar la cuenta administrador");
                return false;
            }

            lista.remove(pos);
            saveList(lista);

            System.out.println("✅ Cuenta eliminada en posición: " + pos);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al eliminar cuenta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Boolean deleteById(Integer id) {
        try {
            if (id == null || id <= 0) {
                System.err.println("❌ Error: ID no válido");
                return false;
            }

            LinkedList<Cuenta> lista = listAll();

            for (int i = 0; i < lista.getLength(); i++) {
                Cuenta c = lista.get(i);
                if (c != null && c.getId() != null && c.getId().equals(id)) {
                    if (c.getCorreoElectronico() != null &&
                        c.getCorreoElectronico().equalsIgnoreCase(ADMIN_EMAIL)) {
                        System.err.println("❌ Error: No se puede eliminar la cuenta administrador");
                        return false;
                    }

                    return delete(i);
                }
            }

            System.err.println("❌ Error: No se encontró cuenta con ID: " + id);
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error en deleteById: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Cuenta findByEmail(String correo) {
        try {
            if (correo == null || correo.trim().isEmpty()) {
                return null;
            }

            LinkedList<Cuenta> lista = listAll();

            for (int i = 0; i < lista.getLength(); i++) {
                Cuenta c = lista.get(i);
                if (c != null && c.getCorreoElectronico() != null &&
                    c.getCorreoElectronico().equalsIgnoreCase(correo.trim())) {
                    return c;
                }
            }

            return null;

        } catch (Exception e) {
            System.err.println("❌ Error en findByEmail: " + e.getMessage());
            return null;
        }
    }

    public Cuenta findById(Integer id) {
        try {
            if (id == null || id <= 0) {
                return null;
            }

            LinkedList<Cuenta> lista = listAll();
            return lista.findById(id);

        } catch (Exception e) {
            System.err.println("❌ Error en findById: " + e.getMessage());
            return null;
        }
    }

    public Cuenta validarCredenciales(String correo, String clave) {
        try {
            if (correo == null || correo.trim().isEmpty() ||
                clave == null || clave.trim().isEmpty()) {
                return null;
            }

            LinkedList<Cuenta> lista = listAll();

            for (int i = 0; i < lista.getLength(); i++) {
                Cuenta c = lista.get(i);
                if (c != null && c.getCorreoElectronico() != null && c.getClave() != null &&
                    c.getCorreoElectronico().equalsIgnoreCase(correo.trim()) &&
                    c.getClave().equals(clave.trim())) {

                    System.out.println("✅ Login exitoso para rol: " + c.getRol());
                    return c;
                }
            }

            System.out.println("❌ Credenciales inválidas");
            return null;

        } catch (Exception e) {
            System.err.println("❌ Error en validarCredenciales: " + e.getMessage());
            return null;
        }
    }

    private HashMap<String, String> toDict(Cuenta cuenta) throws Exception {
        if (cuenta == null) {
            throw new Exception("Cuenta es null");
        }

        HashMap<String, String> aux = new HashMap<>();
        aux.put("id", cuenta.getId() != null ? String.valueOf(cuenta.getId()) : "0");
        aux.put("correoElectronico", cuenta.getCorreoElectronico() != null ? cuenta.getCorreoElectronico() : "");
        aux.put("rol", cuenta.getRol() != null ? String.valueOf(cuenta.getRol()) : "CLIENTE");
        aux.put("id_persona", cuenta.getId_persona() != null ? String.valueOf(cuenta.getId_persona()) : "0");
        return aux;
    }

    public LinkedList<HashMap<String, String>> all() throws Exception {
        LinkedList<HashMap<String, String>> lista = new LinkedList<>();
        LinkedList<Cuenta> cuentas = this.listAll();

        if (!cuentas.isEmpty()) {
            Cuenta[] arreglo = cuentas.toArray();
            for (int i = 0; i < arreglo.length; i++) {
                if (arreglo[i] != null) {
                    lista.add(toDict(arreglo[i]));
                }
            }
        }
        return lista;
    }

    private void saveList(LinkedList<Cuenta> lista) {
        try {
            if (lista == null) {
                System.err.println("❌ Error: Lista es null");
                return;
            }

            Gson gson = new Gson();
            String jsonData = gson.toJson(lista.toArray());

            String fileName = "data" + File.separatorChar + "Cuenta.json";

            FileWriter fw = new FileWriter(fileName, false);
            fw.write(jsonData);
            fw.flush();
            fw.close();

            System.out.println("✅ Lista de cuentas guardada con " + lista.getLength() + " elementos");

        } catch (Exception e) {
            System.err.println("❌ Error al guardar lista de cuentas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void printListStatus() {
        try {
            LinkedList<Cuenta> lista = listAll();
            System.out.println("=== ESTADO DE LA LISTA DE CUENTAS ===");
            System.out.println("📊 Total cuentas: " + lista.getLength());
            System.out.println("❓ ¿Está vacía?: " + lista.isEmpty());

            if (!lista.isEmpty()) {
                System.out.println("📋 Cuentas:");
                for (int i = 0; i < lista.getLength(); i++) {
                    Cuenta c = lista.get(i);
                    if (c != null) {
                        System.out.println("  " + (i + 1) + ": ID=" + c.getId() +
                                         ", Correo=" + c.getCorreoElectronico() +
                                         ", Rol=" + c.getRol());
                    }
                }
            }
            System.out.println("=====================================");

        } catch (Exception e) {
            System.err.println("❌ Error al imprimir estado: " + e.getMessage());
        }
    }

    public HashMap<String, Integer> getEstadisticasPorRol() {
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("ADMINISTRADOR", 0);
        stats.put("CLIENTE", 0);

        try {
            LinkedList<Cuenta> lista = listAll();

            for (int i = 0; i < lista.getLength(); i++) {
                Cuenta c = lista.get(i);
                if (c != null && c.getRol() != null) {
                    String rol = c.getRol().toString();
                    stats.put(rol, stats.get(rol) + 1);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error al obtener estadísticas: " + e.getMessage());
        }

        return stats;
    }

    // Métodos para manejar productos en cuenta administrador

    public boolean agregarProductoACuentaAdmin(Integer cuentaId, Producto producto) {
        try {
            if (cuentaId == null || producto == null) return false;

            LinkedList<Cuenta> lista = listAll();
            for (int i = 0; i < lista.getLength(); i++) {
                Cuenta c = lista.get(i);
                if (c != null && c.getId() != null && c.getId().equals(cuentaId) && c.esAdministrador()) {
                    List<Producto> productos = c.getProductos();
                    if (productos == null) {
                        productos = new ArrayList<>();
                        c.setProductos(productos);
                    }
                    productos.add(producto);
                    saveList(lista);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error al agregar producto a cuenta admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarProductoEnCuentaAdmin(Integer cuentaId, Producto productoActualizado) {
        try {
            if (cuentaId == null || productoActualizado == null || productoActualizado.getId() == null) return false;

            LinkedList<Cuenta> lista = listAll();
            for (int i = 0; i < lista.getLength(); i++) {
                Cuenta c = lista.get(i);
                if (c != null && c.getId() != null && c.getId().equals(cuentaId) && c.esAdministrador()) {
                    List<Producto> productos = c.getProductos();
                    if (productos != null) {
                        for (int j = 0; j < productos.size(); j++) {
                            Producto p = productos.get(j);
                            if (p != null && p.getId() != null && p.getId().equals(productoActualizado.getId())) {
                                productos.set(j, productoActualizado);
                                saveList(lista);
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar producto en cuenta admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarProductoDeCuentaAdmin(Integer cuentaId, Integer productoId) {
        try {
            if (cuentaId == null || productoId == null) return false;

            LinkedList<Cuenta> lista = listAll();
            for (int i = 0; i < lista.getLength(); i++) {
                Cuenta c = lista.get(i);
                if (c != null && c.getId() != null && c.getId().equals(cuentaId) && c.esAdministrador()) {
                    List<Producto> productos = c.getProductos();
                    if (productos != null) {
                        boolean removed = productos.removeIf(p -> p != null && productoId.equals(p.getId()));
                        if (removed) {
                            saveList(lista);
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar producto de cuenta admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}