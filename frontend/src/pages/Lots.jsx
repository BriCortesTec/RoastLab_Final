import { useCallback, useEffect, useState } from 'react';
import CatalogPage from '../components/CatalogPage.jsx';
import { resourceService } from '../services/api.js';

const PAGE_SIZE = 6;
const API_MODE = Boolean(import.meta.env.VITE_API_URL);

export default function Lots({ items, cafes, onSave, notify, user }) {
  const [lotsPage, setLotsPage] = useState([]);
  const [cafeOptions, setCafeOptions] = useState([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState({ totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(API_MODE);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const loadLots = useCallback(async (signal, requestedPage = page) => {
    setLoading(true);
    setError('');
    try {
      const [lotsResult, cafesResult] = await Promise.all([
        resourceService.list('lotes', { page: requestedPage, size: PAGE_SIZE, search, signal }),
        resourceService.list('cafes', { page: 0, size: 50, search: '', signal }),
      ]);
      setLotsPage(lotsResult.content || []);
      setCafeOptions(cafesResult.content || []);
      setPageInfo({
        totalElements: lotsResult.totalElements ?? lotsResult.content?.length ?? 0,
        totalPages: lotsResult.totalPages ?? 1,
      });
    } catch (requestError) {
      if (requestError.name !== 'CanceledError') setError(requestError.message);
    } finally {
      if (!signal?.aborted) setLoading(false);
    }
  }, [page, search]);

  useEffect(() => {
    if (!API_MODE) return undefined;
    const controller = new AbortController();
    const timer = window.setTimeout(() => loadLots(controller.signal), 250);
    return () => {
      window.clearTimeout(timer);
      controller.abort();
    };
  }, [loadLots]);

  const saveLot = async (lot, editing) => {
    const payload = {
      code: lot.code,
      cafeId: Number(lot.cafeId),
      density: lot.density,
      humidity: lot.humidity,
      altitude: lot.altitude,
      harvest: lot.harvest || null,
      weight: lot.weight,
    };
    setSaving(true);
    try {
      if (editing) await resourceService.update('lotes', editing.id, payload);
      else await resourceService.create('lotes', payload);
      if (!editing) setPage(0);
      await loadLots(undefined, editing ? page : 0);
      notify(editing ? 'Lote actualizado.' : 'Lote agregado.');
    } finally {
      setSaving(false);
    }
  };

  const deleteLot = async (lot) => {
    await resourceService.remove('lotes', lot.id);
    if (lotsPage.length === 1 && page > 0) setPage((current) => current - 1);
    else await loadLots();
    notify('Lote eliminado.');
  };

  if (!API_MODE) {
    return <CatalogPage kind="lots" items={items} relatedCafes={cafes} onSave={onSave} notify={notify} />;
  }

  return (
    <CatalogPage
      kind="lots"
      items={lotsPage}
      relatedCafes={cafeOptions}
      notify={notify}
      canManage={['admin', 'gerente'].includes(user?.role)}
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
      onRemoteSave={saveLot}
      onRemoteDelete={deleteLot}
    />
  );
}
