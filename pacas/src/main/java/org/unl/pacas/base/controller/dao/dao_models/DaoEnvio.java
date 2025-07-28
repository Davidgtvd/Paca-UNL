package org.unl.pacas.base.controller.dao.dao_models;

import org.unl.pacas.base.controller.dao.AdapterDao;
import org.unl.pacas.base.models.Envio;

public class DaoEnvio extends AdapterDao<Envio> {
    private Envio obj;

    public DaoEnvio() {
        super(Envio.class);
    }

    public Envio getObj() {
        if (obj == null)
            this.obj = new Envio();
        return this.obj;
    }

    public void setObj(Envio obj) {
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
            this.update(obj, pos);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Puedes agregar métodos extra para filtrar por id_compra si lo necesitas
    // Ejemplo: obtener todos los envíos de una compra específica
    /*
    public LinkedList<Envio> findByCompraId(Integer idCompra) {
        LinkedList<Envio> result = new LinkedList<>(Envio.class);
        LinkedList<Envio> allEnvios = listAll();
        for (int i = 0; i < allEnvios.getLength(); i++) {
            Envio envio = allEnvios.get(i);
            if (envio.getId_compra() != null && envio.getId_compra().equals(idCompra)) {
                result.add(envio);
            }
        }
        return result;
    }
    */

    // MAIN para pruebas
    /*
    public static void main(String[] args) {
        DaoEnvio dao = new DaoEnvio();
        dao.getObj().setCalle("Av. Central");
        dao.getObj().setCiudad("Quito");
        dao.getObj().setProvincia("Pichincha");
        dao.getObj().setReferencia("Edificio azul, piso 2");
        dao.getObj().setId_compra(1);

        if (dao.save()) {
            System.out.println("Envio guardado exitosamente");
        } else {
            System.out.println("Error al guardar el envio");
        }
    }
    */
}