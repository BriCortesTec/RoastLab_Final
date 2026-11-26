import { useCallback, useEffect, useState } from 'react';
import CatalogPage from '../components/CatalogPage.jsx';
import { resourceService } from '../services/api.js';

const PAGE_SIZE = 6;
const API_MODE = Boolean(import.meta.env.VITE_API_URL);
const EVENT_LABELS = {
  charge: ['CARGA', 'Carga'],
  tp: ['TP', 'Punto de inflexión'],
  dry_end: ['FIN SECADO', 'Fin del secado'],
  fc_start: ['PRIMER CRACK', 'Inicio del primer crack'],
  sc_start: ['SEGUNDO CRACK', 'Inicio del segundo crack'],
  drop: ['CAÍDA', 'Caída / descarga'],
};

const fromApi = (profile) => {
  const measurements = profile.measurements || [];
  return {
    ...profile,
    duration: Number(profile.duration || 0),
    measurements,
    events: measurements.filter((point) => point.eventType).map((point) => {
      const [short, label] = EVENT_LABELS[point.eventType] || [point.eventType, point.eventType];
      return { type: point.eventType, short, label, seconds: point.seconds, temperature: point.temperature };
    }),
    adjustments: profile.adjustments || [],
  };
};

export default function Profiles({ items, lots: localLots = [], onSave, notify, user }) {
  const [profiles, setProfiles] = useState([]);
  const [lots, setLots] = useState(localLots);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState({ totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(API_MODE);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const loadProfiles = useCallback(async (signal, requestedPage = page) => {
    setLoading(true);
    setError('');
    try {
      const result = await resourceService.list('perfiles', {
        page: requestedPage, size: PAGE_SIZE, search, signal,
      });
      setProfiles((result.content || []).map(fromApi));
      setPageInfo({
        totalElements: result.totalElements ?? result.content?.length ?? 0,
        totalPages: result.totalPages ?? 1,
      });
    } catch (requestError) {
      if (requestError.name !== 'CanceledError') setError(requestError.message);
    } finally {
      if (!signal?.aborted) setLoading(false);
    }
  }, [page, search]);

  useEffect(() => {
    setLots(localLots);
  }, [localLots]);

  useEffect(() => {
    if (!API_MODE) return undefined;
    const controller = new AbortController();
    resourceService.list('lotes', { page: 0, size: 100, signal: controller.signal })
      .then((result) => { if (!controller.signal.aborted) setLots(result.content || []); })
      .catch((requestError) => { if (!controller.signal.aborted) setError(requestError.message); });
    const timer = window.setTimeout(() => loadProfiles(controller.signal), 250);
    return () => {
      window.clearTimeout(timer);
      controller.abort();
    };
  }, [loadProfiles]);

  const saveProfile = async (profile, editing) => {
    const payload = {
      name: profile.name,
      description: profile.description || '',
      flavorNotes: profile.flavorNotes || '',
      referenceLotId: profile.referenceLotId,
      referenceLotCode: profile.referenceLotCode,
      referenceInputKg: profile.referenceInputKg,
      referenceOutputKg: profile.referenceOutputKg,
      initialTemp: profile.initialTemp,
      finalTemp: profile.finalTemp,
      duration: profile.duration,
      level: profile.level,
      measurements: profile.measurements || [],
      // Los IDs temporales creados en el navegador (Date.now) no son IDs de BD
      // y pueden exceder el Integer que espera AjustePerfilDTO.
      adjustments: (profile.adjustments || []).map(({ type, label, value, unit, seconds, temperature }) => ({
        type, label, value: Number(value), unit: unit || '', seconds, temperature,
      })),
    };
    setSaving(true);
    try {
      if (editing) await resourceService.update('perfiles', editing.id, payload);
      else await resourceService.create('perfiles', payload);
      if (!editing) setPage(0);
      await loadProfiles(undefined, editing ? page : 0);
      notify(editing ? 'Perfil actualizado.' : 'Perfil agregado.');
    } finally {
      setSaving(false);
    }
  };

  const deleteProfile = async (profile) => {
    await resourceService.remove('perfiles', profile.id);
    if (profiles.length === 1 && page > 0) setPage((current) => current - 1);
    else await loadProfiles();
    notify('Perfil eliminado.');
  };

  const canManage = ['admin', 'gerente'].includes(user?.role);

  if (!API_MODE) {
    return <CatalogPage kind="profiles" items={items} relatedLots={lots} onSave={onSave} notify={notify} />;
  }

  return (
    <CatalogPage
      kind="profiles"
      items={profiles}
      relatedLots={lots}
      notify={notify}
      canManage={canManage}
      remoteMode
      remoteSearch={search}
      onRemoteSearch={(value) => { setSearch(value); setPage(0); }}
      remotePage={page}
      remoteTotalElements={pageInfo.totalElements}
      remoteTotalPages={pageInfo.totalPages}
      onRemotePageChange={setPage}
      remoteLoading={loading}
      remoteSaving={saving}
      remoteError={error}
      onRemoteSave={saveProfile}
      onRemoteDelete={deleteProfile}
    />
  );
}
