package org.unl.pacas.base.controller.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.unl.pacas.base.controller.dao.dao_models.DaoCompra;
import org.unl.pacas.base.models.Compra;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@BrowserCallable
@AnonymousAllowed
public class CompraService {
    private DaoCompra dc;

    public CompraService(){
        dc = new DaoCompra();
    }

    public void createCompra(@NotNull double subtotal, @NotEmpty String nroFactura, @NotNull double iva, @NotNull double total, @NotNull Integer id_producto) throws Exception {
        if (subtotal > 0 && nroFactura.trim().length() > 0 && iva > 0 && total > 0 && id_producto != null){
            dc.getObj().setId(dc.listAll().getLength() + 1);
            dc.getObj().setSubtotal(subtotal);
            dc.getObj().setNroFactura(nroFactura);
            dc.getObj().setIva(iva);
            dc.getObj().setTotal(total);
            if (!dc.save())
                throw new Exception("No se pudo guardar la compra");
        }
    }

    public void updateCompra(@NotNull Integer id, @NotNull double subtotal, @NotEmpty String nroFactura, @NotNull double iva, @NotNull double total, @NotNull Integer id_producto) throws Exception {
        if (subtotal > 0 && nroFactura.trim().length() > 0 && iva > 0 && total > 0 && id_producto != null){
            dc.getObj().setId(dc.listAll().getLength() + 1);
            dc.getObj().setSubtotal(subtotal);
            dc.getObj().setNroFactura(nroFactura);
            dc.getObj().setIva(iva);
            dc.getObj().setTotal(total);
            if (!dc.save())
                throw new Exception("No se puso actualizar la compra");
        }
    }

    public List<Compra> listAllCompra() {
        return List.of(dc.listAll().toArray());
    }

    public List<HashMap<String, String>> listCompra() {
        List<HashMap<String, String>> lista = new ArrayList<>();
        if (!dc.listAll().isEmpty()) {
            for (Compra compra : dc.listAll().toArray()) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", compra.getId().toString());
                aux.put("subtotal", compra.getSubtotal().toString());
                aux.put("nroFactura", compra.getNroFactura().toString());
                aux.put("iva", compra.getIva().toString());
                aux.put("total", compra.getTotal().toString());
                
                lista.add(aux);
            }
        }
        return lista;
    }
}    