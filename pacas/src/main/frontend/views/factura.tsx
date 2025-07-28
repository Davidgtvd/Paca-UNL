import { Button, HorizontalLayout, Grid, GridColumn } from '@vaadin/react-components';
import { useEffect, useState } from 'react';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';

// Solo mostrar en menú si es CLIENTE
let usuario = null;
try {
  usuario = JSON.parse(localStorage.getItem('sesion_usuario') || '{}');
} catch {}
export const config: ViewConfig = {
  title: 'Factura',
  menu: usuario?.rol === 'CLIENTE' ? {
    icon: 'vaadin:invoice',
    order: 3,
    title: 'Factura',
  } : undefined,
};

type Producto = {
  nombre: string;
  cantidad: number;
  precio: number;
};

type FacturaData = {
  numeroFactura: string;
  fecha: string;
  cliente: string;
  productos: Producto[];
  subtotal: number;
  iva: number;
  total: number;
  metodoPago: string;
  codigoSeguridad: string;
  telefono?: string;
  email?: string;
  direccionEnvio?: string;
  ciudad?: string;
  codigoPostal?: string;
  pais?: string;
  numeroDocumento?: string;
};

export default function FacturaView() {

  const [facturaData, setFacturaData] = useState<FacturaData | null>(null);

  useEffect(() => {
    const facturaGuardada = localStorage.getItem('factura_actual');
    if (facturaGuardada) {
      setFacturaData(JSON.parse(facturaGuardada));
    }
  }, []);

  const imprimirPDF = () => {
    const contenidoPDF = `
      <html>
        <head>
          <meta charset="utf-8">
          <title>Factura ${facturaData?.numeroFactura}</title>
          <style>
            body { font-family: 'Segoe UI', Arial, sans-serif; margin: 0; background: #f3f4f6; color: #222; }
            .container { max-width: 800px; margin: 40px auto; background: #fff; border-radius: 12px; box-shadow: 0 4px 24px rgba(0,0,0,0.08); padding: 32px; }
            .header { text-align: center; border-bottom: 2px solid #6366f1; padding-bottom: 16px; margin-bottom: 32px; }
            .header h1 { color: #6366f1; margin: 0; font-size: 2.2rem; }
            .header p { margin: 8px 0; color: #555; }
            .info-section { display: flex; gap: 24px; margin-bottom: 32px; flex-wrap: wrap; }
            .info-card { flex: 1; background: #f1f5f9; border-radius: 8px; padding: 18px; border: 1px solid #e5e7eb; }
            .info-title { font-weight: 600; color: #374151; margin-bottom: 12px; font-size: 1.1rem; }
            .info-item { margin: 8px 0; display: flex; justify-content: space-between; }
            .info-item strong { color: #374151; }
            .productos-table { width: 100%; border-collapse: collapse; margin: 24px 0; }
            .productos-table th, .productos-table td { border: 1px solid #e5e7eb; padding: 12px; text-align: center; }
            .productos-table th { background: #6366f1; color: #fff; font-weight: 600; }
            .productos-table td { background: #fff; }
            .totales { margin-top: 32px; padding: 20px; background: #e0f2fe; border-radius: 8px; }
            .total-item { display: flex; justify-content: space-between; padding: 8px 0; font-size: 1.1rem; }
            .total-final { border-top: 2px solid #0ea5e9; margin-top: 10px; padding-top: 10px; font-weight: bold; font-size: 1.3rem; }
            .footer { margin-top: 40px; text-align: center; padding: 20px; background: #fef3c7; border-radius: 8px; }
            @media print { body { margin: 0; } .no-print { display: none; } }
            @media (max-width: 600px) {
              .container { padding: 12px; }
              .info-section { flex-direction: column; gap: 12px; }
            }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="header">
              <h1>🧾 FACTURA</h1>
              <p>Número: ${facturaData?.numeroFactura || 'N/A'}</p>
              <p>Fashion Store Online</p>
            </div>
            <div class="info-section">
              <div class="info-card">
                <div class="info-title">Información de Factura</div>
                <div class="info-item"><span>Número:</span><strong>${facturaData?.numeroFactura || 'N/A'}</strong></div>
                <div class="info-item"><span>Fecha:</span><span>${facturaData?.fecha || new Date().toLocaleDateString()}</span></div>
              </div>
              <div class="info-card">
                <div class="info-title">Información del Cliente</div>
                <div class="info-item"><span>Nombre:</span><strong>${facturaData?.cliente || 'Cliente Online'}</strong></div>
                <div class="info-item"><span>Teléfono:</span><span>${facturaData?.telefono && facturaData.telefono.trim() !== '' ? facturaData.telefono : 'No registrado'}</span></div>
                <div class="info-item"><span>Documento:</span><span>${facturaData?.numeroDocumento || ''}</span></div>
                <div class="info-item"><span>Dirección:</span><span>${facturaData?.direccionEnvio || 'Av. Principal 123'}</span></div>
                <div class="info-item"><span>Ciudad:</span><span>${facturaData?.ciudad || 'Loja'}, ${facturaData?.pais || 'Ecuador'}</span></div>
              </div>
            </div>
            <table class="productos-table">
              <thead>
                <tr>
                  <th>Cantidad</th>
                  <th>Producto</th>
                  <th>Precio Unitario</th>
                  <th>Total</th>
                </tr>
              </thead>
              <tbody>
                ${facturaData?.productos?.map(producto => `
                  <tr>
                    <td>${producto.cantidad}</td>
                    <td>${producto.nombre}</td>
                    <td>$${producto.precio.toFixed(2)}</td>
                    <td>$${(producto.cantidad * producto.precio).toFixed(2)}</td>
                  </tr>
                `).join('') || '<tr><td colspan="4">No hay productos</td></tr>'}
              </tbody>
            </table>
            <div class="totales">
              <div class="total-item">
                <span>Subtotal:</span>
                <span>$${facturaData?.subtotal?.toFixed(2) || '0.00'}</span>
              </div>
              <div class="total-item">
                <span>IVA (15%):</span>
                <span>$${facturaData?.iva?.toFixed(2) || '0.00'}</span>
              </div>
              <div class="total-item total-final">
                <span>TOTAL:</span>
                <span>$${facturaData?.total?.toFixed(2) || '0.00'}</span>
              </div>
            </div>
            <div class="footer">
              <p><strong>¡Gracias por su compra! 🙏</strong></p>
              <p>Factura generada el ${new Date().toLocaleString()}</p>
            </div>
          </div>
        </body>
      </html>
    `;
    const ventanaImpresion = window.open('', '_blank');
    if (ventanaImpresion) {
      ventanaImpresion.document.write(contenidoPDF);
      ventanaImpresion.document.close();
      ventanaImpresion.onload = () => {
        setTimeout(() => {
          ventanaImpresion.print();
        }, 500);
      };
    } else {
      alert('Por favor, permita las ventanas emergentes para imprimir la factura.');
    }
  };

  const cancelarFactura = () => {
    console.log("Factura cancelada.");
  };

  return (
    <>
      <style>
        {`
          .factura-background {
            background: linear-gradient(135deg, #f3f4f6 0%, #e0e7ff 100%);
            min-height: 100vh;
            padding: 2rem 1rem;
          }
          .factura-container {
            max-width: 900px;
            margin: 0 auto;
            background: white;
            border-radius: 16px;
            box-shadow: 0 6px 32px rgba(99,102,241,0.08);
            overflow: hidden;
            border: 1px solid #e5e7eb;
          }
          .factura-header {
            background: #6366f1;
            padding: 2rem;
            text-align: center;
            color: white;
            border-radius: 16px 16px 0 0;
          }
          .factura-header h1 {
            margin: 0;
            font-size: 2.5rem;
            font-weight: 700;
            color: white;
          }
          .factura-header p {
            margin: 0.5rem 0 0 0;
            font-size: 1.1rem;
            color: rgba(255,255,255,0.9);
            font-weight: 400;
          }
          .factura-content {
            padding: 2.5rem;
          }
          .factura-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
            margin-bottom: 3rem;
          }
          .info-card {
            background: #f1f5f9;
            border: 1px solid #e5e7eb;
            border-radius: 8px;
            padding: 1.5rem;
            transition: box-shadow 0.2s ease;
          }
          .info-card:hover {
            box-shadow: 0 4px 12px rgba(99,102,241,0.08);
          }
          .info-title {
            font-size: 1rem;
            font-weight: 600;
            color: #374151;
            margin-bottom: 1rem;
          }
          .info-item {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            padding: 0.75rem 0;
            border-bottom: 1px solid #e5e7eb;
            font-size: 0.95rem;
            gap: 0.5rem;
          }
          .info-item:last-child {
            border-bottom: none;
          }
          .info-item span:first-child {
            flex-shrink: 0;
            min-width: 120px;
            color: #64748b;
            font-weight: 500;
          }
          .info-item span:last-child,
          .info-item strong {
            text-align: right;
            word-break: break-word;
            color: #374151;
            font-weight: 600;
          }
          .productos-grid {
            margin: 2rem 0;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(99,102,241,0.08);
            border: 1px solid #e5e7eb;
            background: white;
          }
          .totales-card {
            background: #e0f2fe;
            border: 1px solid #0ea5e9;
            border-radius: 8px;
            padding: 1.5rem;
            margin: 2rem 0;
          }
          .total-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.75rem 0;
            font-size: 1.1rem;
          }
          .total-item span:first-child {
            color: #0369a1;
            font-weight: 600;
          }
          .total-final {
            border-top: 2px solid #0ea5e9;
            margin-top: 1rem;
            padding-top: 1rem;
            font-size: 1.4rem;
            font-weight: 800;
            color: #1e293b;
          }
          .action-buttons {
            margin: 3rem 0 2rem 0;
          }
          .btn-imprimir {
            background: #10b981;
            color: white;
            border: none;
            font-size: 1rem;
            padding: 0.75rem 2rem;
            border-radius: 6px;
            font-weight: 600;
            transition: background-color 0.2s ease;
            cursor: pointer;
          }
          .btn-imprimir:hover {
            background: #059669;
          }
          .btn-cancelar {
            background: #ef4444;
            color: white;
            border: none;
            font-size: 1rem;
            padding: 0.75rem 2rem;
            border-radius: 6px;
            font-weight: 600;
            transition: background-color 0.2s ease;
            cursor: pointer;
          }
          .btn-cancelar:hover {
            background: #dc2626;
          }
          .thank-you-card {
            margin-top: 1rem;
            text-align: center;
            padding: 0.7rem 1rem;
            background: #fef3c7;
            border-radius: 8px;
            border: 1px solid #f59e0b;
            max-width: 350px;
            margin-left: auto;
            margin-right: auto;
          }
          .thank-you-card.compact p {
            margin: 0.2rem 0;
            font-size: 0.9rem;
          }
          @media (max-width: 768px) {
            .factura-background {
              padding: 1rem 0.5rem;
            }
            .factura-info {
              grid-template-columns: 1fr;
              gap: 1rem;
            }
            .factura-content {
              padding: 1.5rem;
            }
            .factura-header {
              padding: 2rem 1.5rem 1.5rem;
            }
            .factura-header h1 {
              font-size: 2.2rem;
            }
          }
        `}
      </style>
      <main className="factura-background">
        <div className="factura-container">
          <div className="factura-header">
            <h1>🧾 FACTURA</h1>
            <p>Número: {facturaData?.numeroFactura || 'N/A'}</p>
          </div>
          <div className="factura-content">
            <div className="factura-info">
              <div className="info-card">
                <div className="info-title">Información de Factura</div>
                <div className="info-item">
                  <span>Número de Factura:</span>
                  <strong>{facturaData?.numeroFactura || 'N/A'}</strong>
                </div>
                <div className="info-item">
                  <span>Fecha:</span>
                  <span>{facturaData?.fecha || new Date().toLocaleDateString()}</span>
                </div>
              </div>
              <div className="info-card">
                <div className="info-title">Información del Cliente</div>
                <div className="info-item">
                  <span>Nombre Completo:</span>
                  <strong>{facturaData?.cliente || 'Cliente Online'}</strong>
                </div>
                <div className="info-item">
                  <span>Teléfono:</span>
                  <span>{facturaData?.telefono && facturaData.telefono.trim() !== '' ? facturaData.telefono : 'No registrado'}</span>
                </div>
                <div className="info-item">
                  <span>Cédula/RUC:</span>
                  <span>{facturaData?.numeroDocumento || ''}</span>
                </div>
                <div className="info-item">
                  <span>Dirección de Envío:</span>
                  <span style={{ textAlign: 'right', maxWidth: '200px' }}>
                    {facturaData?.direccionEnvio || 'Av. Principal 123, Sector Centro'}
                  </span>
                </div>
                <div className="info-item">
                  <span>Ciudad:</span>
                  <span>{facturaData?.ciudad || 'Loja'}, {facturaData?.pais || 'Ecuador'}</span>
                </div>
              </div>
            </div>
            <Grid
              items={facturaData?.productos ?? []}
              className="productos-grid"
            >
              <GridColumn
                header={<div style={{ textAlign: 'center', fontWeight: 'bold', color: '#6366f1' }}>Cantidad</div>}
                renderer={({ item }) => (
                  <span style={{ fontWeight: '700', color: '#374151', textAlign: 'center', display: 'block', padding: '0.5rem' }}>
                    {item.cantidad}
                  </span>
                )}
              />
              <GridColumn
                header={<div style={{ textAlign: 'center', fontWeight: 'bold', color: '#6366f1' }}>Producto</div>}
                path="nombre"
                flexGrow={4}
                renderer={({ item }) => (
                  <span style={{ fontWeight: '600', color: '#374151', padding: '0.5rem' }}>
                    {item.nombre}
                  </span>
                )}
              />
              <GridColumn
                header={<div style={{ textAlign: 'center', fontWeight: 'bold', color: '#6366f1' }}>Precio Unitario</div>}
                renderer={({ item }) => (
                  <span style={{ fontWeight: '600', color: '#059669', textAlign: 'center', display: 'block', padding: '0.5rem' }}>
                    ${item.precio.toFixed(2)}
                  </span>
                )}
              />
              <GridColumn
                header={<div style={{ textAlign: 'center', fontWeight: 'bold', color: '#6366f1' }}>Total</div>}
                renderer={({ item }) => (
                  <span style={{ fontWeight: '700', color: '#1d4ed8', textAlign: 'center', display: 'block', padding: '0.5rem' }}>
                    ${(item.cantidad * item.precio).toFixed(2)}
                  </span>
                )}
              />
            </Grid>
            <div className="totales-card">
              <div className="total-item">
                <span>Subtotal:</span>
                <span style={{ color: '#059669', fontWeight: '600' }}>
                  ${facturaData?.subtotal?.toFixed(2) ?? '0.00'}
                </span>
              </div>
              <div className="total-item">
                <span>IVA (15%):</span>
                <span style={{ color: '#d97706', fontWeight: '600' }}>
                  ${facturaData?.iva?.toFixed(2) ?? '0.00'}
                </span>
              </div>
              <div className="total-item total-final">
                <span>TOTAL:</span>
                <span>${facturaData?.total?.toFixed(2) ?? '0.00'}</span>
              </div>
            </div>
            <div className="action-buttons">
              <HorizontalLayout theme="spacing" style={{ justifyContent: 'center', gap: '2rem' }}>
                <Button
                  className="btn-imprimir"
                  onClick={imprimirPDF}
                >
                  🖨️ Imprimir PDF
                </Button>
                <Button
                  className="btn-cancelar"
                  onClick={cancelarFactura}
                >
                  ❌ Cancelar
                </Button>
              </HorizontalLayout>
            </div>
            <div className="thank-you-card compact">
              <p style={{ margin: 0, fontSize: '1rem', color: '#92400e', fontWeight: 600 }}>¡Gracias por su compra! 🙏</p>
              <p style={{ margin: '0.3rem 0 0 0', fontSize: '0.8rem', color: '#a16207' }}>Generada el {new Date().toLocaleString()}</p>
            </div>
          </div>
        </div>
      </main>
    </>
  );
}