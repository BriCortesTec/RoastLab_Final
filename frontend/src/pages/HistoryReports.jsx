import { useState } from 'react';
import EmptyState from '../components/EmptyState.jsx';
import Icon from '../components/Icon.jsx';
import TemperatureChart from '../components/TemperatureChart.jsx';
import '../styles/reports.css';

export default function HistoryReports({ data, page }) {
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('Todos');
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedRoast, setSelectedRoast] = useState(null);
  const pageSize = 8;
  const filteredRoasts = data.roasts.filter((roast) => {
    const matchesText = `${roast.name} ${roast.lot} ${roast.profile}`.toLowerCase().includes(search.toLowerCase());
    return matchesText && (status === 'Todos' || roast.status === status);
  });
  const pageCount = Math.max(1, Math.ceil(filteredRoasts.length / pageSize));
  const visibleRoasts = filteredRoasts.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  if (page === 'reports') {
    const roastedCoffee = data.roasts.reduce((sum, roast) => sum + Number(roast.output || 0), 0);
    const averageLoss = data.roasts.length
      ? Math.round(data.roasts.reduce((sum, roast) => sum + (100 * (roast.input - roast.output) / roast.input), 0) / data.roasts.length)
      : 0;

    return <div className="reports-grid">
      <section className="panel report-highlight"><span className="eyebrow">RESUMEN DE PRODUCCIÓN</span><h2>Resultados de tus tuestes</h2><p>Una vista general de las tandas registradas.</p>
        <div className="report-numbers"><div><b>{data.roasts.length}</b><span>Tuestes registrados</span></div><div><b>{roastedCoffee.toFixed(1)} kg</b><span>Café tostado</span></div><div><b>{averageLoss}%</b><span>Merma promedio</span></div></div>
      </section>
      <section className="panel report-panel"><h3>Tueste por nivel</h3>{data.profiles.map((profile, index) => <div className="bar-row" key={profile.id}><span>{profile.level}</span><div><i style={{ width: `${[72, 45, 28][index % 3]}%` }} /></div><b>{data.roasts.filter((roast) => roast.profile === profile.name).length}</b></div>)}<small className="report-note">Los datos de esta vista son de demostración.</small></section>
      <section className="panel report-panel"><h3>Inventario por origen</h3>{data.lots.map((lot) => <div className="inventory-row" key={lot.id}><div><b>{lot.origin}</b><small>{lot.code} · {lot.coffee}</small></div><strong>{Number(lot.weight).toFixed(1)} kg</strong></div>)}</section>
    </div>;
  }

  return <>
    <section className="panel data-panel">
      <div className="panel-head data-panel-head"><div><h3>Historial de tuestes</h3><p>Consulta tus tandas y sus curvas de temperatura.</p></div><div className="history-count">{filteredRoasts.length} registros</div></div>
      <div className="table-tools"><label className="search-box"><Icon name="search" size={17} /><input placeholder="Buscar tueste, lote o perfil…" value={search} onChange={(event) => { setSearch(event.target.value); setCurrentPage(1); }} /></label><select className="input status-filter" value={status} onChange={(event) => { setStatus(event.target.value); setCurrentPage(1); }}><option>Todos</option><option>Completado</option><option>Programado</option></select></div>
      <div className="table-scroll"><table className="history-table"><thead><tr><th>Tueste</th><th>Fecha</th><th>Lote</th><th>Perfil</th><th>Entrada</th><th>Salida</th><th>Duración</th><th>Estado</th><th>Curva</th></tr></thead>
        <tbody>{visibleRoasts.map((roast) => <tr key={roast.id}><td><b>{roast.name}</b></td><td>{roast.date}</td><td>{roast.lot}</td><td>{roast.profile || '—'}</td><td>{roast.input} kg</td><td>{roast.output} kg</td><td>{roast.duration} min</td><td><span className="status status-done">{roast.status}</span></td><td><button className="curve-link" type="button" disabled={!roast.measurements?.length} onClick={() => setSelectedRoast(roast)}>{roast.measurements?.length ? 'Ver curva' : 'Sin lecturas'}</button></td></tr>)}
          {!visibleRoasts.length && <tr><td colSpan="9"><EmptyState title="No hay tuestes" detail="Ajusta los filtros o registra una tanda nueva." /></td></tr>}
        </tbody>
      </table></div>
      <div className="table-footer"><span>Página {currentPage} de {pageCount}</span><div><button className="button button-outline button-small" disabled={currentPage <= 1} onClick={() => setCurrentPage(currentPage - 1)}>Anterior</button><button className="button button-outline button-small" disabled={currentPage >= pageCount} onClick={() => setCurrentPage(currentPage + 1)}>Siguiente</button></div></div>
    </section>

    {selectedRoast && <div className="modal-overlay" onMouseDown={(event) => { if (event.target === event.currentTarget) setSelectedRoast(null); }}><section className="modal-card curve-modal"><div className="curve-modal-header"><div><span className="eyebrow">CURVA GUARDADA</span><h3>{selectedRoast.name}</h3><p>{selectedRoast.date} · {selectedRoast.lot} · Perfil: {selectedRoast.profile || 'sin perfil'}</p></div><button className="icon-button" type="button" aria-label="Cerrar curva" onClick={() => setSelectedRoast(null)}><Icon name="close" /></button></div><TemperatureChart points={selectedRoast.measurements} events={selectedRoast.events || []} elapsed={selectedRoast.duration * 60} emptyLabel="Este registro no tiene lecturas guardadas." />{selectedRoast.events?.length > 0 && <div className="saved-event-list">{selectedRoast.events.map((event) => <span className={`saved-event-chip saved-event-chip-${event.type}`} key={event.type}><b>{event.short || event.type.toUpperCase()}</b> {Math.floor(event.seconds / 60).toString().padStart(2, '0')}:{(event.seconds % 60).toString().padStart(2, '0')}{event.temperature !== null ? ` · ${event.temperature} °C` : ''}</span>)}</div>}<div className="curve-summary"><span>{selectedRoast.measurements.length} lecturas</span><span>Máxima: {selectedRoast.maxTemp} °C</span><span>Duración: {selectedRoast.duration} min</span></div></section></div>}
  </>;
}
