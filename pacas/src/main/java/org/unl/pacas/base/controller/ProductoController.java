package org.unl.pacas.base.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unl.pacas.base.controller.services.ProductoServices;
import org.unl.pacas.base.models.Producto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoServices productoServices = new ProductoServices();

    @GetMapping
    public ResponseEntity<List<Producto>> listAll() {
        try {
            List<Producto> productos = productoServices.listAll();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createProducto(
            @RequestBody ProductoRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            if (request.getCorreoUsuario() == null || request.getCorreoUsuario().trim().isEmpty()) {
                response.put("message", "❌ Correo de usuario requerido");
                response.put("status", "error");
                return ResponseEntity.status(400).body(response);
            }

            productoServices.createProducto(
                request.getNombre(),
                request.getDescripcion(),
                request.getImagen(),
                request.getPrecio(),
                request.getStock(),
                request.getPvp(),
                request.getCategoria(),
                request.getCorreoUsuario()
            );
            
            response.put("message", "✅ Producto creado correctamente");
            response.put("status", "success");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("message", "❌ Error: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateProducto(
            @PathVariable Integer id, 
            @RequestBody ProductoRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            if (request.getCorreoUsuario() == null || request.getCorreoUsuario().trim().isEmpty()) {
                response.put("message", "❌ Correo de usuario requerido");
                response.put("status", "error");
                return ResponseEntity.status(400).body(response);
            }

            productoServices.updateProducto(
                id,
                request.getNombre(),
                request.getDescripcion(),
                request.getImagen(),
                request.getPrecio(),
                request.getStock(),
                request.getPvp(),
                request.getCategoria(),
                request.getCorreoUsuario()
            );
            
            response.put("message", "✅ Producto actualizado correctamente");
            response.put("status", "success");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("message", "❌ Error: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProducto(
            @PathVariable Integer id,
            @RequestParam String correoUsuario) {
        Map<String, String> response = new HashMap<>();
        try {
            if (correoUsuario == null || correoUsuario.trim().isEmpty()) {
                response.put("message", "❌ Correo de usuario requerido");
                response.put("status", "error");
                return ResponseEntity.status(400).body(response);
            }

            productoServices.deleteProducto(id, correoUsuario);
            
            response.put("message", "✅ Producto eliminado correctamente");
            response.put("status", "success");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("message", "❌ Error: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/subir-imagen")
    public ResponseEntity<Map<String, String>> subirImagen(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("correoUsuario") String correoUsuario) {
        Map<String, String> response = new HashMap<>();
        try {
            if (correoUsuario == null || correoUsuario.trim().isEmpty()) {
                response.put("message", "❌ Correo de usuario requerido");
                response.put("status", "error");
                return ResponseEntity.status(400).body(response);
            }
            if (archivo.isEmpty()) {
                response.put("message", "❌ Archivo de imagen requerido");
                response.put("status", "error");
                return ResponseEntity.status(400).body(response);
            }
            byte[] bytes = archivo.getBytes();
            String nombreImagen = productoServices.subirImagen(
                bytes, 
                archivo.getOriginalFilename(), 
                correoUsuario
            );
            response.put("message", "✅ Imagen subida correctamente");
            response.put("status", "success");
            response.put("nombreImagen", nombreImagen);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "❌ Error: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/verificar-admin")
    public ResponseEntity<Map<String, Object>> verificarAdmin(
            @RequestParam String correoUsuario) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean esAdmin = productoServices.verificarEsAdmin(correoUsuario);
            response.put("esAdmin", esAdmin);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "❌ Error: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Integer id) {
        try {
            Producto producto = productoServices.getProductoById(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<HashMap<String, Object>>> buscarProductos(
            @RequestParam String atributo,
            @RequestParam String valor) {
        try {
            List<HashMap<String, Object>> productos = productoServices.busqueda(atributo, valor);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/ordenar")
    public ResponseEntity<List<Producto>> ordenarProductos(
            @RequestParam String columna,
            @RequestParam Integer direccion) {
        try {
            List<Producto> productos = productoServices.order(columna, direccion);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}/verificar-stock")
    public ResponseEntity<Map<String, Object>> verificarStock(
            @PathVariable Integer id,
            @RequestParam Integer cantidad) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean disponible = productoServices.verificarStock(id, cantidad);
            response.put("disponible", disponible);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "❌ Error: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Map<String, String>> actualizarStock(
            @PathVariable Integer id,
            @RequestParam Integer nuevoStock) {
        Map<String, String> response = new HashMap<>();
        try {
            productoServices.actualizarStock(id, nuevoStock);
            response.put("message", "✅ Stock actualizado correctamente");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "❌ Error: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listCategorias() {
        try {
            List<String> categorias = productoServices.listTipoCategoria();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}

class ProductoRequest {
    private String nombre;
    private String descripcion;
    private String imagen;
    private Double precio;
    private Integer stock;
    private Double pvp;
    private String categoria;
    private String correoUsuario;

    public ProductoRequest() {}

    public ProductoRequest(String nombre, String descripcion, String imagen, Double precio, 
                          Integer stock, Double pvp, String categoria, String correoUsuario) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.precio = precio;
        this.stock = stock;
        this.pvp = pvp;
        this.categoria = categoria;
        this.correoUsuario = correoUsuario;
    }

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

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getCorreoUsuario() { return correoUsuario; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }
}