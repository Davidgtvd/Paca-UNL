import React, { useState, useEffect } from "react";
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';

export const config: ViewConfig = {
  title: 'Cuenta',
  menu: {
    icon: 'vaadin:user',
    order: 5,
    title: 'Cuenta',
  },
};

type RolEnum = "ADMINISTRADOR" | "CLIENTE";

interface Cuenta {
  id?: number;
  nombre: string;
  correoElectronico: string;
  clave: string;
  rol: RolEnum;
  productos?: Producto[];
}

interface Pedido {
  id: number;
  producto: string;
  estado: "Pendiente" | "Cancelado" | "Completado";
  fecha: string;
  precio: number;
}

interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  imagen: string;
  precio?: number;
  stock?: number;
  pvp?: number;
  categoria?: string;
}

const ProductoFormAdmin: React.FC<{
  producto: Producto;
  modo: "agregar" | "editar";
  onGuardar: (producto: Producto) => void;
  onCancelar: () => void;
}> = ({ producto, modo, onGuardar, onCancelar }) => {
  const [formProducto, setFormProducto] = useState<Producto>(producto);

  useEffect(() => {
    setFormProducto(producto);
  }, [producto]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
  const { name, value } = e.target;
  setFormProducto(prev => ({
    ...prev,
    [name]: name === "stock" ? Number(value) : value,
  }));
};
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onloadend = () => {
      setFormProducto(prev => ({ ...prev, imagen: reader.result as string }));
    };
    reader.readAsDataURL(file);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formProducto.nombre.trim() || formProducto.nombre.trim().length < 3) {
      alert("El nombre del producto debe tener al menos 3 caracteres");
      return;
    }
    if (!formProducto.descripcion.trim() || formProducto.descripcion.trim().length < 10) {
      alert("La descripción del producto debe tener al menos 10 caracteres");
      return;
    }
    onGuardar(formProducto);
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: 20, border: "1px solid #ccc", padding: 16, borderRadius: 8 }}>
      <h4>{modo === "editar" ? "Editar Producto" : "Agregar Producto"}</h4>
      <div style={{ marginBottom: 8 }}>
        <label>Nombre:</label><br />
        <input
          type="text"
          name="nombre"
          value={formProducto.nombre}
          onChange={handleInputChange}
          required
          placeholder="Nombre del producto"
          autoComplete="off"
          style={{ width: "100%", padding: 8, borderRadius: 4, border: "1px solid #ccc" }}
        />
      </div>
      <div style={{ marginBottom: 8 }}>
        <label>Descripción:</label><br />
        <textarea
          name="descripcion"
          value={formProducto.descripcion}
          onChange={handleInputChange}
          required
          placeholder="Descripción del producto"
          rows={4}
          style={{ width: "100%", padding: 8, borderRadius: 4, border: "1px solid #ccc" }}
        />
      </div>
      <div style={{ marginBottom: 8 }}>
  <label>Stock:</label><br />
  <input
    type="number"
    name="stock"
    value={formProducto.stock ?? 0}
    onChange={handleInputChange}
    min={0}
    required
    placeholder="Cantidad en stock"
    style={{ width: "100%", padding: 8, borderRadius: 4, border: "1px solid #ccc" }}
  />
</div>
      <div style={{ marginBottom: 8 }}>
        <label>Imagen (subir desde computador):</label><br />
        <input
          type="file"
          accept="image/*"
          onChange={handleFileChange}
          style={{ width: "100%", padding: 8, borderRadius: 4, border: "1px solid #ccc" }}
        />
        {formProducto.imagen && (
          <img
            src={formProducto.imagen}
            alt="Preview"
            style={{ marginTop: 8, maxWidth: "200px", maxHeight: "150px", objectFit: "cover", borderRadius: 6 }}
          />
        )}
      </div>
      <button type="submit" style={{ marginRight: 8 }}>
        {modo === "editar" ? "Actualizar" : "Agregar"}
      </button>
      <button type="button" onClick={onCancelar}>
        Cancelar
      </button>
    </form>
  );
};

const Cuenta: React.FC = () => {
  const [cuenta, setCuenta] = useState<Cuenta | null>(() => {
    try {
      const sessionData = localStorage.getItem("sesion_usuario");
      return sessionData ? JSON.parse(sessionData) : null;
    } catch {
      return null;
    }
  });

  const [form, setForm] = useState<{ correoElectronico: string; clave: string; nombre?: string }>({
    correoElectronico: "",
    clave: "",
    nombre: "",
  });
  const [mensaje, setMensaje] = useState<string>("");
  const [modo, setModo] = useState<"selector" | "login" | "registro" | "admin">("selector");
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [productosAdmin, setProductosAdmin] = useState<Producto[]>([]);

  // Estados para formulario de producto admin (agregar/editar)
  const [productoFormVisible, setProductoFormVisible] = useState(false);
  const [modoProducto, setModoProducto] = useState<"agregar" | "editar">("agregar");
  const [productoEditando, setProductoEditando] = useState<Producto | null>(null);

  useEffect(() => {
    if (mensaje) {
      const timer = setTimeout(() => setMensaje(""), 5000);
      return () => clearTimeout(timer);
    }
  }, [mensaje]);

  useEffect(() => {
    if (cuenta) {
      cargarPedidosUsuario(cuenta.correoElectronico);
      if (cuenta.rol === "ADMINISTRADOR") {
        cargarProductosLocalStorage();
      } else {
        setProductosAdmin([]);
      }
    } else {
      setPedidos([]);
      setProductosAdmin([]);
    }
  }, [cuenta]);

  const cargarProductosLocalStorage = () => {
    try {
      const productosGuardados = localStorage.getItem("productos");
      if (productosGuardados) {
        setProductosAdmin(JSON.parse(productosGuardados));
      } else {
        setProductosAdmin([]);
      }
    } catch {
      setProductosAdmin([]);
    }
  };

  const guardarProductosLocalStorage = (productos: Producto[]) => {
    setProductosAdmin(productos);
    localStorage.setItem("productos", JSON.stringify(productos));
  };

  const cargarUsuariosRegistrados = (): Cuenta[] => {
    try {
      const usuariosStr = localStorage.getItem("usuarios_registrados");
      if (usuariosStr) {
        return JSON.parse(usuariosStr);
      }
      return [];
    } catch {
      return [];
    }
  };

  const guardarUsuariosRegistrados = (usuarios: Cuenta[]) => {
    localStorage.setItem("usuarios_registrados", JSON.stringify(usuarios));
  };

  const apiLogin = async (correoElectronico: string, clave: string): Promise<Cuenta> => {
    if (correoElectronico === "maria@gmail.com" && clave === "1234") {
      return {
        id: 1,
        nombre: "Maria",
        correoElectronico,
        clave: "",
        rol: "ADMINISTRADOR",
      };
    }
    if (correoElectronico === "cliente@ejemplo.com" && clave === "1234") {
      return {
        id: 2,
        nombre: "Cliente Ejemplo",
        correoElectronico,
        clave: "",
        rol: "CLIENTE",
      };
    }
    const usuarios = cargarUsuariosRegistrados();
    const usuario = usuarios.find(u => u.correoElectronico === correoElectronico && u.clave === clave);
    if (usuario) {
      return usuario;
    }
    throw new Error("Credenciales incorrectas");
  };

  const apiRegistrarCliente = async (correoElectronico: string, clave: string, nombre: string): Promise<Cuenta> => {
    const usuarios = cargarUsuariosRegistrados();
    if (usuarios.some(u => u.correoElectronico === correoElectronico)) {
      throw new Error("Ya existe una cuenta con ese correo");
    }
    const nuevoUsuario: Cuenta = {
      id: Date.now(),
      nombre,
      correoElectronico,
      clave,
      rol: "CLIENTE",
    };
    usuarios.push(nuevoUsuario);
    guardarUsuariosRegistrados(usuarios);
    return nuevoUsuario;
  };

  const cargarPedidosUsuario = (correoUsuario: string) => {
    try {
      const pedidosKey = `pedidos_${correoUsuario}`;
      const pedidosGuardados = localStorage.getItem(pedidosKey);
      if (pedidosGuardados) {
        setPedidos(JSON.parse(pedidosGuardados));
      } else {
        setPedidos([]);
      }
    } catch {
      setPedidos([]);
    }
  };

  const guardarPedidosUsuario = (nuevosPedidos: Pedido[]) => {
    if (!cuenta) return;
    try {
      const pedidosKey = `pedidos_${cuenta.correoElectronico}`;
      localStorage.setItem(pedidosKey, JSON.stringify(nuevosPedidos));
      setPedidos(nuevosPedidos);
    } catch {}
  };

  const iniciarSesion = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const usuario = await apiLogin(form.correoElectronico.trim(), form.clave.trim());
      setCuenta(usuario);
      localStorage.setItem("sesion_usuario", JSON.stringify(usuario));
      setMensaje("✅ Sesión iniciada correctamente");
      setModo("selector");
      setForm({ correoElectronico: "", clave: "", nombre: "" });
    } catch (e: any) {
      setMensaje("❌ " + e.message);
    }
  };

  const registrarUsuario = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (!form.nombre || form.nombre.trim().length < 3) {
        setMensaje("❌ El nombre debe tener al menos 3 caracteres");
        return;
      }
      const usuario = await apiRegistrarCliente(form.correoElectronico.trim(), form.clave.trim(), form.nombre.trim());
      setCuenta(usuario);
      localStorage.setItem("sesion_usuario", JSON.stringify(usuario));
      setMensaje("✅ Registro exitoso");
      setModo("selector");
      setForm({ correoElectronico: "", clave: "", nombre: "" });
    } catch (e: any) {
      setMensaje("❌ " + e.message);
    }
  };

  const handleLogout = () => {
    setCuenta(null);
    setPedidos([]);
    setProductosAdmin([]);
    localStorage.removeItem("sesion_usuario");
    setModo("selector");
    setMensaje("Sesión cerrada");
  };

  const cancelarPedido = (id: number) => {
    const nuevosPedidos = pedidos.map(p => (p.id === id ? { ...p, estado: "Cancelado" } : p));
    guardarPedidosUsuario(nuevosPedidos);
  };

  const volverAPedir = (id: number) => {
    const nuevosPedidos = pedidos.map(p => (p.id === id ? { ...p, estado: "Pendiente" } : p));
    guardarPedidosUsuario(nuevosPedidos);
  };

  const agregarPedido = (producto: string, precio: number) => {
    const nuevoPedido: Pedido = {
      id: Date.now(),
      producto,
      estado: "Pendiente",
      fecha: new Date().toLocaleDateString(),
      precio,
    };
    guardarPedidosUsuario([...pedidos, nuevoPedido]);
  };

  // Funciones para agregar/editar producto admin

 const agregarProductoAdmin = (producto: Producto) => {
  if (!cuenta) return;
  // Aseguramos que stock sea número y tenga valor (0 si no definido)
  const nuevoProducto = { ...producto, id: Date.now(), stock: producto.stock ?? 0 };
  const nuevosProductos = [...productosAdmin, nuevoProducto];
  guardarProductosLocalStorage(nuevosProductos);
  setMensaje("✅ Producto agregado");
  setProductoFormVisible(false);
};

const actualizarProductoAdmin = (productoActualizado: Producto) => {
  if (!cuenta) return;
  // Aseguramos que stock sea número y tenga valor (0 si no definido)
  const productoConStock = { ...productoActualizado, stock: productoActualizado.stock ?? 0 };
  const nuevosProductos = productosAdmin.map(p =>
    p.id === productoConStock.id ? productoConStock : p
  );
  guardarProductosLocalStorage(nuevosProductos);
  setMensaje("✅ Producto actualizado");
  setProductoFormVisible(false);
};

const eliminarProductoAdmin = (id: number) => {
  if (!cuenta) return;
  const nuevosProductos = productosAdmin.filter(p => p.id !== id);
  guardarProductosLocalStorage(nuevosProductos);
  setMensaje("✅ Producto eliminado");
};

const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
  // Para el formulario general (login/registro), no relacionado con stock
  setForm({ ...form, [e.target.name]: e.target.value });
}; 


  const UserIcon = ({ size = 32, color = "#6366f1" }: { size?: number; color?: string }) => (
    <div
      style={{
        width: size,
        height: size,
        borderRadius: "50%",
        backgroundColor: color,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        color: "white",
        fontSize: size * 0.6,
        fontWeight: "bold",
        userSelect: "none",
      }}
    >
      👤
    </div>
  );

  const renderSelector = () => (
    <div style={{ textAlign: "center" }}>
      <h2>Bienvenido</h2>
      <button onClick={() => setModo("login")} style={styles.btnPrincipal}>
        <UserIcon size={20} color="#6366f1" /> Iniciar Sesión
      </button>
      <button onClick={() => setModo("registro")} style={styles.btnPrincipal}>
        Registrarse
      </button>
      <button onClick={() => setModo("admin")} style={styles.btnPrincipal}>
        Admin Login
      </button>
    </div>
  );

  const renderLogin = () => (
    <form onSubmit={iniciarSesion} style={styles.formContainer}>
      <h2>Iniciar Sesión</h2>
      <input
        type="email"
        name="correoElectronico"
        placeholder="Correo electrónico"
        value={form.correoElectronico}
        onChange={handleChange}
        style={styles.input}
        required
      />
      <input
        type="password"
        name="clave"
        placeholder="Contraseña"
        value={form.clave}
        onChange={handleChange}
        style={styles.input}
        required
      />
      <button type="submit" style={styles.btnPrincipal}>
        Entrar
      </button>
      <button type="button" onClick={() => setModo("selector")} style={styles.btnSecundario}>
        Volver
      </button>
    </form>
  );

  const renderRegistro = () => (
    <form onSubmit={registrarUsuario} style={styles.formContainer}>
      <h2>Registro de Cliente</h2>
      <input
        type="text"
        name="nombre"
        placeholder="Nombre completo"
        value={form.nombre || ""}
        onChange={handleChange}
        style={styles.input}
        required
      />
      <input
        type="email"
        name="correoElectronico"
        placeholder="Correo electrónico"
        value={form.correoElectronico}
        onChange={handleChange}
        style={styles.input}
        required
      />
      <input
        type="password"
        name="clave"
        placeholder="Contraseña"
        value={form.clave}
        onChange={handleChange}
        style={styles.input}
        required
      />
      <button type="submit" style={styles.btnPrincipal}>
        Registrarse
      </button>
      <button type="button" onClick={() => setModo("selector")} style={styles.btnSecundario}>
        Volver
      </button>
    </form>
  );

  const renderAdminLogin = () => (
    <form onSubmit={iniciarSesion} style={styles.formContainer}>
      <h2>Login Administrador</h2>
      <input
        type="email"
        name="correoElectronico"
        placeholder="Correo electrónico"
        value={form.correoElectronico}
        onChange={handleChange}
        style={styles.input}
        required
      />
      <input
        type="password"
        name="clave"
        placeholder="Contraseña"
        value={form.clave}
        onChange={handleChange}
        style={styles.input}
        required
      />
      <button type="submit" style={styles.btnPrincipal}>
        Entrar
      </button>
      <button type="button" onClick={() => setModo("selector")} style={styles.btnSecundario}>
        Volver
      </button>
    </form>
  );

  const renderCuenta = () => (
    <div className="cuenta-vista" style={styles.cuentaVista}>
      <div style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 20 }}>
        <UserIcon size={40} color={cuenta?.rol === "ADMINISTRADOR" ? "#f59e0b" : "#6366f1"} />
        <h2 style={{ margin: 0 }}>Mi Cuenta</h2>
        {cuenta?.rol === "ADMINISTRADOR" && <span style={styles.badgeAdmin}>👑 ADMIN</span>}
      </div>

      <div style={styles.datosCuenta}>
        <p><strong>ID:</strong> {cuenta?.id}</p>
        <p><strong>Nombre:</strong> {cuenta?.nombre}</p>
        <p><strong>Correo:</strong> {cuenta?.correoElectronico}</p>
        <p><strong>Rol:</strong> {cuenta?.rol}</p>
        <p><strong>Sesión activa:</strong> ✅ Persistente</p>
      </div>

      {cuenta?.rol === "ADMINISTRADOR" && (
        <>
          <div style={{ marginBottom: 32 }}>
            <h3>🛒 Lista de Productos (Editable)</h3>

            {!productoFormVisible && (
              <button
                style={{
                  marginBottom: 12,
                  padding: "8px 16px",
                  borderRadius: 6,
                  backgroundColor: "#10b981",
                  color: "white",
                  border: "none",
                  cursor: "pointer",
                }}
                onClick={() => {
                  setProductoEditando({ id: 0, nombre: "", descripcion: "", imagen: "" });
                  setModoProducto("agregar");
                  setProductoFormVisible(true);
                }}
              >
                ➕ Agregar Producto
              </button>
            )}

            {productoFormVisible && productoEditando && (
              <ProductoFormAdmin
                producto={productoEditando}
                modo={modoProducto}
                onGuardar={(prod) => {
                  if (modoProducto === "agregar") {
                    agregarProductoAdmin(prod);
                  } else {
                    actualizarProductoAdmin(prod);
                  }
                }}
                onCancelar={() => setProductoFormVisible(false)}
              />
            )}

            {productosAdmin.length === 0 ? (
              <p>No hay productos disponibles.</p>
            ) : (
              productosAdmin.map((producto) => (
                <div
                  key={producto.id}
                  style={{ border: "1px solid #ccc", padding: 12, borderRadius: 8, marginBottom: 12 }}
                >
                  <h4>{producto.nombre}</h4>
                  <p>{producto.descripcion}</p>
                  {producto.imagen && (
                    <img
                      src={producto.imagen}
                      alt={producto.nombre}
                      style={{ maxWidth: "200px", maxHeight: "150px", objectFit: "cover", borderRadius: 6 }}
                    />
                  )}
                  <div style={{ marginTop: 8 }}>
                    <button
                      style={{ marginRight: 8, padding: "6px 12px", borderRadius: 6, cursor: "pointer" }}
                      onClick={() => {
                        setProductoEditando(producto);
                        setModoProducto("editar");
                        setProductoFormVisible(true);
                      }}
                    >
                      ✏️ Editar
                    </button>
                    <button
                      style={{
                        padding: "6px 12px",
                        borderRadius: 6,
                        cursor: "pointer",
                        backgroundColor: "#ef4444",
                        color: "white",
                        border: "none",
                      }}
                      onClick={() => {
                        if (window.confirm(`¿Eliminar producto "${producto.nombre}"? Esta acción no se puede deshacer.`)) {
                          eliminarProductoAdmin(producto.id!);
                        }
                      }}
                    >
                      🗑️ Eliminar
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </>
      )}

      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
        <h3>📦 Mis Pedidos ({pedidos.length})</h3>
        {cuenta?.rol === "CLIENTE" && (
          <button
            style={styles.btnAgregar}
            onClick={() => agregarPedido("Paca Nueva", Math.floor(Math.random() * 100) + 20)}
          >
            ➕ Nuevo Pedido
          </button>
        )}
      </div>

      {pedidos.length === 0 ? (
        <div style={styles.sinPedidos}>
          <p>📭 No tienes pedidos aún</p>
          {cuenta?.rol === "CLIENTE" && (
            <button style={styles.btnPrincipal} onClick={() => agregarPedido("Mi Primera Paca", 35.99)}>
              🛒 Hacer mi primer pedido
            </button>
          )}
        </div>
      ) : (
        <div style={styles.tablaContainer}>
          <table style={styles.tabla}>
            <thead>
              <tr style={styles.tablaHeader}>
                <th style={styles.th}>ID</th>
                <th style={styles.th}>Producto</th>
                <th style={styles.th}>Fecha</th>
                <th style={styles.th}>Precio</th>
                <th style={styles.th}>Estado</th>
                <th style={styles.th}>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {pedidos.map((p) => (
                <tr key={p.id} style={styles.tablaRow}>
                  <td style={styles.td}>#{p.id.toString().slice(-4)}</td>
                  <td style={styles.td}>{p.producto}</td>
                  <td style={styles.td}>{p.fecha}</td>
                  <td style={styles.td}>${p.precio.toFixed(2)}</td>
                  <td style={styles.td}>
                    <span
                      style={{
                        ...styles.estadoBadge,
                        backgroundColor:
                          p.estado === "Cancelado"
                            ? "#fee2e2"
                            : p.estado === "Completado"
                            ? "#dcfce7"
                            : "#fef3c7",
                        color:
                          p.estado === "Cancelado"
                            ? "#dc2626"
                            : p.estado === "Completado"
                            ? "#16a34a"
                            : "#d97706",
                      }}
                    >
                      {p.estado === "Pendiente" && "⏳ "}
                      {p.estado === "Completado" && "✅ "}
                      {p.estado === "Cancelado" && "❌ "}
                      {p.estado}
                    </span>
                  </td>
                  <td style={styles.td}>
                    {p.estado === "Pendiente" && (
                      <button style={styles.btnCancelar} onClick={() => cancelarPedido(p.id)}>
                        Cancelar
                      </button>
                    )}
                    {p.estado === "Cancelado" && (
                      <button style={styles.btnVolver} onClick={() => volverAPedir(p.id)}>
                        Volver a pedir
                      </button>
                    )}
                    {p.estado === "Completado" && (
                      <span style={{ color: "#16a34a", fontWeight: "bold" }}>✔️ Entregado</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div style={styles.resumenPedidos}>
            <p>
              <strong>Total gastado:</strong> ${pedidos.reduce((sum, p) => sum + p.precio, 0).toFixed(2)}
            </p>
            <p>
              <strong>Pedidos completados:</strong> {pedidos.filter((p) => p.estado === "Completado").length}
            </p>
          </div>
        </div>
      )}

      <button style={styles.btnSecundario} onClick={handleLogout}>
        🚪 Cerrar sesión
      </button>
      {mensaje && (
        <p style={{ color: mensaje.startsWith("❌") ? "red" : "green", marginTop: 12 }}>{mensaje}</p>
      )}
    </div>
  );

  return (
    <div style={styles.contenedor}>
      {!cuenta ? (
        modo === "registro" ? (
          renderRegistro()
        ) : modo === "admin" ? (
          renderAdminLogin()
        ) : modo === "login" ? (
          renderLogin()
        ) : (
          renderSelector()
        )
      ) : (
        renderCuenta()
      )}
    </div>
  );
};

const styles: { [key: string]: React.CSSProperties } = {
  contenedor: {
    minHeight: "100vh",
    background: "linear-gradient(135deg, #e0e7ff 0%, #f8fafc 100%)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
    padding: "20px",
  },
  formContainer: {
    maxWidth: 400,
    margin: "auto",
    background: "#fff",
    padding: 24,
    borderRadius: 12,
    boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
    display: "flex",
    flexDirection: "column",
    gap: 12,
  },
  input: {
    width: "100%",
    padding: 10,
    borderRadius: 6,
    border: "1px solid #ccc",
    fontSize: 16,
    fontFamily: "inherit",
  },
  cuentaVista: {
    background: "#fff",
    padding: 32,
    borderRadius: 16,
    boxShadow: "0 4px 24px rgba(0, 0, 0, 0.1)",
    minWidth: 600,
    maxWidth: 800,
    width: "100%",
    maxHeight: "90vh",
    overflowY: "auto",
  },
  datosCuenta: {
    marginBottom: 24,
    background: "linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%)",
    padding: 20,
    borderRadius: 12,
    border: "1px solid #e2e8f0",
  },
  badgeAdmin: {
    backgroundColor: "#fbbf24",
    color: "#92400e",
    padding: "4px 12px",
    borderRadius: 20,
    fontSize: 12,
    fontWeight: "bold",
  },
  sinPedidos: {
    textAlign: "center",
    padding: 40,
    color: "#6b7280",
    backgroundColor: "#f9fafb",
    borderRadius: 12,
    marginBottom: 24,
  },
  tablaContainer: {
    overflowX: "auto",
    marginBottom: 24,
  },
  tabla: {
    width: "100%",
    borderCollapse: "collapse",
    backgroundColor: "#fff",
    borderRadius: 8,
    overflow: "hidden",
    boxShadow: "0 1px 3px rgba(0, 0, 0, 0.1)",
  },
  tablaHeader: {
    backgroundColor: "#f8fafc",
  },
  tablaRow: {
    borderBottom: "1px solid #e5e7eb",
  },
  th: {
    padding: 12,
    textAlign: "left",
    fontWeight: "bold",
    color: "#374151",
    borderBottom: "2px solid #e5e7eb",
  },
  td: {
    padding: 12,
    color: "#6b7280",
  },
  estadoBadge: {
    padding: "4px 8px",
    borderRadius: 12,
    fontSize: 12,
    fontWeight: "bold",
  },
  btnCancelar: {
    background: "#ef4444",
    color: "#fff",
    border: "none",
    borderRadius: 6,
    padding: "6px 12px",
    fontWeight: "bold",
    cursor: "pointer",
    fontSize: 12,
  },
  btnVolver: {
    background: "#10b981",
    color: "#fff",
    border: "none",
    borderRadius: 6,
    padding: "6px 12px",
    fontWeight: "bold",
    cursor: "pointer",
    fontSize: 12,
  },
  resumenPedidos: {
    marginTop: 16,
    padding: 16,
    backgroundColor: "#f8fafc",
    borderRadius: 8,
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  btnAgregar: {
    background: "#10b981",
    color: "#fff",
    border: "none",
    borderRadius: 6,
    padding: "8px 16px",
    fontWeight: "bold",
    cursor: "pointer",
    fontSize: 14,
  },
  btnPrincipal: {
    background: "linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)",
    color: "#fff",
    border: "none",
    borderRadius: 8,
    padding: "12px 20px",
    fontWeight: "bold",
    cursor: "pointer",
    margin: "8px 4px",
    fontSize: 16,
    display: "inline-flex",
    alignItems: "center",
    gap: 8,
    userSelect: "none",
    transition: "background-color 0.3s ease",
  },
  btnSecundario: {
    background: "#f8fafc",
    color: "#6366f1",
    border: "2px solid #e5e7eb",
    borderRadius: 8,
    padding: "12px 20px",
    fontWeight: "bold",
    cursor: "pointer",
    marginTop: 12,
    fontSize: 16,
    transition: "all 0.2s",
  },
};

export default Cuenta;