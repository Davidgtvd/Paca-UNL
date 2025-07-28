package org.unl.pacas.base.controller.dao.dao_models;

import java.util.HashMap;

import org.unl.pacas.base.controller.dao.AdapterDao;
import org.unl.pacas.base.controller.data_struct.list.LinkedList;
import org.unl.pacas.base.models.Compra;
import org.unl.pacas.base.models.DetalleFactura;
import org.unl.pacas.base.models.Persona;
import org.unl.pacas.base.models.Producto;

import org.unl.pacas.base.controller.dao.dao_models.DaoCompra;
import org.unl.pacas.base.controller.dao.dao_models.DaoPersona;

public class DaoDetalleFactura extends AdapterDao<DetalleFactura> {
    private DetalleFactura obj;

    public DaoDetalleFactura() {
        super(DetalleFactura.class);
    }

    public DetalleFactura getObj() {
        if (obj == null)
            this.obj = new DetalleFactura();
        return this.obj;
    }

    public void setObj(DetalleFactura obj) {
        this.obj = obj;
    }

    public Boolean save() {
        try {
            obj.setId(listAll().getLength() + 1);
            this.persist(obj);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    public Boolean update(Integer pos) {
        try {
            this.update(obj, pos); // obj.getId
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public LinkedList<HashMap<String, String>> all() throws Exception {
        LinkedList<HashMap<String, String>> lista = new LinkedList<>();
        if (!this.listAll().isEmpty()) {
            DetalleFactura[] arreglo = this.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {

                lista.add(toDict(arreglo[i]));
            }
        }
        return lista;
    }

    /*
     * private HashMap<String, String> toDict(DetalleFactura arreglo) throws
     * Exception {
     * DaoProducto dp = new DaoProducto();
     * DaoPersona db = new DaoPersona();
     * HashMap<String, String> aux = new HashMap<>();
     * aux.put("id", arreglo.getId().toString());
     * //aux.put("nombre", arreglo.getNombre().toString());
     * aux.put("producto", dp.get(arreglo.getId_producto()).getNombre());
     * aux.put("compra", db.get(arreglo.getId_compra()).getNombre());
     * return aux;
     * }
     */

    private HashMap<String, String> toDict(DetalleFactura detalle) throws Exception {
        DaoProducto dp = new DaoProducto();
        HashMap<String, String> aux = new HashMap<>();

        Producto producto = dp.get(detalle.getId_producto());
        aux.put("id", detalle.getId().toString());
        aux.put("id_producto", detalle.getId_producto().toString());
        aux.put("nombre", producto.getNombre());
        aux.put("pvp", producto.getPrecio().toString());
        aux.put("cantidad", detalle.getCantidad().toString());
        return aux;
    }

    /// lista de factura
    public LinkedList<HashMap<String, String>> allFacturas() throws Exception {
        LinkedList<HashMap<String, String>> lista = new LinkedList<>();
        DaoCompra daoCompra = new DaoCompra();
        DaoPersona daoPersona = new DaoPersona();

        if (!daoCompra.listAll().isEmpty()) {
            Compra[] compras = daoCompra.listAll().toArray();
            for (Compra compra : compras) {
                HashMap<String, String> aux = new HashMap<>();
                Persona persona = daoPersona.get(compra.getId_persona());

                aux.put("nro", compra.getId().toString());
                aux.put("nroFactura", compra.getNroFactura());
                aux.put("fecha", ""); 
                aux.put("nombrePersona", persona.getNombre() + " " + persona.getApellido());
                aux.put("subtotal", compra.getSubtotal().toString());
                aux.put("iva", compra.getIva().toString());
                aux.put("total", compra.getTotal().toString());

                lista.add(aux);
            }
        }
        return lista;
    }

    public static void main(String[] args) {
        DaoDetalleFactura da = new DaoDetalleFactura();
        da.getObj().setId(da.listAll().getLength() + 1);
        da.getObj().setCantidad(3);
        da.getObj().setPrecioUnitario(25.50);
        da.getObj().setTotal(76.50); // cantidad * precioUnitario
        da.getObj().setId_compra(1);
        da.getObj().setId_producto(2);

        if (da.save()) {
            System.out.println("Detalle de factura guardado");
        } else {
            System.out.println("Error al guardar detalle de factura");
        }
    }
}