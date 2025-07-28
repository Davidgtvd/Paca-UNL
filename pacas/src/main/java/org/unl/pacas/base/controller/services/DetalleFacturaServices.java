package org.unl.pacas.base.controller.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.unl.pacas.base.controller.dao.dao_models.DaoCompra;
import org.unl.pacas.base.controller.dao.dao_models.DaoDetalleFactura;
import org.unl.pacas.base.controller.dao.dao_models.DaoProducto;
import org.unl.pacas.base.controller.dao.dao_models.DaoPersona;
import org.unl.pacas.base.controller.data_struct.list.LinkedList;
import org.unl.pacas.base.models.Compra;
import org.unl.pacas.base.models.DetalleFactura;
import org.unl.pacas.base.models.Persona;
import org.unl.pacas.base.models.Producto;

//import org.unl.pacas.base.controller.services.*;


import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;

@BrowserCallable
@AnonymousAllowed
public class DetalleFacturaServices {
    private DaoDetalleFactura df;
    private DaoProducto dp;
    private DaoPersona db;
    private DaoCompra dc;

    public DetalleFacturaServices(){
        df = new DaoDetalleFactura();
        dp = new DaoProducto();
    }

    /**
     * Crear detalle de factura
     */
    public void createDetalleFactura(@NotNull Double total, @NotNull Integer cantidad, @NotNull Double precioUnitario, 
                                   @NotNull Integer id_compra, @NotNull Integer id_producto, @NotNull Integer id_persona) throws Exception {
        if (total > 0 && cantidad > 0 && precioUnitario > 0 && id_compra != null && id_producto != null && id_persona != null){
            // Verificar que el producto existe
            Producto producto = dp.findById(id_producto);
            if (producto == null) {
                throw new Exception("Producto no encontrado con ID: " + id_producto);
            }
            
            df.getObj().setTotal(total);
            df.getObj().setCantidad(cantidad);
            df.getObj().setPrecioUnitario(precioUnitario);
            df.getObj().setId_compra(id_compra);
            df.getObj().setId_producto(id_producto);
            
            if (!df.save())
                throw new Exception("No se pudo guardar los datos del detalle de factura");
        } else {
            throw new Exception("Todos los campos son obligatorios y deben tener valores válidos");
        }
    }

    /**
     * Actualizar detalle de factura
     */
    public void updateDetalleFactura(@NotNull Integer id, @NotNull Double total, @NotNull Integer cantidad, 
                                   @NotNull Double precioUnitario, @NotNull Integer id_compra, 
                                   @NotNull Integer id_producto, @NotNull Integer id_persona) throws Exception {
        if (id != null && total > 0 && cantidad > 0 && precioUnitario > 0 && id_compra != null && id_producto != null && id_persona != null){
            
            LinkedList<DetalleFactura> detalles = df.listAll();
            Integer indice = detalles.findIndexById(id);
            
            if (indice != -1) {
                // Verificar que el producto existe
                Producto producto = dp.findById(id_producto);
                if (producto == null) {
                    throw new Exception("Producto no encontrado con ID: " + id_producto);
                }
                
                DetalleFactura detalleActualizado = new DetalleFactura();
                detalleActualizado.setId(id);
                detalleActualizado.setTotal(total);
                detalleActualizado.setCantidad(cantidad);
                detalleActualizado.setPrecioUnitario(precioUnitario);
                detalleActualizado.setId_compra(id_compra);
                detalleActualizado.setId_producto(id_producto);
                
                df.setObj(detalleActualizado);
                if (!df.update(indice)) {
                    throw new Exception("No se pudo actualizar el detalle de factura con ID: " + id);
                }
            } else {
                throw new Exception("Detalle de factura no encontrado con ID: " + id);
            }
        } else {
            throw new Exception("Todos los campos son obligatorios y deben tener valores válidos");
        }
    }

    /**
     * Eliminar detalle de factura
     */
    public void deleteDetalleFactura(@NotNull Integer id) throws Exception {
        if (id != null && id > 0) {
            LinkedList<DetalleFactura> detalles = df.listAll();
            Integer indice = detalles.findIndexById(id);
            
            if (indice != -1) {
                detalles.delete(indice);
            } else {
                throw new Exception("Detalle de factura no encontrado con ID: " + id);
            }
        } else {
            throw new Exception("ID no válido");
        }
    }

    /**
     * Listar todos los detalles con información del producto
     */
    public List<DetalleFactura> listAll() {
        try {
            LinkedList<DetalleFactura> detalles = df.listAll();
            LinkedList<Producto> productos = dp.listAll();
            
            List<DetalleFactura> detallesEnriquecidos = new ArrayList<>();
            
            for (DetalleFactura detalle : detalles.toArray()) {
                // Buscar el producto correspondiente
                Producto producto = null;
                for (Producto p : productos.toArray()) {
                    if (p.getId().equals(detalle.getId_producto())) {
                        producto = p;
                        break;
                    }
                }
                
                // Crear un nuevo detalle con información del producto
                DetalleFactura detalleEnriquecido = new DetalleFactura();
                detalleEnriquecido.setId(detalle.getId());
                detalleEnriquecido.setTotal(detalle.getTotal());
                detalleEnriquecido.setCantidad(detalle.getCantidad());
                detalleEnriquecido.setPrecioUnitario(detalle.getPrecioUnitario());
                detalleEnriquecido.setId_compra(detalle.getId_compra());
                detalleEnriquecido.setId_producto(detalle.getId_producto());

                
                // Agregar información del producto si existe
                if (producto != null) {
                }
                
                detallesEnriquecidos.add(detalleEnriquecido);
            }
            
            return detallesEnriquecidos;
            
        } catch (Exception e) {
            System.err.println("Error en listAll: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Ordenar detalles por columna
     */
    public List<DetalleFactura> order(@NotEmpty String columnId, @NotNull Integer dir) throws Exception {
        try {
            LinkedList<DetalleFactura> detallesLista = df.listAll();
            if (detallesLista.isEmpty()) {
                return new ArrayList<>();
            }

            LinkedList<DetalleFactura> listaOrdenada = detallesLista.quickSort(columnId, dir);
            return Arrays.asList(listaOrdenada.toArray());

        } catch (Exception e) {
            System.err.println("Error en ordenamiento de detalles: " + e.getMessage());
            throw new Exception("Error al ordenar detalles: " + e.getMessage());
        }
    }

    /**
     * Búsqueda en detalles de factura
     */
    public List<DetalleFactura> buscarBinariaLineal(@NotEmpty @NotBlank String atributo, 
                                                  @NotBlank @NotEmpty String valor, 
                                                  @NotNull Integer tipo) throws Exception {
        try {
            LinkedList<DetalleFactura> detallesLista = df.listAll();
            if (detallesLista.isEmpty()) {
                return new ArrayList<>();
            }
            
            LinkedList<DetalleFactura> resultados;
            
            if (tipo == 1) { // Búsqueda lineal
                resultados = detallesLista.buscarPorAtributo(atributo, valor);
            } else { // Búsqueda binaria
                detallesLista.quickSort(atributo, 1); // Ordenar primero
                resultados = detallesLista.busquedaLinealBinaria(atributo, valor);
            }
            
            return Arrays.asList(resultados.toArray());
            
        } catch (Exception e) {
            System.err.println("Error en búsqueda de detalles: " + e.getMessage());
            throw new Exception("Error al realizar la búsqueda: " + e.getMessage());
        }
    }

   /* public List<HashMap> listAll() throws Exception{
        
        return Arrays.asList(df.all().toArray());
    }*/

    public List<HashMap<String, String>> listarFacturas() throws Exception {
    return Arrays.asList(df.allFacturas().toArray());
}

}
  
    

   

    /**
     * Obtener precio de producto por índice
     */
   /* public Double obtenerPrecioProducto(@NotNull Integer indice) {
        try {
            LinkedList<Producto> productos = dp.listAll();
            if (indice >= 0 && indice < productos.getLength()) {
                return productos.get(indice).getPrecio();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener precio del producto: " + e.getMessage());
        }
        return 0.0;
    }

    public HashMap<String, Object> obtenerInfoProductoPorId(@NotNull Integer idProducto) {
        HashMap<String, Object> info = new HashMap<>();
        try {
            Producto producto = dp.findById(idProducto);
            if (producto != null) {
                info.put("id", producto.getId());
                info.put("nombre", producto.getNombre());
                info.put("descripcion", producto.getDescripcion());
                info.put("imagen", producto.getImagen());
                info.put("precio", producto.getPrecio());
                info.put("stock", producto.getStock());
                info.put("pvp", producto.getPvp());
                info.put("categoria", producto.getCategoria().toString());
            } else {
                info.put("error", "Producto no encontrado");
            }
        } catch (Exception e) {
            System.err.println("Error al obtener información del producto: " + e.getMessage());
            info.put("error", "Error al cargar producto");
        }
        return info;
    }*/
