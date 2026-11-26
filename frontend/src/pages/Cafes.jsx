import { useCallback, useEffect, useState } from 'react';
import CatalogPage from '../components/CatalogPage.jsx';
import { resourceService } from '../services/api.js';

const PAGE_SIZE = 6;
const API_MODE = Boolean(import.meta.env.VITE_API_URL);

export default function Cafes({ items, onSave, notify, user }) {
  const [cafes, setCafes] = useState([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState({ totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(API_MODE);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const loadCafes = useCallback(async (signal, requestedPage = page) => {
    setLoading(true);
    setError('');
    try {
      const result = await resourceService.list('cafes', {
        page: requestedPage,
        size: PAGE_SIZE,
        search,
        signal,
      });
      setCafes(result.content || []);
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
    if (!API_MODE) return undefined;
    const controller = new AbortController();
    const timer = window.setTimeout(() => loadCafes(controller.signal), 250);
    return () => {
      window.clearTimeout(timer);
      controller.abort();
    };
  }, [loadCafes]);

  const saveCafe = async (cafe, editing) => {
    const payload = { ...cafe };
    delete payload.id;
    setSaving(true);
    try {
      if (editing) await resourceService.update('cafes', editing.id, payload);
      else await resourceService.create('cafes', payload);
      if (!editing) setPage(0);
      await loadCafes(undefined, editing ? page : 0);
      notify(editing ? 'Café actualizado.' : 'Café agregado.');
    } finally {
      setSaving(false);
    }
  };

  const deleteCafe = async (cafe) => {
    await resourceService.remove('cafes', cafe.id);
    if (cafes.length === 1 && page > 0) setPage((current) => current - 1);
    else await loadCafes();
    notify('Café eliminado.');
  };

  const canManage = ['admin', 'gerente'].includes(user?.role);

  if (!API_MODE) {
    return <CatalogPage kind="cafes" items={items} onSave={onSave} notify={notify} />;
  }

  return (
    <CatalogPage
      kind="cafes"
      items={cafes}
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
      onRemoteSave={saveCafe}
      onRemoteDelete={deleteCafe}
    />
  );
}
