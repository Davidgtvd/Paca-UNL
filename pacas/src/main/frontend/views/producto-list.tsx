import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, Notification } from '@vaadin/react-components';
import { useEffect, useState } from 'react';
import Producto from 'Frontend/generated/org/unl/pacas/base/models/Producto';
import { ProductoServices } from 'Frontend/generated/endpoints';

export const config: ViewConfig = {
  title: 'Producto',
  menu: {
    icon: 'vaadin:cart',
    order: 2,
    title: 'Productos',
  },
};

interface CarritoItem {
  id: number;
  nombre: string;
  precio?: number;
  imagen?: string;
  stock?: number;
  cantidad: number;
  usuarioId?: number;
}

export default function ProductoView() {
  const [items, setItems] = useState<Producto[]>([]);
  const [usuarioLogueado, setUsuarioLogueado] = useState<any>(null);
  const [carrito, setCarrito] = useState<CarritoItem[]>([]);
  const [carritoVisible, setCarritoVisible] = useState(false);

  // Carga usuario logueado
  useEffect(() => {
    const usuarioGuardado = localStorage.getItem('sesion_usuario');
    if (usuarioGuardado) {
      try {
        setUsuarioLogueado(JSON.parse(usuarioGuardado));
      } catch {
        localStorage.removeItem('sesion_usuario');
      }
    }
  }, []);

  // Carga productos desde backend y localStorage, combinados sin duplicados
  useEffect(() => {
    async function cargarProductos() {
      try {
        const backendProductos = (await ProductoServices.listAll()) ?? [];
        const localProductosStr = localStorage.getItem('productos');
        const localProductos: Producto[] = localProductosStr ? JSON.parse(localProductosStr) : [];

        // Combinar y eliminar duplicados por id (priorizando local)
        const mapProductos = new Map<number, Producto>();
        localProductos.forEach(p => mapProductos.set(p.id!, p));
        backendProductos.forEach(p => {
          if (!mapProductos.has(p.id!)) mapProductos.set(p.id!, p);
        });

        setItems(Array.from(mapProductos.values()));
      } catch (error) {
        Notification.show('❌ Error al cargar productos', { duration: 4000, position: 'top-center', theme: 'error' });
        setItems([]);
      }
    }
    cargarProductos();
  }, []);

  // Carga carrito desde localStorage
  useEffect(() => {
    if (usuarioLogueado) {
      const carritoKey = `carrito_${usuarioLogueado.id}`;
      const carritoActual = localStorage.getItem(carritoKey);
      setCarrito(carritoActual ? JSON.parse(carritoActual) : []);
    } else {
      setCarrito([]);
    }
  }, [usuarioLogueado]);

  // Guarda carrito en localStorage
  const guardarCarrito = (nuevoCarrito: CarritoItem[]) => {
    if (!usuarioLogueado) return;
    const carritoKey = `carrito_${usuarioLogueado.id}`;
    localStorage.setItem(carritoKey, JSON.stringify(nuevoCarrito));
    setCarrito(nuevoCarrito);
  };

  // Añadir producto al carrito
  const añadirAlCarrito = (producto: Producto) => {
    if (!usuarioLogueado) {
      Notification.show('🔒 Debe iniciar sesión para agregar productos al carrito', {
        duration: 5000,
        position: 'top-center',
        theme: 'error',
      });
      setTimeout(() => (window.location.href = '/cuenta'), 2000);
      return;
    }

    try {
      const productoExistente = carrito.find((item) => item.id === producto.id);
      let nuevoCarrito: CarritoItem[];
      if (productoExistente) {
        nuevoCarrito = carrito.map((item) =>
          item.id === producto.id ? { ...item, cantidad: item.cantidad + 1 } : item
        );
        Notification.show(`✅ "${producto.nombre ?? 'Producto'}" - Cantidad actualizada en el carrito`, {
          duration: 4000,
          position: 'top-start',
          theme: 'success',
        });
      } else {
        const nuevoItem: CarritoItem = {
          id: producto.id!,
          nombre: producto.nombre ?? 'Producto',
          precio: producto.pvp ?? producto.precio ?? 0,
          imagen: producto.imagen ?? '',
          stock: producto.stock,
          cantidad: 1,
          usuarioId: usuarioLogueado.id,
        };
        nuevoCarrito = [...carrito, nuevoItem];
        Notification.show(`🛒 "${producto.nombre ?? 'Producto'}" agregado al carrito`, {
          duration: 4000,
          position: 'top-start',
          theme: 'success',
        });
      }
      guardarCarrito(nuevoCarrito);
      setCarritoVisible(true);
    } catch (error) {
      Notification.show('❌ Error al añadir producto al carrito', {
        duration: 3000,
        position: 'top-center',
        theme: 'error',
      });
    }
  };

  // Ordenar productos
  const ordenarProductos = (campo: 'nombre' | 'pvp', asc: boolean) => {
    const itemsOrdenados = [...items].sort((a, b) => {
      if (campo === 'nombre') {
        const na = a.nombre?.toLowerCase() ?? '';
        const nb = b.nombre?.toLowerCase() ?? '';
        return asc ? na.localeCompare(nb) : nb.localeCompare(na);
      } else {
        const pa = a.pvp ?? 0;
        const pb = b.pvp ?? 0;
        return asc ? pa - pb : pb - pa;
      }
    });
    setItems(itemsOrdenados);
  };

  // Cambiar cantidad en carrito
  const cambiarCantidad = (id: number, cantidad: number) => {
    if (cantidad < 1) return;
    const nuevoCarrito = carrito.map((item) => (item.id === id ? { ...item, cantidad } : item));
    guardarCarrito(nuevoCarrito);
  };

  // Eliminar producto del carrito
  const eliminarDelCarrito = (id: number) => {
    const nuevoCarrito = carrito.filter((item) => item.id !== id);
    guardarCarrito(nuevoCarrito);
  };

  // Calcular total carrito
  const totalCarrito = carrito.reduce((sum, item) => sum + ((item.precio ?? 0) * item.cantidad), 0);

  // Ir a pago
  const irAPago = () => {
    window.location.href = '/venta';
  };

  // Ir a login
  const irALogin = () => {
    window.location.href = '/cuenta';
  };

  return (
    <main style={{ width: '100%', minHeight: '100vh', padding: 20, boxSizing: 'border-box', background: 'linear-gradient(135deg, #e0e7ff 0%, #f8fafc 100%)', display: 'flex', flexDirection: 'column', gap: 20, position: 'relative' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0 10px', backgroundColor: '#fff', borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
        <h1 style={{ margin: 0, color: '#2c3e50' }}>Catálogo de Productos</h1>
        {usuarioLogueado ? (
          <div
            style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '8px 16px', backgroundColor: '#e8f5e9', borderRadius: 8, border: '1px solid #4caf50', fontWeight: 'bold', color: '#2e7d32', cursor: 'pointer', userSelect: 'none', position: 'relative' }}
            onClick={() => setCarritoVisible(!carritoVisible)}
            title="Ver carrito"
          >
            👤 {usuarioLogueado.nombre ?? usuarioLogueado.correoElectronico} &nbsp;🛒 Carrito ({carrito.length})
            <Button
              theme="success"
              style={{ marginLeft: 12, fontWeight: 'bold', fontSize: 14 }}
              onClick={(e) => {
                e.stopPropagation();
                irAPago();
              }}
              disabled={carrito.length === 0}
              title={carrito.length === 0 ? 'El carrito está vacío' : 'Proceder al pago'}
            >
              💳 Pagar
            </Button>
          </div>
        ) : (
          <div
            style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '8px 16px', backgroundColor: '#fff3cd', borderRadius: 8, border: '1px solid #ffc107', fontWeight: 'bold', color: '#856404', cursor: 'pointer', userSelect: 'none' }}
            onClick={irALogin}
            title="Iniciar sesión para comprar"
          >
            🔒 Inicie sesión para comprar &nbsp;
            <Button theme="primary" style={{ backgroundColor: '#007bff', color: 'white', fontWeight: 'bold' }}>
              🔑 Iniciar Sesión
            </Button>
          </div>
        )}
      </header>

      {/* Botones de ordenamiento */}
      <section style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
        <Button onClick={() => ordenarProductos('nombre', true)}>📝 A-Z</Button>
        <Button onClick={() => ordenarProductos('nombre', false)}>📝 Z-A</Button>
        <Button onClick={() => ordenarProductos('pvp', true)}>💰 Precio ↑</Button>
        <Button onClick={() => ordenarProductos('pvp', false)}>💰 Precio ↓</Button>
      </section>

      {/* Ventana carrito */}
      {carritoVisible && usuarioLogueado && (
        <div style={{ position: 'absolute', top: 70, right: 20, width: 320, maxHeight: '60vh', overflowY: 'auto', backgroundColor: '#fff', borderRadius: 12, boxShadow: '0 4px 20px rgba(0,0,0,0.2)', padding: 16, zIndex: 1000 }}>
          <h3 style={{ marginTop: 0, marginBottom: 12 }}>🛒 Carrito de Compras</h3>
          {carrito.length === 0 ? (
            <p style={{ textAlign: 'center', color: '#666' }}>El carrito está vacío</p>
          ) : (
            <>
              {carrito.map((item) => (
                <div key={item.id} style={{ display: 'flex', alignItems: 'center', marginBottom: 12, borderBottom: '1px solid #eee', paddingBottom: 8 }}>
                  <img src={item.imagen ?? ''} alt={item.nombre} style={{ width: 50, height: 50, objectFit: 'cover', borderRadius: 6, marginRight: 12 }} onError={(e) => { (e.target as HTMLImageElement).src = ''; }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontWeight: 'bold', fontSize: 14 }}>{item.nombre}</div>
                    <div style={{ fontSize: 12, color: '#666' }}>
                      ${item.precio?.toFixed(2) ?? '0.00'} x
                      <input type="number" min={1} value={item.cantidad} onChange={(e) => cambiarCantidad(item.id, parseInt(e.target.value) || 1)} style={{ width: 50, marginLeft: 8, padding: 4, borderRadius: 4, border: '1px solid #ccc', fontSize: 12 }} />
                    </div>
                  </div>
                  <button onClick={() => eliminarDelCarrito(item.id)} style={{ backgroundColor: '#ef4444', border: 'none', color: 'white', borderRadius: 6, padding: '4px 8px', cursor: 'pointer', fontSize: 12, marginLeft: 8 }} title="Eliminar producto">🗑️</button>
                </div>
              ))}
              <div style={{ borderTop: '1px solid #ddd', paddingTop: 12, fontWeight: 'bold', fontSize: 16, textAlign: 'right' }}>
                Total: ${totalCarrito.toFixed(2)}
              </div>
            </>
          )}
        </div>
      )}

      {/* Lista productos */}
      <section style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: 20 }}>
        {items.length === 0 && (
          <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: 40, color: '#666', fontSize: 16 }}>
            <div style={{ fontSize: 48, marginBottom: 16 }}>📦</div>
            <div>No hay productos disponibles</div>
          </div>
        )}

        {items.map((producto, index) => (
          <article key={producto.id} style={{ border: '1px solid #e0e0e0', borderRadius: 12, padding: 20, backgroundColor: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.1)', transition: 'transform 0.2s ease, box-shadow 0.2s ease', cursor: 'default', display: 'flex', flexDirection: 'column', alignItems: 'center' }} onMouseEnter={e => { const el = e.currentTarget; el.style.transform = 'translateY(-4px)'; el.style.boxShadow = '0 6px 20px rgba(0,0,0,0.15)'; }} onMouseLeave={e => { const el = e.currentTarget; el.style.transform = 'translateY(0)'; el.style.boxShadow = '0 2px 8px rgba(0,0,0,0.1)'; }}>
            <div style={{ alignSelf: 'flex-start', fontSize: 12, color: '#666', fontWeight: 'bold', marginBottom: 8 }}>#{index + 1}</div>
            <img src={producto.imagen ?? ''} alt={producto.nombre ?? 'Producto'} style={{ width: 120, height: 120, objectFit: 'cover', borderRadius: 8, border: '1px solid #ddd', margin: '0 auto 16px', display: 'block' }} onError={e => { (e.target as HTMLImageElement).src = ''; }} />
            <h3 style={{ margin: '0 0 12px 0', fontSize: 18, fontWeight: 'bold', color: '#2c3e50', textAlign: 'center', lineHeight: 1.3 }}>{producto.nombre ?? 'Producto'}</h3>
            <div style={{ background: '#e3f2fd', color: '#1976d2', padding: '4px 12px', borderRadius: 16, fontSize: 12, fontWeight: 'bold', textAlign: 'center', marginBottom: 12, textTransform: 'uppercase', minWidth: 'fit-content' }}>{producto.categoria ?? ''}</div>
            <p style={{ margin: '0 0 16px 0', fontSize: 14, color: '#666', lineHeight: 1.4, textAlign: 'center', minHeight: 40 }}>{producto.descripcion ?? ''}</p>
            <div style={{ width: '100%', marginBottom: 16 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                <span style={{ fontSize: 12, color: '#666' }}>Precio:</span>
                <span style={{ fontSize: 16, fontWeight: 'bold', color: '#e74c3c' }}>${producto.precio?.toFixed(2) ?? '0.00'}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                <span style={{ fontSize: 12, color: '#666' }}>PVP:</span>
                <span style={{ fontSize: 16, fontWeight: 'bold', color: '#27ae60' }}>${producto.pvp?.toFixed(2) ?? '0.00'}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: 12, color: '#666' }}>Stock:</span>
                <span style={{ fontSize: 14, fontWeight: 'bold', color: (producto.stock ?? 0) > 5 ? '#27ae60' : '#e74c3c', padding: '2px 8px', borderRadius: 12, backgroundColor: (producto.stock ?? 0) > 5 ? '#e8f5e8' : '#ffeaea' }}>
                  {producto.stock ?? 0} unidades
                </span>
              </div>
            </div>
            {usuarioLogueado ? (
              <Button onClick={() => añadirAlCarrito(producto)} theme="primary" style={{ backgroundColor: (producto.stock ?? 0) <= 0 ? '#ccc' : '#28a745', color: 'white', fontWeight: 'bold', fontSize: 12, padding: '8px 12px', width: '100%', cursor: (producto.stock ?? 0) <= 0 ? 'not-allowed' : 'pointer' }} disabled={(producto.stock ?? 0) <= 0} title={(producto.stock ?? 0) <= 0 ? 'Sin stock disponible' : 'Agregar al carrito'}>
                {(producto.stock ?? 0) <= 0 ? '❌ Sin Stock' : '🛒 Agregar'}
              </Button>
            ) : (
              <Button onClick={irALogin} theme="primary" style={{ backgroundColor: '#007bff', color: 'white', fontWeight: 'bold', fontSize: 12, padding: '8px 12px', width: '100%' }} title="Iniciar sesión para comprar">
                🔑 Iniciar Sesión para Comprar
              </Button>
            )}
          </article>
        ))}
      </section>
    </main>
  );
}