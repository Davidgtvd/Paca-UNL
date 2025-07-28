package org.unl.pacas.base.controller.data_struct.list;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.unl.pacas.base.controller.Utiles;
import org.unl.pacas.base.controller.dao.AdapterDao;
import org.unl.pacas.base.controller.data_struct.stack.Stack;
import org.unl.pacas.base.controller.data_struct.stack.StackImplementation;

public class LinkedList<E> {
    private Node<E> head;
    private Node<E> last;
    private Integer length;
    private Class<?> clazz;

    private static class Node<E> {
        E data;
        Node<E> next; 

        public Node(E data) {
            this.data = data;
            this.next = null;
        }

        public Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }

        public E getData() {
            return data;
        }

        public void setData(E data) {
            this.data = data;
        }

        public Node<E> getNext() {
            return next;
        }

        public void setNext(Node<E> next) {
            this.next = next;
        }
    }

    public LinkedList() {
        head = null;
        last = null;
        length = 0;
    }

    public LinkedList(Class<?> clazz) {
        this();
        this.clazz = clazz;
    }

    public Integer getLength() {
        return this.length;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Boolean isEmpty() {
        return head == null || length == 0;
    }

    private Node<E> getNode(Integer pos) {
        if (isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("List empty");
        } else if (pos < 0 || pos >= length) {
            throw new ArrayIndexOutOfBoundsException("Index out range");
        } else if (pos == 0) {
            return head;
        } else if ((length.intValue() - 1) == pos.intValue()) {
            return last;
        } else {
            Node<E> search = head;
            Integer cont = 0;
            while (cont < pos) {
                cont++;
                search = search.getNext();
            }
            return search;
        }
    }

    private E getDataFirst() {
        if (isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("List empty");
        } else {
            return head.getData();
        }
    }

    private E getDataLast() {
        if (isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("List empty");
        } else {
            return last.getData();
        }
    }

    public E get(Integer pos) {
        return getNode(pos).getData();
    }

    // ✅ MÉTODO REMOVE ARREGLADO
    public void remove(int index) {
        if (index < 0 || index >= this.getLength()) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
        if (index == 0) {
            // Eliminar el primer nodo
            this.head = this.head.next;
            if (this.length == 1) {
                this.last = null;
            }
        } else if (index == this.length - 1) {
            // Eliminar el último nodo
            Node<E> prev = this.head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
            }
            prev.next = null;
            this.last = prev;
        } else {
            // Eliminar nodo intermedio
            Node<E> prev = this.head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
            }
            prev.next = prev.next.next;
        }
        this.length--;
    }

    private void addFirst(E data) {
        if (isEmpty()) {
            Node<E> aux = new Node<>(data);
            head = aux;
            last = aux;
        } else {
            Node<E> head_old = head;
            Node<E> aux = new Node<>(data, head_old);
            head = aux;
        }
        length++;
    }

    private void addLast(E data) {
        if (isEmpty()) {
            addFirst(data);
        } else {
            Node<E> aux = new Node<>(data);
            last.setNext(aux);
            last = aux;
            length++;
        }
    }

    public void add(E data, Integer pos) throws Exception {
        if (pos == 0) {
            addFirst(data);
        } else if (length.intValue() == pos.intValue()) {
            addLast(data);
        } else {
            Node<E> search_preview = getNode(pos - 1);
            Node<E> search = getNode(pos);
            Node<E> aux = new Node<>(data, search);
            search_preview.setNext(aux);
            length++;
        }
    }

    public void add(E data) {
        addLast(data);
    }

    public void update(E data, Integer pos) {
        getNode(pos).setData(data);
    }

    public void clear() {
        head = null;
        last = null;
        length = 0;
    }

    protected E deleteFirst() throws Exception {
        if (isEmpty()) {
            throw new Exception("List empty");
        } else {
            E element = head.getData();
            Node<E> aux = head.getNext();
            head = aux;
            if (length.intValue() == 1)
                last = null;
            length--;
            return element;
        }
    }

    protected E deleteLast() throws Exception {
        if (isEmpty()) {
            throw new Exception("List empty");
        } else {
            E element = last.getData();
            Node<E> aux = getNode(length - 2);
            if (aux == null) {
                last = null;
                if (length == 2) {
                    last = head;
                } else {
                    head = null;
                }
            } else {
                last = null;
                last = aux;
                last.setNext(null);
            }
            length--;
            return element;
        }
    }

    public E delete(Integer pos) throws Exception {
        if (isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("List empty");
        } else if (pos < 0 || pos >= length) {
            throw new ArrayIndexOutOfBoundsException("Index out range");
        } else if (pos == 0) {
            return deleteFirst();
        } else if ((length.intValue() - 1) == pos.intValue()) {
            return deleteLast();
        } else {
            Node<E> preview = getNode(pos - 1);
            Node<E> actualy = getNode(pos);
            E element = actualy.getData();
            Node<E> next = actualy.getNext();
            actualy = null;
            preview.setNext(next);
            length--;
            return element;
        }
    }

    public String print() {
        if (isEmpty())
            return "Esta vacia";
        else {
            StringBuilder resp = new StringBuilder();
            Node<E> help = head;
            while (help != null) {
                resp.append(help.getData()).append(" - ");
                help = help.getNext();
            }
            resp.append("\n");
            return resp.toString();
        }
    }

    @SuppressWarnings("unchecked")
    public E[] toArray() {
        if (this.length == 0) return null;
        
        Class<?> clase = (this.clazz != null) ? this.clazz : this.head.getData().getClass();
        E[] matriz = (E[]) java.lang.reflect.Array.newInstance(clase, this.length);
        
        Node<E> aux = head;
        for (int i = 0; i < length; i++) {
            matriz[i] = aux.getData();
            aux = aux.getNext();
        }
        return matriz;
    }

    public LinkedList<E> toList(E[] matriz) {
        clear();
        for (int i = 0; i < matriz.length; i++) {
            this.add(matriz[i]);
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LinkedList Data:\n");
        try {
            Node<E> help = head;
            while (help != null) {
                sb.append(help.getData()).append(" -> ");
                help = help.getNext();
            }
            sb.append("null");
        } catch (Exception e) {
            sb.append("Error: ").append(e.getMessage());
        }
        return sb.toString();
    }

    public void reset() {
        this.head = null;
        this.last = null;
        this.length = 0;
    }

    public Object getClazz(Object data, String atributo) throws Exception {
        String getter = "get" + Utiles.capitalize(atributo);
        
        for (Method i : data.getClass().getMethods()) {
            if (i.getName().equals(getter)) {
                return i.invoke(data);
            }
        }
        
        throw new NoSuchMethodException("No existe el método " + getter + " en " + data.getClass().getName());
    }

    private Object exist_attribute(E a, String attribute) throws Exception {
        Method method = null;
        attribute = attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
        attribute = "get" + attribute;
        
        for (Method aux : a.getClass().getMethods()) {
            if (aux.getName().equals(attribute)) {
                method = aux;
                break;
            }
        }

        if (method == null) {
            for (Method aux : a.getClass().getSuperclass().getMethods()) {
                if (aux.getName().equals(attribute)) {
                    method = aux;
                    break;
                }
            }
        }

        if (method != null) {
            return method.invoke(a);
        }
        return null;
    }

    public String[] getAtributos(Object obj) {
        Field[] array = obj.getClass().getDeclaredFields();
        LinkedList<String> atr = new LinkedList<>();
        for(int i = 0 ; i < array.length ; i ++){
            if(array[i].getName().startsWith("id_")){
                atr.add(array[i].getName().substring(3));
            } else{
                atr.add(array[i].getName());
            }
        }
        return atr.toArray();
    }

    public Boolean comparar(Object obj1, Object obj2, Integer type) {
        if (obj1 == null || obj2 == null) return false;

        if (type.equals(Utiles.ASCENDENTE)) {
            if (obj1 instanceof Number && obj2 instanceof Number) {
                Number o1 = (Number) obj1;
                Number o2 = (Number) obj2;
                return o1.doubleValue() < o2.doubleValue();
            } else {
                return obj1.toString().compareToIgnoreCase(obj2.toString()) < 0;
            }
        } else if (type.equals(Utiles.DESCENDENTE)) {
            if (obj1 instanceof Number && obj2 instanceof Number) {
                Number o1 = (Number) obj1;
                Number o2 = (Number) obj2;
                return o1.doubleValue() > o2.doubleValue();
            } else {
                return obj1.toString().compareToIgnoreCase(obj2.toString()) > 0;
            }
        } else if (type.equals(Utiles.BUSCAR)) {
            if (obj1 instanceof Number && obj2 instanceof Number) {
                Number o1 = (Number) obj1;
                Number o2 = (Number) obj2;
                return o1.doubleValue() == o2.doubleValue();
            } else {
                return obj1.toString().toLowerCase().contains(obj2.toString().toLowerCase());
            }
        }
        return false;
    }

    private Boolean compare(Object a, Object b, Integer type) {
        switch (type) {
            case 0:
                if (a instanceof Number) {
                    Number a1 = (Number) a;
                    Number b1 = (Number) b;
                    return a1.doubleValue() > b1.doubleValue();
                } else {
                    return (a.toString()).compareTo(b.toString()) > 0;
                }
            default:
                if (a instanceof Number) {
                    Number a1 = (Number) a;
                    Number b1 = (Number) b;
                    return a1.doubleValue() < b1.doubleValue();
                } else {
                    return (a.toString()).compareTo(b.toString()) < 0;
                }
        }
    }

    private Boolean compararObjetos(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            Number c = (Number) a;
            Number d = (Number) b;
            return c.doubleValue() == d.doubleValue();
        } else if (a instanceof String && b instanceof String) {
            return ((String) a).contains((String) b);
        } else {
            return false;
        }
    }

    public boolean compararAtributos(String atributo, Object a, Object b, Integer orden) throws Exception {
        Object valA = getClazz(a, atributo);
        Object valB = getClazz(b, atributo);
        return comparar(valA, valB, orden);
    }

    private Boolean atrribute_compare(String attribute, E a, E b, Integer type) throws Exception {
        return compare(exist_attribute(a, attribute), exist_attribute(b, attribute), type);
    }

    public LinkedList<E> quickSort(String atributo, Integer tipoOrden) throws Exception {
        if (isEmpty()) return this;
        
        E[] arr = this.toArray();
        quickSortRecursivo(atributo, arr, 0, arr.length - 1, tipoOrden);
        return this.toList(arr);
    }

    private void quickSortRecursivo(String atributo, E[] arr, int bajo, int alto, Integer tipoOrden) throws Exception {
        if (bajo < alto) {
            int pi = particion(atributo, arr, bajo, alto, tipoOrden);
            quickSortRecursivo(atributo, arr, bajo, pi - 1, tipoOrden);
            quickSortRecursivo(atributo, arr, pi + 1, alto, tipoOrden);
        }
    }

    private int particion(String atributo, E[] arr, int bajo, int alto, Integer tipoOrden) throws Exception {
        E pivot = arr[alto];
        int i = (bajo - 1);
        
        for (int j = bajo; j < alto; j++) {
            if (compararAtributos(atributo, arr[j], pivot, tipoOrden)) {
                i++;
                intercambiar(arr, i, j);
            }
        }
        intercambiar(arr, i + 1, alto);
        return i + 1;
    }

    private void intercambiar(E[] arr, int i, int j) {
        E temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public LinkedList<E> buscarPorAtributo(String atributo, Object valor) throws Exception {
        LinkedList<E> resultado = new LinkedList<>(this.clazz);
        if (isEmpty()) return resultado;
        
        Node<E> current = head;
        while (current != null) {
            Object valorAtributo = getClazz(current.getData(), atributo);
            if (comparar(valorAtributo, valor, Utiles.BUSCAR)) {
                resultado.add(current.getData());
            }
            current = current.getNext();
        }
        return resultado;
    }

    private int busquedaBinaria(E arr[], Object x, String attribute) throws Exception {
        int elementMenor = 0, elementMayor = arr.length - 1;
        while (elementMenor <= elementMayor) {
            int mid = elementMenor + (elementMayor - elementMenor) / 2;
            if (exist_attribute(arr[mid], attribute).equals(x)) 
                return mid;
            if (compare(exist_attribute(arr[mid], attribute), x, 1)) {
                elementMenor = mid + 1;
            } else {
                elementMayor = mid - 1;
            }
        }
        return -1;
    }

    public E busquedaBinaria(String attribute, Object x) {
        if (isEmpty()) return null;
        try {
            E[] arr = this.toArray();
            LinkedList<E> tempList = new LinkedList<>(this.clazz);
            tempList.toList(arr);
            tempList.quickSort(attribute, Utiles.ASCENDENTE);
            E[] sortedArr = tempList.toArray();
            
            int index = busquedaBinaria(sortedArr, x, attribute);
            return (index != -1) ? sortedArr[index] : null;
        } catch (Exception e) {
            System.out.println("LinkedList.busquedaBinaria() dice: " + e.getMessage());
            return null;
        }
    }

    public LinkedList<E> busquedaLinealBinaria(String attribute, Object x) {
        if (isEmpty()) return new LinkedList<>(this.clazz);
        try {
            Integer indice = getIndice(attribute, x);
            if (indice == -1) return new LinkedList<>(this.clazz);
            
            Integer i = indice.intValue();
            E objeto = get(indice);
            E[] arr = this.toArray();
            LinkedList<E> lista = new LinkedList<>(this.clazz);

            while (indice >= 0 && compararObjetos(exist_attribute(arr[indice], attribute), exist_attribute(objeto, attribute))) {
                lista.add(arr[indice]);
                indice--;
            }
            
            indice = i + 1;
            while (indice < this.length && compararObjetos(exist_attribute(arr[indice], attribute), exist_attribute(objeto, attribute))) {
                lista.add(arr[indice]);
                indice++;
            }
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList<>(this.clazz);
        }
    }

    public Integer getIndice(String attribute, Object x) throws Exception {
        if (isEmpty()) return -1;
        E[] arr = this.toArray();
        
        LinkedList<E> tempList = new LinkedList<>(this.clazz);
        tempList.toList(arr);
        tempList.quickSort(attribute, Utiles.ASCENDENTE);
        E[] sortedArr = tempList.toArray();
        
        return busquedaBinaria(sortedArr, x, attribute);
    }

    public String printArray(E[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]).append("\n");
        }
        return sb.toString();
    }

    public <T> LinkedList<HashMap<String, Object>> getHasMap(String atribAnidado, String[] atributos, 
                                                           T[] array, HashMap<String, AdapterDao<?>> daos) throws Exception {
        LinkedList<HashMap<String, Object>> list = new LinkedList<>();
        
        for(T item : array) {
            list.add(createHasMao(atribAnidado, atributos, item, daos));
        }
        return list;
    }

    public <T> HashMap<String, Object> createHasMao(String atribAnidado, String[] atributos, T item, HashMap<String, AdapterDao<?>> daos) throws Exception {
        HashMap<String, Object> aux = new HashMap<>();
        for(String atributo : atributos) {
            if(daos != null && daos.containsKey(atributo)) {
                Object relatedObj = getRelatedObject(item, atributo, daos.get(atributo));
                aux.put(atributo, getClazz(relatedObj, atribAnidado));
            } else {
                aux.put(atributo, getClazz(item, atributo));
            }
        }
        return aux;
    }

    public List<HashMap<String, Object>> transformList(LinkedList<HashMap<String, Object>> lista) throws Exception {
        List<HashMap<String, Object>> listaJavaUtil = new java.util.ArrayList<>();
        for (int i = 0; i < lista.getLength(); i++) {
            listaJavaUtil.add(lista.get(i));
        }
        return listaJavaUtil;
    }

    private <T> Object getRelatedObject(T obj, String relationName, AdapterDao<?> dao) throws Exception {
        Object id = getClazz(obj, "id_" + relationName);
        if(id == null) return null;
        
        LinkedList<?> allData = dao.listAll();
        if (allData != null && ((Integer)id).intValue() > 0 && ((Integer)id).intValue() <= allData.getLength()) {
            return allData.get(((Integer)id).intValue() - 1);
        }
        return null;
    }

    public Node<E> getNodeById(Integer pos) throws Exception {
        if(isEmpty()) return null;
        
        Node<E> search = head;
        while(search != null){
            try {
                Object currentId = this.getClazz(search.getData(), "id");
                if (currentId != null && currentId.equals(pos)) {
                    return search;
                }
            } catch (Exception e) {
                // Si no tiene atributo "id", continuar
            }
            search = search.getNext();
        }
        return null;
    }

    public Boolean delete_by_id(Integer id) {
        if (isEmpty() || id == null) {
            return false;
        }
        
        try {
            for (int i = 0; i < length; i++) {
                E elemento = get(i);
                
                if (elemento != null) {
                    try {
                        java.lang.reflect.Method getIdMethod = elemento.getClass().getMethod("getId");
                        Object elementId = getIdMethod.invoke(elemento);
                        
                        if (id.equals(elementId)) {
                            delete(i);
                            return true;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al eliminar por ID: " + e.getMessage());
            return false;
        }
    }

    public E findById(Integer id) {
        if (isEmpty() || id == null) {
            return null;
        }
        
        try {
            for (int i = 0; i < length; i++) {
                E elemento = get(i);
                
                if (elemento != null) {
                    try {
                        java.lang.reflect.Method getIdMethod = elemento.getClass().getMethod("getId");
                        Object elementId = getIdMethod.invoke(elemento);
                        
                        if (id.equals(elementId)) {
                            return elemento;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error en findById: " + e.getMessage());
            return null;
        }
    }

    public Integer findIndexById(Integer id) {
        if (isEmpty() || id == null) {
            return -1;
        }
        
        try {
            for (int i = 0; i < length; i++) {
                E elemento = get(i);
                
                if (elemento != null) {
                    try {
                        java.lang.reflect.Method getIdMethod = elemento.getClass().getMethod("getId");
                        Object elementId = getIdMethod.invoke(elemento);
                        
                        if (id.equals(elementId)) {
                            return i;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
            return -1;
        } catch (Exception e) {
            System.err.println("Error en findIndexById: " + e.getMessage());
            return -1;
        }
    }
}