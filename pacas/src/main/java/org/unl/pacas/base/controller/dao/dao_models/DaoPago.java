package org.unl.pacas.base.controller.dao.dao_models;

import org.unl.pacas.base.controller.dao.AdapterDao;
import org.unl.pacas.base.models.Pago;

public class DaoPago extends AdapterDao<Pago> {
    private Pago obj;

    public DaoPago() {
        super(Pago.class);
    }

    public Pago getObj() {
        if (obj == null)
            this.obj = new Pago();
        return this.obj;
    }

    public void setObj(Pago obj) {
        this.obj = obj;
    }

    public Boolean save() {
        try {
            obj.setId(listAll().getLength() + 1);
            this.persist(obj);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar pago: " + e.getMessage());
            return false;
        }
    }

    public Boolean update(Integer pos) {
        try {
            this.update(obj, pos);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar pago: " + e.getMessage());
            return false;
        }
    }
}