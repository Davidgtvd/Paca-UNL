import { Button, TextField, ComboBox, Notification } from '@vaadin/react-components';
import { useState } from 'react';
import { PagoServices } from 'Frontend/generated/endpoints';
import { useLocation, useNavigate } from 'react-router';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';

export const config: ViewConfig = {
  title: 'Pago',
  menu: {
    icon: 'vaadin:cart',
    order: 8,
    title: 'Pago',
  },
};

export default function PagoView() {
  const navigate = useNavigate();
  const location = useLocation();

  // Obtener datos de la venta desde la ubicación
  const { productos = [], subtotal = 0, iva = 0, total = 0 } = location.state || {};

  const [datosPago, setDatosPago] = useState({
    codigo_seguridad: '',
    metodoPago: 'TARJETA_DE_CREDITO',
    numeroTarjeta: '',
    fechaExpiracion: '',
    nombreTitular: ''
  });

  const metodosPago = [
    { label: '💳 Tarjeta de Crédito', value: 'TARJETA_DE_CREDITO' },
    { label: '💳 Tarjeta de Débito', value: 'TARJETA_DE_DEBITO' }
  ];

  const handleSubmit = async () => {
    if (!datosPago.codigo_seguridad || datosPago.codigo_seguridad.length < 3) {
      Notification.show('Ingrese un código de seguridad válido', {
        theme: 'error',
        position: 'top-center'
      });
      return;
    }
    if (!datosPago.numeroTarjeta || datosPago.numeroTarjeta.replace(/\s/g, '').length !== 16) {
      Notification.show('El número de tarjeta debe tener 16 dígitos', {
        theme: 'error',
        position: 'top-center'
      });
      return;
    }
    if (!datosPago.nombreTitular.trim()) {
      Notification.show('Ingrese el nombre del titular', {
        theme: 'error',
        position: 'top-center'
      });
      return;
    }
    if (!datosPago.fechaExpiracion.match(/^(0[1-9]|1[0-2])\/?([0-9]{2})$/)) {
      Notification.show('Fecha de expiración inválida', {
        theme: 'error',
        position: 'top-center'
      });
      return;
    }

    try {
      await PagoServices.createPago(
        datosPago.codigo_seguridad,
        datosPago.metodoPago,
        true
      );

      Notification.show('Pago realizado con éxito', {
        theme: 'success',
        position: 'top-center'
      });

      localStorage.removeItem('carrito');
      navigate('/confirmacion');
    } catch (error) {
      Notification.show('Error al procesar el pago', {
        theme: 'error',
        position: 'top-center'
      });
      console.error('Error:', error);
    }
  };

  return (
    <>
      <style>{`
        .pago-container {
          min-height: 100vh;
          background: #f5f7fa;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 2rem;
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        }
        .pago-card {
          max-width: 450px;
          width: 100%;
          background: white;
          border-radius: 12px;
          box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
          overflow: hidden;
          border: 1px solid #e5e7eb;
        }
        .pago-header {
          background: white;
          padding: 1.5rem 2rem;
          border-bottom: 1px solid #e5e7eb;
          display: flex;
          align-items: center;
          gap: 0.75rem;
        }
        .pago-icon {
          background: #fbbf24;
          padding: 0.5rem;
          border-radius: 6px;
          font-size: 1rem;
        }
        .pago-title {
          font-size: 1.25rem;
          font-weight: 600;
          color: #374151;
          margin: 0;
        }
        .pago-content {
          padding: 2rem;
        }
        .total-section {
          background: #dbeafe;
          border: 1px solid #bfdbfe;
          border-radius: 8px;
          padding: 1.5rem;
          text-align: center;
          margin-bottom: 2rem;
        }
        .total-label {
          font-size: 0.875rem;
          color: #6b7280;
          margin-bottom: 0.5rem;
          font-weight: 500;
        }
        .total-amount {
          font-size: 2rem;
          font-weight: 700;
          color: #1d4ed8;
          margin: 0;
        }
        .form-section {
          margin-bottom: 1.5rem;
        }
        .form-label {
          display: block;
          font-size: 0.875rem;
          font-weight: 500;
          color: #374151;
          margin-bottom: 0.5rem;
        }
        .input-row {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 1rem;
        }
        .button-row {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 1rem;
          margin-top: 2rem;
        }
        .btn-cancel {
          background: #6b7280 !important;
          color: white !important;
          border: none !important;
          padding: 0.75rem 1.5rem;
          border-radius: 8px !important;
          font-weight: 500 !important;
          font-size: 0.875rem !important;
          cursor: pointer;
          transition: background-color 0.2s;
        }
        .btn-cancel:hover {
          background: #4b5563 !important;
        }
        .btn-confirm {
          background: #0ea5e9 !important;
          color: white !important;
          border: none !important;
          padding: 0.75rem 1.5rem;
          border-radius: 8px !important;
          font-weight: 500 !important;
          font-size: 0.875rem !important;
          cursor: pointer;
          transition: background-color 0.2s;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 0.5rem;
        }
        .btn-confirm:hover {
          background: #0284c7 !important;
        }
        vaadin-text-field, vaadin-combo-box {
          --lumo-border-radius: 6px;
          --lumo-space-m: 0.75rem;
          margin-bottom: 1rem;
        }
        vaadin-text-field::part(input-field), vaadin-combo-box::part(input-field) {
          background: #f9fafb;
          border: 1px solid #d1d5db;
          font-size: 0.875rem;
        }
        vaadin-text-field:focus-within::part(input-field), 
        vaadin-combo-box:focus-within::part(input-field) {
          border-color: #2563eb;
          box-shadow: 0 0 0 1px #2563eb;
        }
        vaadin-text-field::part(label) {
          font-size: 0.875rem;
          font-weight: 500;
          color: #374151;
        }
        vaadin-combo-box::part(label) {
          font-size: 0.875rem;
          font-weight: 500;
          color: #374151;
        }
        @media (max-width: 640px) {
          .pago-container {
            padding: 1rem;
          }
          .pago-content {
            padding: 1.5rem;
          }
          .pago-header {
            padding: 1rem 1.5rem;
          }
          .input-row {
            grid-template-columns: 1fr;
          }
          .button-row {
            grid-template-columns: 1fr;
          }
          .total-amount {
            font-size: 1.75rem;
          }
        }
      `}</style>

      <div className="pago-container">
        <div className="pago-card">
          <div className="pago-header">
            <span className="pago-icon">💳</span>
            <h1 className="pago-title">Procesar Pago</h1>
          </div>

          <div className="pago-content">
            <div className="total-section">
              <div className="total-label">Total a Pagar</div>
              <div className="total-amount">${total.toFixed(2)}</div>
            </div>

            <div className="form-section">
              <ComboBox
                label="Método de Pago"
                items={metodosPago}
                value={datosPago.metodoPago}
                onValueChanged={(e) => setDatosPago({ ...datosPago, metodoPago: e.detail.value })}
                style={{ width: '100%' }}
              />
            </div>

            <div className="form-section">
              <TextField
                label="Número de Tarjeta"
                pattern="[0-9]{16}"
                value={datosPago.numeroTarjeta}
                onValueChanged={(e) => setDatosPago({ ...datosPago, numeroTarjeta: e.detail.value.replace(/\D/g, '').substring(0, 16) })}
                required
                style={{ width: '100%' }}
                maxlength={16}
                placeholder="1234 5678 9012 3456"
              />
            </div>

            <div className="form-section">
              <TextField
                label="Nombre del Titular"
                value={datosPago.nombreTitular}
                onValueChanged={(e) => setDatosPago({ ...datosPago, nombreTitular: e.detail.value.toUpperCase() })}
                required
                style={{ width: '100%' }}
                placeholder="Nombre completo como aparece en la tarjeta"
              />
            </div>

            <div className="input-row">
              <TextField
                label="Fecha de Vencimiento"
                placeholder="MM/YY"
                pattern="(0[1-9]|1[0-2])\/?([0-9]{2})"
                value={datosPago.fechaExpiracion}
                onValueChanged={(e) => setDatosPago({ ...datosPago, fechaExpiracion: e.detail.value.replace(/[^0-9/]/g, '').substring(0, 5) })}
                required
                maxlength={5}
              />
              <TextField
                label="Código de Seguridad"
                pattern="[0-9]{3,4}"
                value={datosPago.codigo_seguridad}
                onValueChanged={(e) => setDatosPago({ ...datosPago, codigo_seguridad: e.detail.value.replace(/\D/g, '').substring(0, 4) })}
                required
                maxlength={4}
                placeholder="CVV"
              />
            </div>

            <div className="button-row">
              <Button
                className="btn-cancel"
                onClick={() => navigate(-1)}
              >
                Cancelar
              </Button>
              <Button
                className="btn-confirm"
                onClick={handleSubmit}
              >
                ✓ Confirmar Pago
              </Button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}