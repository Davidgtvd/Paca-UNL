import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, Dialog, Grid, GridColumn, HorizontalLayout, TextArea, TextField, VerticalLayout, Select } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import Envio from 'Frontend/generated/org/unl/pacas/base/models/Envio';
import { EnvioService } from 'Frontend/generated/endpoints';
import { useEffect, useState } from 'react';

export const config: ViewConfig = {
  title: 'Envíos',
  menu: {
    icon: 'vaadin:truck',
    order: 4,
    title: 'Envíos',
  },
};

type EnvioEntryFormProps = {
  onEnvioCreated?: () => void;
};

function EnvioEntryForm(props: EnvioEntryFormProps) {
  const calle = useSignal('');
  const ciudad = useSignal('');
  const provincia = useSignal('');
  const referencia = useSignal('');
  const id_compra = useSignal('');
  const dialogOpened = useSignal(false);

  const createEnvio = async () => {
    try {
      if (!calle.value.trim() || !ciudad.value.trim() || !provincia.value.trim() || !referencia.value.trim() || !id_compra.value.trim()) {
        Notification.show('⚠️ Todos los campos son obligatorios', { duration: 4000, position: 'top-center', theme: 'error' });
        return;
      }
      if (isNaN(Number(id_compra.value)) || Number(id_compra.value) <= 0) {
        Notification.show('⚠️ El ID de compra debe ser un número válido', { duration: 4000, position: 'top-center', theme: 'error' });
        return;
      }
      await EnvioService.createEnvio(
        calle.value.trim(),
        ciudad.value.trim(),
        provincia.value.trim(),
        referencia.value.trim(),
        Number(id_compra.value)
      );
      props.onEnvioCreated?.();
      calle.value = '';
      ciudad.value = '';
      provincia.value = '';
      referencia.value = '';
      id_compra.value = '';
      dialogOpened.value = false;
      Notification.show('✅ Envío registrado con éxito', { duration: 4000, position: 'bottom-end', theme: 'success' });
    } catch (error) {
      Notification.show('❌ Error al registrar el envío', { duration: 4000, position: 'top-center', theme: 'error' });
      handleError(error);
    }
  };

  return (
    <>
      <Dialog
        modeless
        headerTitle="Nuevo Envío"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => { dialogOpened.value = detail.value; }}
        footer={
          <>
            <Button onClick={() => { dialogOpened.value = false; }}>Cancelar</Button>
            <Button onClick={createEnvio} theme="primary">Registrar</Button>
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '20rem', maxWidth: '100%' }}>
          <TextField label="Calle" value={calle.value} required onValueChanged={e => calle.value = e.detail.value} />
          <TextField label="Ciudad" value={ciudad.value} required onValueChanged={e => ciudad.value = e.detail.value} />
          <TextField label="Provincia" value={provincia.value} required onValueChanged={e => provincia.value = e.detail.value} />
          <TextArea label="Referencia" value={referencia.value} required onValueChanged={e => referencia.value = e.detail.value} />
          <TextField label="ID de Compra" value={id_compra.value} required onValueChanged={e => id_compra.value = e.detail.value} />
        </VerticalLayout>
      </Dialog>
      <Button onClick={() => { dialogOpened.value = true; }}>Agregar Envío</Button>
    </>
  );
}

export default function EnvioListView() {
  const [items, setItems] = useState<Envio[]>([]);
  const [filtered, setFiltered] = useState<Envio[]>([]);
  const criterio = useSignal('');
  const texto = useSignal('');

  const callData = () => {
    EnvioService.listAllEnvio().then(data => {
      setItems(data);
      setFiltered(data);
    });
  };

  useEffect(() => {
    callData();
  }, []);

  // Búsqueda simple por ciudad o provincia
  const criteriosBusqueda = [
    { label: 'Ciudad', value: 'ciudad' },
    { label: 'Provincia', value: 'provincia' },
    { label: 'Calle', value: 'calle' },
    { label: 'ID Compra', value: 'id_compra' }
  ];

  const search = () => {
    if (!criterio.value) {
      Notification.show('⚠️ Seleccione un criterio de búsqueda', { duration: 3000, position: 'top-center', theme: 'error' });
      return;
    }
    if (!texto.value.trim()) {
      Notification.show('⚠️ Ingrese un texto para buscar', { duration: 3000, position: 'top-center', theme: 'error' });
      return;
    }
    const filteredList = items.filter((envio: any) => {
      const val = envio[criterio.value];
      return val && val.toString().toLowerCase().includes(texto.value.trim().toLowerCase());
    });
    setFiltered(filteredList);
    Notification.show(`🔎 ${filteredList.length} envío(s) encontrado(s)`, { duration: 3000, position: 'bottom-end', theme: 'success' });
  };

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Gestión de Envíos">
        <Group>
          <EnvioEntryForm onEnvioCreated={callData} />
        </Group>
      </ViewToolbar>

      <HorizontalLayout theme="spacing">
        <Select
          items={criteriosBusqueda}
          value={criterio.value}
          onValueChanged={evt => (criterio.value = evt.detail.value)}
          placeholder="Seleccione un criterio"
        />
        <TextField
          placeholder="Buscar"
          style={{ width: '50%' }}
          value={texto.value}
          onValueChanged={evt => (texto.value = evt.detail.value)}
        />
        <Button onClick={search} theme="primary">BUSCAR</Button>
        <Button onClick={() => setFiltered(items)} theme="secondary">Reset</Button>
      </HorizontalLayout>

      <Grid items={filtered} style={{ height: '500px', marginTop: '16px' }}>
        <GridColumn path="id" header="ID" width="80px" />
        <GridColumn path="calle" header="Calle" />
        <GridColumn path="ciudad" header="Ciudad" />
        <GridColumn path="provincia" header="Provincia" />
        <GridColumn path="referencia" header="Referencia" />
        <GridColumn path="id_compra" header="ID Compra" />
      </Grid>

      {filtered.length === 0 && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666', fontSize: '16px' }}>
          No hay envíos registrados
        </div>
      )}
    </main>
  );
}