package org.unl.pacas.base.controller.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.unl.pacas.base.controller.Utiles;
import org.unl.pacas.base.controller.dao.dao_models.DaoCuenta;
import org.unl.pacas.base.controller.dao.dao_models.DaoProducto;
import org.unl.pacas.base.controller.data_struct.list.LinkedList;
import org.unl.pacas.base.models.CategoriaEnum;
import org.unl.pacas.base.models.Cuenta;
import org.unl.pacas.base.models.Producto;
import org.unl.pacas.base.models.RolEnum;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@BrowserCallable
@AnonymousAllowed
public class ProductoServices {
    private DaoProducto dp;
    private DaoCuenta daoCuenta;

    public ProductoServices() {
        dp = new DaoProducto();
        daoCuenta = new DaoCuenta();
    }

    private boolean esAdministrador(String correoUsuario) {
        try {
            if (correoUsuario == null || correoUsuario.trim().isEmpty()) {
                return false;
            }
            Cuenta cuenta = daoCuenta.findByEmail(correoUsuario.trim());
            return cuenta != null && cuenta.getRol() == RolEnum.ADMINISTRADOR;
        } catch (Exception e) {
            return false;
        }
    }

    public void createProducto(@NotEmpty String nombre, @NotEmpty String descripcion, @NotEmpty String imagen,
                              @NotNull Double precio, @NotNull Integer stock, @NotNull Double pvp, 
                              @NotEmpty String categoria, @NotEmpty String correoUsuario) throws Exception {
        if (!esAdministrador(correoUsuario)) {
            throw new Exception("Solo los administradores pueden crear productos");
        }
        if (nombre == null || nombre.trim().length() < 3) throw new Exception("El nombre debe tener al menos 3 caracteres");
        if (descripcion == null || descripcion.trim().length() < 10) throw new Exception("La descripción debe tener al menos 10 caracteres");
        if (imagen == null || imagen.trim().isEmpty()) throw new Exception("La imagen es obligatoria");
        if (precio == null || precio <= 0) throw new Exception("El precio debe ser mayor a 0");
        if (precio > 10000) throw new Exception("El precio no puede ser mayor a $10,000");
        if (stock == null || stock < 0) throw new Exception("El stock no puede ser negativo");
        if (stock > 1000) throw new Exception("El stock no puede ser mayor a 1000 unidades");
        if (pvp == null || pvp <= 0) throw new Exception("El precio de venta debe ser mayor a 0");
        if (pvp <= precio) throw new Exception("El precio de venta debe ser mayor al precio de compra");
        if (categoria == null || categoria.trim().isEmpty()) throw new Exception("La categoría es obligatoria");

        CategoriaEnum categoriaEnum;
        try {
            categoriaEnum = CategoriaEnum.valueOf(categoria.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Categoría no válida: " + categoria + ". Valores permitidos: " + Arrays.toString(CategoriaEnum.values()));
        }

        if (dp.existsProductWithName(nombre.trim(), null)) throw new Exception("Ya existe un producto con el nombre: " + nombre.trim());

        String imagenLimpia = limpiarNombreImagen(imagen);

        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(nombre.trim());
        nuevoProducto.setDescripcion(descripcion.trim());
        nuevoProducto.setImagen(imagenLimpia);
        nuevoProducto.setPrecio(precio);
        nuevoProducto.setStock(stock);
        nuevoProducto.setPvp(pvp);
        nuevoProducto.setCategoria(categoriaEnum);

        dp.setObj(nuevoProducto);
        if (!dp.save()) throw new Exception("No se pudo guardar el producto en la base de datos");
    }

    public void updateProducto(@NotNull Integer id, @NotEmpty String nombre, @NotEmpty String descripcion,
                              @NotEmpty String imagen, @NotNull Double precio, @NotNull Integer stock,
                              @NotNull Double pvp, @NotEmpty String categoria, @NotEmpty String correoUsuario) throws Exception {
        if (!esAdministrador(correoUsuario)) {
            throw new Exception("Solo los administradores pueden editar productos");
        }
        if (id == null || id <= 0) throw new Exception("ID de producto no válido");
        if (nombre == null || nombre.trim().length() < 3) throw new Exception("El nombre debe tener al menos 3 caracteres");
        if (descripcion == null || descripcion.trim().length() < 10) throw new Exception("La descripción debe tener al menos 10 caracteres");
        if (imagen == null || imagen.trim().isEmpty()) throw new Exception("La imagen es obligatoria");
        if (precio == null || precio <= 0) throw new Exception("El precio debe ser mayor a 0");
        if (precio > 10000) throw new Exception("El precio no puede ser mayor a $10,000");
        if (stock == null || stock < 0) throw new Exception("El stock no puede ser negativo");
        if (stock > 1000) throw new Exception("El stock no puede ser mayor a 1000 unidades");
        if (pvp == null || pvp <= 0) throw new Exception("El precio de venta debe ser mayor a 0");
        if (pvp <= precio) throw new Exception("El precio de venta debe ser mayor al precio de compra");
        if (categoria == null || categoria.trim().isEmpty()) throw new Exception("La categoría es obligatoria");

        CategoriaEnum categoriaEnum;
        try {
            categoriaEnum = CategoriaEnum.valueOf(categoria.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Categoría no válida: " + categoria + ". Valores permitidos: " + Arrays.toString(CategoriaEnum.values()));
        }

        LinkedList<Producto> productos = dp.listAll();
        Integer indice = productos.findIndexById(id);
        if (indice == -1) throw new Exception("Producto no encontrado con ID: " + id);

        if (dp.existsProductWithName(nombre.trim(), id)) throw new Exception("Ya existe otro producto con el nombre: " + nombre.trim());

        String imagenLimpia = limpiarNombreImagen(imagen);

        Producto productoActualizado = new Producto();
        productoActualizado.setId(id);
        productoActualizado.setNombre(nombre.trim());
        productoActualizado.setDescripcion(descripcion.trim());
        productoActualizado.setImagen(imagenLimpia);
        productoActualizado.setPrecio(precio);
        productoActualizado.setStock(stock);
        productoActualizado.setPvp(pvp);
        productoActualizado.setCategoria(categoriaEnum);

        dp.update(productoActualizado, indice);
    }

    public void deleteProducto(@NotNull Integer id, @NotEmpty String correoUsuario) throws Exception {
        if (!esAdministrador(correoUsuario)) {
            throw new Exception("Solo los administradores pueden eliminar productos");
        }
        if (id == null || id <= 0) throw new Exception("ID de producto no válido");
        Producto producto = dp.findById(id);
        if (producto == null) throw new Exception("Producto no encontrado con ID: " + id);
        if (!dp.deleteById(id)) throw new Exception("No se pudo eliminar el producto de la base de datos");
    }

    public String subirImagen(byte[] archivoBytes, @NotEmpty String nombreOriginal, @NotEmpty String correoUsuario) throws Exception {
        if (!esAdministrador(correoUsuario)) {
            throw new Exception("Solo los administradores pueden subir imágenes");
        }
        if (archivoBytes == null || archivoBytes.length == 0) {
            throw new Exception("Archivo de imagen vacío");
        }
        if (nombreOriginal == null || nombreOriginal.trim().isEmpty()) {
            throw new Exception("Nombre de archivo no válido");
        }
        String extension = obtenerExtension(nombreOriginal);
        if (!esExtensionValida(extension)) {
            throw new Exception("Solo se permiten imágenes: jpg, jpeg, png, webp");
        }
        if (archivoBytes.length > 5 * 1024 * 1024) {
            throw new Exception("La imagen no puede ser mayor a 5MB");
        }
        try {
            Path directorioImagenes = Paths.get("src/main/resources/META-INF/resources/imagenes");
            if (!Files.exists(directorioImagenes)) {
                Files.createDirectories(directorioImagenes);
            }
            String nombreLimpio = limpiarNombreImagen(nombreOriginal);
            String nombreUnico = System.currentTimeMillis() + "_" + nombreLimpio;
            Path rutaArchivo = directorioImagenes.resolve(nombreUnico);
            try (FileOutputStream fos = new FileOutputStream(rutaArchivo.toFile())) {
                fos.write(archivoBytes);
            }
            return nombreUnico;
        } catch (Exception e) {
            throw new Exception("Error al guardar la imagen: " + e.getMessage());
        }
    }

    private boolean esExtensionValida(String extension) {
        if (extension == null) return false;
        String ext = extension.toLowerCase();
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("webp");
    }
    
    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) return "";
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1);
    }

    public boolean verificarEsAdmin(@NotEmpty String correoUsuario) {
        return esAdministrador(correoUsuario);
    }

    public List<String> listTipoCategoria() {
        List<String> list = new ArrayList<>();
        for (CategoriaEnum r : CategoriaEnum.values()) list.add(r.toString());
        return list;
    }

    public List<Producto> order(@NotEmpty String columnId, @NotNull Integer dir) throws Exception {
        if (columnId == null || columnId.trim().isEmpty()) throw new Exception("Columna de ordenamiento no válida");
        if (dir == null || (dir != 1 && dir != 2)) throw new Exception("Dirección de ordenamiento no válida (1=ASC, 2=DESC)");
        LinkedList<Producto> productosLista = dp.listAll();
        if (productosLista.isEmpty()) return new ArrayList<>();
        LinkedList<Producto> listaOrdenada = productosLista.quickSort(columnId, dir);
        return Arrays.asList(listaOrdenada.toArray());
    }

    public List<HashMap<String, Object>> buscarBinariaLineal(@NotEmpty String atributo,
                                                            @NotEmpty String valor,
                                                            @NotNull Integer tipo) throws Exception {
        if (atributo == null || atributo.trim().isEmpty()) throw new Exception("Atributo de búsqueda no válido");
        if (valor == null || valor.trim().isEmpty()) throw new Exception("Valor de búsqueda no válido");
        if (tipo == null || (tipo != 1 && tipo != 2)) throw new Exception("Tipo de búsqueda no válido (1=Lineal, 2=Binaria)");
        LinkedList<Producto> productosLista = dp.listAll();
        if (productosLista.isEmpty()) return new ArrayList<>();
        LinkedList<Producto> resultados;
        if (tipo == 1) {
            resultados = productosLista.buscarPorAtributo(atributo, valor.trim());
        } else {
            productosLista.quickSort(atributo, Utiles.ASCENDENTE);
            resultados = productosLista.busquedaLinealBinaria(atributo, valor.trim());
        }
        return convertirProductosAHashMap(resultados);
    }

    public List<HashMap<String, Object>> busqueda(@NotEmpty String atributo,
                                                 @NotEmpty String valor) throws Exception {
        if (atributo == null || atributo.trim().isEmpty()) throw new Exception("Atributo de búsqueda no válido");
        if (valor == null || valor.trim().isEmpty()) throw new Exception("Valor de búsqueda no válido");
        LinkedList<Producto> productosLista = dp.listAll();
        if (productosLista.isEmpty()) return new ArrayList<>();
        LinkedList<Producto> resultados = productosLista.buscarPorAtributo(atributo, valor.trim());
        return convertirProductosAHashMap(resultados);
    }

    public List<Producto> listAll() {
        try {
            LinkedList<Producto> productos = dp.listAll();
            return Arrays.asList(productos.toArray());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Producto getProductoById(@NotNull Integer id) throws Exception {
        if (id == null || id <= 0) throw new Exception("ID de producto no válido");
        Producto producto = dp.findById(id);
        if (producto == null) throw new Exception("Producto no encontrado con ID: " + id);
        return producto;
    }

    public boolean verificarStock(@NotNull Integer id, @NotNull Integer cantidadSolicitada) throws Exception {
        if (id == null || id <= 0) throw new Exception("ID de producto no válido");
        if (cantidadSolicitada == null || cantidadSolicitada <= 0) throw new Exception("Cantidad solicitada no válida");
        Producto producto = dp.findById(id);
        if (producto == null) throw new Exception("Producto no encontrado con ID: " + id);
        return producto.getStock() >= cantidadSolicitada;
    }

    public void actualizarStock(@NotNull Integer id, @NotNull Integer nuevoStock) throws Exception {
        if (id == null || id <= 0) throw new Exception("ID de producto no válido");
        if (nuevoStock == null || nuevoStock < 0) throw new Exception("Stock no puede ser negativo");
        LinkedList<Producto> productos = dp.listAll();
        Integer indice = productos.findIndexById(id);
        if (indice == -1) throw new Exception("Producto no encontrado con ID: " + id);
        Producto producto = productos.get(indice);
        producto.setStock(nuevoStock);
        dp.update(producto, indice);
    }

    private List<HashMap<String, Object>> convertirProductosAHashMap(LinkedList<Producto> productos) {
        List<HashMap<String, Object>> listaFinal = new ArrayList<>();
        for (Producto producto : productos.toArray()) {
            if (producto != null) {
                HashMap<String, Object> productMap = new HashMap<>();
                productMap.put("id", producto.getId());
                productMap.put("nombre", producto.getNombre());
                productMap.put("descripcion", producto.getDescripcion());
                productMap.put("imagen", producto.getImagen());
                productMap.put("precio", producto.getPrecio());
                productMap.put("stock", producto.getStock());
                productMap.put("pvp", producto.getPvp());
                productMap.put("categoria", producto.getCategoria());
                listaFinal.add(productMap);
            }
        }
        return listaFinal;
    }

    private String limpiarNombreImagen(String imagen) {
        if (imagen == null || imagen.trim().isEmpty()) return "";
        String imagenLimpia = imagen.trim();
        if (imagenLimpia.contains("/")) imagenLimpia = imagenLimpia.substring(imagenLimpia.lastIndexOf("/") + 1);
        if (imagenLimpia.contains("\\")) imagenLimpia = imagenLimpia.substring(imagenLimpia.lastIndexOf("\\") + 1);
        imagenLimpia = imagenLimpia.replaceAll("[^a-zA-Z0-9._-]", "");
        return imagenLimpia;
    }
}