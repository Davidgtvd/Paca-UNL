package org.unl.pacas.base.controller.dao.dao_models;

import org.unl.pacas.base.controller.dao.AdapterDao;
import org.unl.pacas.base.models.IdentificacionEnum;
import org.unl.pacas.base.models.Persona;
import org.unl.pacas.base.models.SexoEnum;

public class DaoPersona extends AdapterDao <Persona>{
    private Persona obj;

    public DaoPersona (){
        super(Persona.class);
    }

    public Persona getObj() {
        if (obj == null) 
            this.obj = new Persona();
        return this.obj;
    }

    public void setObj(Persona obj) {
        this.obj = obj;
    }

    public Boolean save(){
        try {
            obj.setId(listAll().getLength()+1);
            this.persist(obj);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    public Boolean update(Integer pos){
        try{
            this.update(obj, pos); //obj.getId
            return true;
        } catch (Exception e) { 
            return false;  
        }
    }

    /*public static void main(String[] args) {
        DaoPersona da = new DaoPersona();
        da.getObj().setId(da.listAll().getLength() + 1);
        da.getObj().setNombre("María");
        da.getObj().setApellido("González");
        da.getObj().setIdentificacion(IdentificacionEnum.CEDULA);
        da.getObj().setTelefono("0987654321");
        da.getObj().setSexo(SexoEnum.FEMENINO);
        da.getObj().setFecha_nacimiento(new java.util.Date());
        
        if (da.save()) {
            System.out.println("Persona guardada exitosamente");
        } else {
            System.out.println("Error al guardar la persona");
        }
    }*/
}