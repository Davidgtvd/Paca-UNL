package org.unl.pacas.base.controller.dao.dao_models;

import org.unl.pacas.base.controller.dao.AdapterDao;
import org.unl.pacas.base.models.Compra;

public class DaoCompra extends AdapterDao<Compra> {
    private Compra obj;

    public DaoCompra() {
        super(Compra.class);
    }

    public Compra getObj() {
        if (obj == null)
            this.obj = new Compra();
        return this.obj;
    }

    public void setObj(Compra obj) {
        this.obj = obj;
    }

    public Boolean save() {
        try {
            obj.setId(listAll().getLength() + 1);
            this.persist(obj);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar compra: " + e.getMessage());
            return false;
        }
    }

    public Boolean update(Integer pos) {
        try {
            this.update(obj, pos);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar compra: " + e.getMessage());
            return false;
        }
    }
}