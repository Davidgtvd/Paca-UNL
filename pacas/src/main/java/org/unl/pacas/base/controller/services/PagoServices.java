package org.unl.pacas.base.controller.services;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.unl.pacas.base.controller.PagoController;
import org.unl.pacas.base.controller.dao.dao_models.DaoPago;
import org.unl.pacas.base.models.MetodoPagoEnum;
import org.unl.pacas.base.models.Pago;

@BrowserCallable
@Transactional(propagation = Propagation.REQUIRES_NEW)
@AnonymousAllowed
public class PagoServices {

    private DaoPago dp;

    public PagoServices() {
        dp = new DaoPago();
    }

    public void createPago(@NotEmpty String codigo_seguridad, @NotEmpty String metodoPago, Boolean estado) throws Exception {
        if (codigo_seguridad.trim().length() > 0 && metodoPago.length() > 0 && estado != null) {
            dp.getObj().setCodigo_seguridad(codigo_seguridad);
            dp.getObj().setMetodoPago(MetodoPagoEnum.valueOf(metodoPago));
            dp.getObj().setEstado(estado);
            if (!dp.save()) {
                throw new Exception("No se pudo guardar los datos de Pago");
            }
        } else {
            throw new Exception("Datos incompletos para crear el pago");
        }
    }

    public void updatePago(Integer id, @NotEmpty String codigo_seguridad, @NotEmpty String metodoPago, Boolean estado) throws Exception {
        if (id != null && codigo_seguridad.trim().length() > 0 && metodoPago.length() > 0 && estado != null) {
            dp.setObj(dp.listAll().get(id));
            dp.getObj().setCodigo_seguridad(codigo_seguridad);
            dp.getObj().setMetodoPago(MetodoPagoEnum.valueOf(metodoPago));
            dp.getObj().setEstado(estado);
            if (!dp.save()) {
                throw new Exception("No se pudo guardar los datos de Pago");
            }
        } else {
            throw new Exception("Datos incompletos para crear el pago");
        }
    }
    
    public List<Pago> listAllPagos() {
        return List.of(dp.listAll().toArray());
    }

    public List<String> listMetodoPago() {
        List<String> list = new ArrayList<>();
        for (MetodoPagoEnum m : MetodoPagoEnum.values()) {
            list.add(m.toString());
        }
        return list;
    }

    public void crearPago(Boolean estado, MetodoPagoEnum metodoPago) throws Exception {
        if (estado == null || metodoPago == null) {
            throw new Exception("Datos incompletos para crear el pago");
        }

        dp.getObj().setId(dp.listAll().getLength() + 1);
        dp.getObj().setCodigo_seguridad("AUTO" + System.currentTimeMillis());
        dp.getObj().setMetodoPago(metodoPago);
        dp.getObj().setEstado(estado);
        
        if (!dp.save()) {
            throw new Exception("No se pudo guardar el pago");
        }
    }

    public Map<String, Object> checkout(float total, String currency) {
        try {
            HashMap<String, Object> response = new PagoController().request(total, currency);
            return response;
        } catch (Exception e) {
            return Map.of("estado", "false", "error", e.getMessage());
        }
    }

    public HashMap<String, Object> consultarEstadoPago(String idCheckout) throws IOException {
        PagoController pagoControl = new PagoController();
        return pagoControl.requestPay(idCheckout);
    }
}