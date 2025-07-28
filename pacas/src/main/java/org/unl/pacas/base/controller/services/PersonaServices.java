package org.unl.pacas.base.controller.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

import org.unl.pacas.base.controller.dao.dao_models.DaoCuenta;
import org.unl.pacas.base.controller.dao.dao_models.DaoPersona;
import org.unl.pacas.base.controller.data_struct.list.LinkedList;
import org.unl.pacas.base.models.Cuenta;
import org.unl.pacas.base.models.Persona;
import org.unl.pacas.base.models.IdentificacionEnum;
import org.unl.pacas.base.models.SexoEnum;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@BrowserCallable
@AnonymousAllowed
public class PersonaServices {
    private DaoPersona db;

    public PersonaServices() {
        db = new DaoPersona();
    }

    /**
     * Crear persona
     */
    public void createPersona(@NotEmpty String nombre, @NotEmpty String apellido, 
                             @NotNull IdentificacionEnum identificacion, @NotEmpty String telefono,
                             @NotNull SexoEnum sexo, @NotNull Date fecha_nacimiento) throws Exception {
        if (nombre.trim().length() > 0 && apellido.trim().length() > 0 && 
            telefono.trim().length() > 0 && identificacion != null && 
            sexo != null && fecha_nacimiento != null) {
            
            db.getObj().setNombre(nombre);
            db.getObj().setApellido(apellido);
            db.getObj().setIdentificacion(identificacion);
            db.getObj().setTelefono(telefono);
            db.getObj().setSexo(sexo);
            db.getObj().setFecha_nacimiento(fecha_nacimiento);
            
            if (!db.save()) {
                throw new Exception("No se pudo guardar los datos de la persona");
            }
        } else {
            throw new Exception("Todos los campos son obligatorios");
        }
    }

    /**
     * Actualizar persona
     */
    public void updatePersona(@NotNull Integer id, @NotEmpty String nombre, @NotEmpty String apellido, 
                             @NotNull IdentificacionEnum identificacion, @NotEmpty String telefono,
                             @NotNull SexoEnum sexo, @NotNull Date fecha_nacimiento) throws Exception {
        if (id != null && nombre.trim().length() > 0 && apellido.trim().length() > 0 && 
            telefono.trim().length() > 0 && identificacion != null && 
            sexo != null && fecha_nacimiento != null) {
            
            LinkedList<Persona> personas = db.listAll();
            Integer indice = personas.findIndexById(id);
            
            if (indice != -1) {
                db.getObj().setId(id);
                db.getObj().setNombre(nombre);
                db.getObj().setApellido(apellido);
                db.getObj().setIdentificacion(identificacion);
                db.getObj().setTelefono(telefono);
                db.getObj().setSexo(sexo);
                db.getObj().setFecha_nacimiento(fecha_nacimiento);
                
                if (!db.update(indice)) {
                    throw new Exception("No se pudo actualizar la persona");
                }
            } else {
                throw new Exception("Persona no encontrada");
            }
        } else {
            throw new Exception("Todos los campos son obligatorios");
        }
    }

    /**
     * Eliminar persona
     */
   /*  public void deletePersona(@NotNull Integer id) throws Exception {
        if (id != null && id > 0) {
            LinkedList<Persona> personas = db.listAll();
            Integer indice = personas.findIndexById(id);
            
            if (indice != -1) {
                if (!db.delete(indice)) {
                    throw new Exception("No se pudo eliminar la persona");
                }
            } else {
                throw new Exception("Persona no encontrada");
            }
        } else {
            throw new Exception("ID de persona inválido");
        }
    }*/

    /**
     * Listar todas las personas
     */
    public List<Persona> listAll() {
        try {
            LinkedList<Persona> personas = db.listAll();
            List<Persona> lista = new ArrayList<>();
            
            if (!personas.isEmpty()) {
                for (int i = 0; i < personas.getLength(); i++) {
                    lista.add(personas.get(i));
                }
            }
            return lista;
        } catch (Exception e) {
            System.err.println("Error al listar personas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Listar personas para ComboBox (usado en DetalleFactura)
     */
    public List<String> listaPersonaCombo() {
        List<String> lista = new ArrayList<>();
        try {
            LinkedList<Persona> personas = db.listAll();
            if (!personas.isEmpty()) {
                for (int i = 0; i < personas.getLength(); i++) {
                    Persona persona = personas.get(i);
                    // Formato: "índice - nombre apellido - teléfono"
                    lista.add(i + " - " + persona.getNombre() + " " + persona.getApellido() + " - " + persona.getTelefono());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar personas para combo: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Ordenar personas por columna
     */
    public List<Persona> order(@NotEmpty String columnId, @NotNull Integer dir) throws Exception {
        try {
            LinkedList<Persona> personas = db.listAll();
            
            if (!personas.isEmpty()) {
                // Implementar ordenamiento según la columna
                switch (columnId.toLowerCase()) {
                    case "nombre":
                        // Implementar ordenamiento por nombre
                        break;
                    case "apellido":
                        // Implementar ordenamiento por apellido
                        break;
                    case "telefono":
                        // Implementar ordenamiento por teléfono
                        break;
                    default:
                        throw new Exception("Columna de ordenamiento no válida");
                }
            }
            
            return listAll();
        } catch (Exception e) {
            throw new Exception("Error al ordenar personas: " + e.getMessage());
        }
    }

    /**
     * Buscar personas
     */
    public List<Persona> buscarBinariaLineal(@NotEmpty String atributo, 
                                           @NotEmpty String valor, 
                                           @NotNull Integer tipo) throws Exception {
        try {
            LinkedList<Persona> personas = db.listAll();
            List<Persona> resultados = new ArrayList<>();
            
            if (!personas.isEmpty()) {
                for (int i = 0; i < personas.getLength(); i++) {
                    Persona persona = personas.get(i);
                    boolean coincide = false;
                    
                    switch (atributo.toLowerCase()) {
                        case "nombre":
                            coincide = persona.getNombre().toLowerCase().contains(valor.toLowerCase());
                            break;
                        case "apellido":
                            coincide = persona.getApellido().toLowerCase().contains(valor.toLowerCase());
                            break;
                        case "telefono":
                            coincide = persona.getTelefono().contains(valor);
                            break;
                        case "identificacion":
                            coincide = persona.getIdentificacion().toString().toLowerCase().contains(valor.toLowerCase());
                            break;
                        case "sexo":
                            coincide = persona.getSexo().toString().toLowerCase().contains(valor.toLowerCase());
                            break;
                    }
                    
                    if (coincide) {
                        resultados.add(persona);
                    }
                }
            }
            
            return resultados;
        } catch (Exception e) {
            throw new Exception("Error en la búsqueda: " + e.getMessage());
        }
    }

    /**
     * Obtener tipos de identificación
     */
    public List<String> listaIdentificacion() {
        List<String> lista = new ArrayList<>();
        for (IdentificacionEnum tipo : IdentificacionEnum.values()) {
            lista.add(tipo.toString());
        }
        return lista;
    }

    /**
     * Obtener tipos de sexo
     */
    public List<String> listaSexo() {
        List<String> lista = new ArrayList<>();
        for (SexoEnum sexo : SexoEnum.values()) {
            lista.add(sexo.toString());
        }
        return lista;
    }

    /**
     * Método legacy mantenido para compatibilidad
     */
    @Deprecated
    public void save(@NotEmpty String usuario, @NotEmpty @Email String correo, @NotEmpty String clave, Integer edad) throws Exception {
        if (usuario.trim().length() > 0 && correo.trim().length() > 0 && clave.trim().length() > 0 && edad > 0) {
            // Mapear datos legacy al nuevo modelo
            db.getObj().setNombre(usuario);
            db.getObj().setApellido(""); // Campo requerido pero no disponible en método legacy
            db.getObj().setIdentificacion(IdentificacionEnum.CEDULA); // Valor por defecto
            db.getObj().setTelefono(""); // Campo requerido pero no disponible
            db.getObj().setSexo(SexoEnum.MASCULINO); // Valor por defecto
            db.getObj().setFecha_nacimiento(new Date()); // Fecha actual por defecto
            
            if (!db.save()) {
                throw new Exception("No se pudo guardar los datos de la persona");
            } else {
                DaoCuenta dc = new DaoCuenta();
                dc.getObj().setClave(clave);
                dc.getObj().setCorreoElectronico(correo);
                //dc.getObj().setEstado(Boolean.TRUE);
                dc.getObj().setId_persona(db.getObj().getId());
                if (!dc.save()) {
                    throw new Exception("No se pudo guardar los datos de la cuenta");
                }
            }
        } else {
            throw new Exception("No se pudo guardar los datos de persona");
        }
    }

    /**
     * Método legacy mantenido para compatibilidad
     */
    @Deprecated
    public List<HashMap> listaPersonas() {
        List<HashMap> lista = new ArrayList<>();
        if (!db.listAll().isEmpty()) {
            Persona[] arreglo = db.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                try {
                    HashMap<String, String> aux = new HashMap<>();
                    aux.put("id", arreglo[i].getId().toString());
                    aux.put("nombre", arreglo[i].getNombre());
                    aux.put("apellido", arreglo[i].getApellido());
                    aux.put("telefono", arreglo[i].getTelefono());
                    aux.put("identificacion", arreglo[i].getIdentificacion().toString());
                    aux.put("sexo", arreglo[i].getSexo().toString());
                    
                    lista.add(aux);
                } catch (Exception e) {
                    System.err.println("Error al procesar persona: " + e.getMessage());
                }
            }
        }
        return lista;
    }
}