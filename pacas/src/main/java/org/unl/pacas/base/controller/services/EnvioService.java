package org.unl.pacas.base.controller.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.unl.pacas.base.controller.dao.dao_models.DaoEnvio;
import org.unl.pacas.base.models.Envio;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@BrowserCallable
@AnonymousAllowed
public class EnvioService {
    private DaoEnvio de;

    public EnvioService() {
        de = new DaoEnvio();
    }

    public void createEnvio(
            @NotEmpty String calle,
            @NotEmpty String ciudad,
            @NotEmpty String provincia,
            @NotEmpty String referencia,
            @NotNull Integer id_compra
    ) throws Exception {
        if (calle.trim().length() > 0 && ciudad.trim().length() > 0 && provincia.trim().length() > 0 && referencia.trim().length() > 0 && id_compra != null) {
            de.getObj().setId(de.listAll().getLength() + 1);
            de.getObj().setCalle(calle);
            de.getObj().setCiudad(ciudad);
            de.getObj().setProvincia(provincia);
            de.getObj().setReferencia(referencia);
            de.getObj().setId_compra(id_compra);
            if (!de.save())
                throw new Exception("No se pudo guardar el envío");
        }
    }

    public void updateEnvio(
            @NotNull Integer id,
            @NotEmpty String calle,
            @NotEmpty String ciudad,
            @NotEmpty String provincia,
            @NotEmpty String referencia,
            @NotNull Integer id_compra
    ) throws Exception {
        // Buscar la posición del envío por ID
        int pos = -1;
        Envio[] envios = de.listAll().toArray();
        for (int i = 0; i < envios.length; i++) {
            if (envios[i].getId().equals(id)) {
                pos = i;
                break;
            }
        }
        if (pos == -1) throw new Exception("No se encontró el envío para actualizar");

        de.getObj().setId(id);
        de.getObj().setCalle(calle);
        de.getObj().setCiudad(ciudad);
        de.getObj().setProvincia(provincia);
        de.getObj().setReferencia(referencia);
        de.getObj().setId_compra(id_compra);
        if (!de.update(pos))
            throw new Exception("No se pudo actualizar el envío");
    }

    public List<Envio> listAllEnvio() {
        return List.of(de.listAll().toArray());
    }

    public List<HashMap<String, String>> listEnvio() {
        List<HashMap<String, String>> lista = new ArrayList<>();
        if (!de.listAll().isEmpty()) {
            for (Envio envio : de.listAll().toArray()) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", envio.getId().toString());
                aux.put("calle", envio.getCalle());
                aux.put("ciudad", envio.getCiudad());
                aux.put("provincia", envio.getProvincia());
                aux.put("referencia", envio.getReferencia());
                aux.put("id_compra", envio.getId_compra() != null ? envio.getId_compra().toString() : "");
                lista.add(aux);
            }
        }
        return lista;
    }

    // Método extra: listar envíos por id_compra (útil para compradores)
    public List<Envio> listEnvioByCompra(Integer id_compra) {
        List<Envio> result = new ArrayList<>();
        if (!de.listAll().isEmpty()) {
            for (Envio envio : de.listAll().toArray()) {
                if (envio.getId_compra() != null && envio.getId_compra().equals(id_compra)) {
                    result.add(envio);
                }
            }
        }
        return result;
    }
}