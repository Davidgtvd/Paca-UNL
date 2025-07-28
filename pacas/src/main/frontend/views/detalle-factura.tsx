import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, Dialog, Grid, GridColumn, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { DetalleFacturaServices } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import { useEffect, useState } from 'react';

export const config: ViewConfig = {
  title: '',
  menu: {
    icon: 'vaadin:invoice',
    order: 1,
    title: 'Lista de Facturas',
  },
};

// FORMULARIO PARA CREAR DETALLE DE FACTURA (puedes dejarlo igual)
function DetalleFacturaEntryForm({ onDetalleFacturaCreated }) {
  const cantidad = useSignal('');
  const precioUnitario = useSignal('');
  const compra = useSignal('');
  const producto = useSignal('');
  const persona = useSignal('');

  const createDetalleFactura = async () => {
    try {
      if (
        cantidad.value.trim().length > 0 &&
        precioUnitario.value.trim().length > 0 &&
        compra.value.trim().length > 0 &&
        producto.value.trim().length > 0 &&
        persona.value.trim().length > 0
      ) {
        const cantidadNum = parseInt(cantidad.value);
        const precioNum = parseFloat(precioUnitario.value);
        const total = cantidadNum * precioNum;
        const id_compra = parseInt(compra.value) + 1;
        const id_producto = parseInt(producto.value) + 1;
        const id_persona = parseInt(persona.value) + 1;

        await DetalleFacturaServices.createDetalleFactura(
          total,
          cantidadNum,
          precioNum,
          id_compra,
          id_producto,
          id_persona
        );

        if (onDetalleFacturaCreated) {
          onDetalleFacturaCreated();
        }

        cantidad.value = '';
        precioUnitario.value = '';
        compra.value = '';
        producto.value = '';
        persona.value = '';
        dialogOpened.value = false;
        Notification.show('Detalle de factura creado', {
          duration: 5000,
          position: 'bottom-end',
          theme: 'success',
        });
      } else {
        Notification.show('No se pudo crear, faltan datos', {
          duration: 5000,
          position: 'top-center',
          theme: 'error',
        });
      }
    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };

  const dialogOpened = useSignal(false);

  return (
    <>
      <Dialog
        modeless
        headerTitle="Nuevo detalle de factura"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => {
          dialogOpened.value = detail.value;
        }}
        footer={
          <>
            <Button
              onClick={() => {
                dialogOpened.value = false;
              }}
            >
              Cancelar
            </Button>
            <Button onClick={createDetalleFactura} theme="primary">
              Registrar
            </Button>
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          {/* Aquí van los campos del formulario */}
        </VerticalLayout>
      </Dialog>
      <Button
        onClick={() => {
          dialogOpened.value = true;
        }}
      >
        Agregar
      </Button>
    </>
  );
}

// VISTA DE LISTA DE FACTURAS
export default function FacturaListView() {
  // Verificar si el usuario es ADMINISTRADOR
  const usuarioGuardado = localStorage.getItem('sesion_usuario');
  let esAdmin = false;
  if (usuarioGuardado) {
    try {
      const usuario = JSON.parse(usuarioGuardado);
      esAdmin = usuario.rol === "ADMINISTRADOR";
    } catch {}
  }
  if (!esAdmin) {
    return null; // No muestra nada si no es administrador
  }

  const [facturas, setFacturas] = useState([]);
  const cargarFacturas = () => {
    DetalleFacturaServices.listarFacturas().then(setFacturas);
  };
  useEffect(() => {
    cargarFacturas();
  }, []);
  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Lista de Facturas">
        <Group>
          <DetalleFacturaEntryForm onDetalleFacturaCreated={cargarFacturas} />
        </Group>
      </ViewToolbar>
      <Grid items={facturas}>
        <GridColumn path="nro" header="Nro" />
        <GridColumn path="nroFactura" header="Nro Factura" />
        <GridColumn path="fecha" header="Fecha" />
        <GridColumn path="nombrePersona" header="Persona" />
        <GridColumn path="subtotal" header="Subtotal" />
        <GridColumn path="iva" header="IVA" />
        <GridColumn path="total" header="Total" />
      </Grid>
    </main>
  );
}