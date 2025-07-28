import { Button, Grid, GridColumn, HorizontalLayout, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { useEffect, useState } from 'react';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';

export const config: ViewConfig = {
  title: 'Venta',
  menu: {
    icon: 'vaadin:clipboard-check',
    order: 2,
    title: 'Venta',
  },
};

type Producto = {
  nombre: string;
  cantidad: number;
  precio: number;
};

export default function FacturaView() {
  const [productos, setProductos] = useState<Producto[]>([]);
  const [subtotal, setSubtotal] = useState(0);
  const [iva, setIva] = useState(0);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    const carrito = localStorage.getItem('carrito');
    if (carrito) {
      setProductos(JSON.parse(carrito));
    }
  }, []);

  useEffect(() => {
    const sub = productos.reduce((acc, p) => acc + p.cantidad * p.precio, 0);
    const ivaCalc = sub * 0.15;
    setSubtotal(sub);
    setIva(ivaCalc);
    setTotal(sub + ivaCalc);
  }, [productos]);

  const limpiarCarro = () => {
    setProductos([]);
    localStorage.removeItem('carrito');
    Notification.show('🗑️ Carrito limpiado correctamente', { theme: 'success', position: 'top-start' });
  };

  return (
    <>
      <style>
        {`
          .modern-grid vaadin-grid-cell-content {
            border-bottom: 1px solid #e5e7eb !important;
            text-align: center !important;
            padding: 12px 8px !important;
            font-weight: 500 !important;
          }
          .modern-grid vaadin-grid thead vaadin-grid-cell-content {
            border-bottom: 3px solid #3b82f6 !important;
            background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%) !important;
            font-weight: 700 !important;
            color: #1e293b !important;
            padding: 16px 8px !important;
          }
          .modern-grid vaadin-grid-row:hover vaadin-grid-cell-content {
            background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%) !important;
            transition: all 0.2s ease !important;
          }
          .modern-grid {
            box-shadow: 0 4px 20px rgba(59, 130, 246, 0.15) !important;
          }
          .summary-card {
            background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
            border: 2px solid #3b82f6;
            border-radius: 12px;
            padding: 1.5em 2em;
            box-shadow: 0 8px 25px rgba(59, 130, 246, 0.2);
            position: relative;
            overflow: hidden;
          }
          .summary-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #3b82f6, #1d4ed8);
          }
          .modern-button-primary {
            background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 100%) !important;
            color: white !important;
            border: none !important;
            box-shadow: 0 4px 12px rgba(14, 165, 233, 0.35) !important;
            transform: translateY(0) !important;
            transition: all 0.3s ease !important;
          }
          .modern-button-primary:hover {
            transform: translateY(-2px) !important;
            box-shadow: 0 6px 20px rgba(2, 132, 199, 0.5) !important;
            background: linear-gradient(135deg, #0284c7 0%, #0369a1 100%) !important;
          }
          .modern-button-danger {
            background: linear-gradient(135deg, #f78282ff 0%, #f87171 100%) !important;
            color: white !important;
            border: none !important;
            box-shadow: 0 4px 12px rgba(252, 165, 165, 0.35) !important;
            transform: translateY(0) !important;
            transition: all 0.3s ease !important;
          }
          .modern-button-danger:hover {
            transform: translateY(-2px) !important;
            box-shadow: 0 6px 20px rgba(248, 113, 113, 0.4) !important;
            background: linear-gradient(135deg, #ff5757ff 0%, #ef4444 100%) !important;
          }
          .quantity-display {
            font-weight: 700 !important;
            color: #1e293b !important;
            font-size: 16px !important;
          }
        `}
      </style>

      <main className="w-full h-full flex flex-col items-center box-border gap-s p-m"
        style={{
          background: "linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)",
          minHeight: "100vh"
        }}>
        <VerticalLayout
          theme="spacing"
          style={{
            maxWidth: 800,
            width: "100%",
            background: "linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)",
            borderRadius: 20,
            boxShadow: "0 10px 40px rgba(0, 0, 0, 0.1)",
            padding: "2.5rem",
            border: "1px solid #e2e8f0"
          }}
        >
          <div style={{ textAlign: "center", marginBottom: "1.5em" }}>
            <h1 style={{
              fontSize: "2.5em",
              fontWeight: "800",
              background: "linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)",
              WebkitBackgroundClip: "text",
              WebkitTextFillColor: "transparent",
              marginBottom: "0"
            }}>
              🛒 Tu Compra
            </h1>
          </div>

          <div style={{ textAlign: "center", marginBottom: "1em" }}>
            <Button
              style={{
                fontSize: "1em",
                padding: "0.6em 1.5em",
                borderRadius: "4px",
                background: "#dbeafe",
                color: "#3b82f6",
                border: "2px solid #3b82f6", 
                fontWeight: "600",
                cursor: "pointer",
                boxShadow: "none",
                transition: "all 0.3s ease"
              }}
              onClick={() => window.location.href = 'http://localhost:8080/producto-list'}
            >
              🛍️ Seguir Comprando
            </Button>
          </div>

          <div style={{
            borderBottom: "3px solid #3b82f6",
            marginBottom: "1em",
            background: "linear-gradient(90deg, #3b82f6, #1d4ed8)",
            height: "3px",
            borderRadius: "2px"
          }}></div>

          <Grid
            items={productos}
            className="modern-grid"
            style={{
              background: "#ffffff",
              borderRadius: 12,
              border: "2px solid #3b82f6",
              overflow: "hidden"
            }}
          >
            <GridColumn
              header={<div style={{ textAlign: 'center' }}><b>Cantidad</b></div>}
              renderer={({ item }) => (
                <span className="quantity-display">{item.cantidad}</span>
              )}
            />
            <GridColumn
              header={<div style={{ textAlign: 'center' }}><b>Producto</b></div>}
              path="nombre"
              flexGrow={4}
            />
            <GridColumn
              header={<div style={{ textAlign: 'center' }}><b>Precio Unitario</b></div>}
              path="precio"
              renderer={({ item }) => <span style={{ fontWeight: '600', color: '#059669' }}>${item.precio.toFixed(2)}</span>}
            />
            <GridColumn
              header={<div style={{ textAlign: 'center' }}><b>Precio Total</b></div>}
              renderer={({ item }) => <span style={{ fontWeight: '700', color: '#1d4ed8' }}>${(item.cantidad * item.precio).toFixed(2)}</span>}
            />
          </Grid>

          <HorizontalLayout theme="spacing" style={{ justifyContent: 'flex-end', width: '100%', marginTop: "2em" }}>
            <div className="summary-card" style={{ minWidth: 280 }}>
              <div style={{
                fontSize: "1.2em",
                marginBottom: 12,
                color: "#475569",
                fontWeight: "600"
              }}>
                Subtotal: <span style={{ color: "#059669", fontWeight: "700" }}>${subtotal.toFixed(2)}</span>
              </div>
              <div style={{
                fontSize: "1.2em",
                marginBottom: 12,
                color: "#475569",
                fontWeight: "600"
              }}>
                IVA (15%): <span style={{ color: "#d97706", fontWeight: "700" }}>${iva.toFixed(2)}</span>
              </div>
              <div style={{
                borderTop: "2px solid #3b82f6",
                paddingTop: "12px",
                marginTop: "12px"
              }}>
                <div style={{
                  fontSize: "1.4em",
                  color: "#1e293b",
                  fontWeight: "800"
                }}>
                  Total: <span style={{ color: "#1d4ed8" }}>${total.toFixed(2)}</span>
                </div>
              </div>
            </div>
          </HorizontalLayout>

          <HorizontalLayout theme="spacing" style={{ justifyContent: 'center', width: '100%', marginTop: "2.5em", gap: "2em" }}>
            <Button
              className="modern-button-primary"
              style={{ fontSize: "1.2em", padding: "0.8em 2.5em", borderRadius: "12px", fontWeight: "700" }}
              onClick={() => window.location.href = '/pago'}
            >
              💳 Pagar
            </Button>
            <Button
              className="modern-button-danger"
              style={{ fontSize: "1.2em", padding: "0.8em 2.5em", borderRadius: "12px", fontWeight: "700" }}
              onClick={limpiarCarro}
            >
              🗑️ Limpiar Carrito
            </Button>
          </HorizontalLayout>
           {productos.length === 0 && (
            <div style={{
              textAlign: "center",
              padding: "3em 2em",
              color: "#64748b",
              fontSize: "1.1em",
              margin: "2em auto",
              maxWidth: "400px",
              background: "linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)",
              borderRadius: "16px",
              border: "2px solid #cbd5e1",
              boxShadow: "0 4px 12px rgba(0, 0, 0, 0.08)"
            }}>
              <div style={{ fontSize: "2em", marginBottom: "0.5em" }}>🛒</div>
              <p style={{ fontWeight: "600", marginBottom: "0.5em" }}>Tu carrito está vacío</p>
              <p style={{ margin: 0 }}>Agrega productos para continuar con tu compra</p>
            </div>
          )}
        </VerticalLayout>
      </main>
    </>
  );
}