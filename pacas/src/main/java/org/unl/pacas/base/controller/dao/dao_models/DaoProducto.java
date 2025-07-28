package org.unl.pacas.base.controller.dao.dao_models;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.unl.pacas.base.controller.dao.AdapterDao;
import org.unl.pacas.base.controller.data_struct.list.LinkedList;
import org.unl.pacas.base.models.Producto;

import com.google.gson.Gson;

public class DaoProducto extends AdapterDao<Producto> {
    private Producto obj;

    public DaoProducto() {
        super(Producto.class);
        createImageDirectory();
    }

    private void createImageDirectory() {
        try {
            Path imageDir = Paths.get("src/main/resources/META-INF/resources/imagenes");
            if (!Files.exists(imageDir)) Files.createDirectories(imageDir);
            Path staticDir = Paths.get("src/main/resources/static/imagenes");
            if (!Files.exists(staticDir)) Files.createDirectories(staticDir);
        } catch (Exception e) {
            System.err.println("⚠️ Error al crear directorio de imágenes: " + e.getMessage());
        }
    }

    public Producto getObj() {
        if (obj == null) obj = new Producto();
        return obj;
    }

    public void setObj(Producto obj) {
        this.obj = obj;
    }

    public boolean save() {
        try {
            if (obj == null || !obj.isValid()) return false;
            LinkedList<Producto> lista = listAll();
            int maxId = 0;
            for (int i = 0; i < lista.getLength(); i++) {
                Producto p = lista.get(i);
                if (p != null && p.getId() != null && p.getId() > maxId) maxId = p.getId();
            }
            obj.setId(maxId + 1);
            lista.add(obj);
            saveList(lista);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void update(Producto producto, Integer pos) {
        try {
            if (producto == null || !producto.isValid() || pos == null || pos < 0) return;
            LinkedList<Producto> lista = listAll();
            if (pos >= lista.getLength()) return;
            lista.update(producto, pos);
            saveList(lista);
        } catch (Exception e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean deleteById(Integer id) {
        try {
            if (id == null || id <= 0) return false;
            LinkedList<Producto> lista = listAll();
            boolean eliminado = lista.delete_by_id(id);
            if (eliminado) {
                saveList(lista);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public LinkedList<Producto> listAll() {
        try {
            String fileName = "data" + File.separatorChar + "Producto.json";
            File dataFile = new File(fileName);
            if (!dataFile.exists()) {
                return new LinkedList<>();
            }
            String jsonData = new String(Files.readAllBytes(dataFile.toPath()));
            Gson gson = new Gson();
            Producto[] productosArray = gson.fromJson(jsonData, Producto[].class);
            LinkedList<Producto> lista = new LinkedList<>();
            if (productosArray != null) {
                for (Producto p : productosArray) {
                    lista.add(p);
                }
            }
            return lista;
        } catch (Exception e) {
            System.err.println("Error al listar productos: " + e.getMessage());
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    private HashMap<String, String> toDict(Producto arr) throws Exception {
        if (arr == null) throw new Exception("Producto es null");
        HashMap<String, String> aux = new HashMap<>();
        aux.put("id", arr.getId() != null ? String.valueOf(arr.getId()) : "0");
        aux.put("nombre", arr.getNombre() != null ? arr.getNombre() : "");
        aux.put("descripcion", arr.getDescripcion() != null ? arr.getDescripcion() : "");
        aux.put("precio", arr.getPrecio() != null ? String.valueOf(arr.getPrecio()) : "0.0");
        aux.put("stock", arr.getStock() != null ? String.valueOf(arr.getStock()) : "0");
        aux.put("pvp", arr.getPvp() != null ? String.valueOf(arr.getPvp()) : "0.0");
        aux.put("categoria", arr.getCategoria() != null ? String.valueOf(arr.getCategoria()) : "");
        aux.put("imagen", arr.getImagen() != null ? arr.getImagen() : "");
        return aux;
    }

    public LinkedList<HashMap<String, String>> all() throws Exception {
        LinkedList<HashMap<String, String>> lista = new LinkedList<>();
        LinkedList<Producto> productos = this.listAll();
        if (!productos.isEmpty()) {
            Producto[] arreglo = productos.toArray();
            for (Producto producto : arreglo) {
                if (producto != null) lista.add(toDict(producto));
            }
        }
        return lista;
    }

    public Producto findById(Integer id) {
        try {
            if (id == null || id <= 0) return null;
            LinkedList<Producto> lista = listAll();
            return lista.findById(id);
        } catch (Exception e) {
            System.err.println("Error en findById producto: " + e.getMessage());
            return null;
        }
    }

    private void saveList(LinkedList<Producto> lista) {
        try {
            if (lista == null) return;
            Gson gson = new Gson();
            String jsonData = gson.toJson(lista.toArray());
            String fileName = "data" + File.separatorChar + "Producto.json";
            File dataDir = new File("data");
            if (!dataDir.exists()) dataDir.mkdirs();
            FileWriter fw = new FileWriter(fileName, false);
            fw.write(jsonData);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            System.err.println("Error al guardar lista de productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean existsProductWithName(String nombre, Integer excludeId) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) return false;
            LinkedList<Producto> lista = listAll();
            for (int i = 0; i < lista.getLength(); i++) {
                Producto p = lista.get(i);
                if (p != null && p.getNombre() != null &&
                    p.getNombre().equalsIgnoreCase(nombre.trim()) &&
                    (excludeId == null || !p.getId().equals(excludeId))) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en existsProductWithName: " + e.getMessage());
            return false;
        }
    }
}