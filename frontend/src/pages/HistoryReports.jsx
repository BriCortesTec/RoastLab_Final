import { useState } from 'react';
import EmptyState from '../components/EmptyState.jsx';
import Icon from '../components/Icon.jsx';
import TemperatureChart from '../components/TemperatureChart.jsx';
import '../styles/reports.css';

export default function HistoryReports({ data, page, onSaveProfile }) {
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('Todos');
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedRoast, setSelectedRoast] = useState(null);
  const [profileSource, setProfileSource] = useState(null);
  const [profileError, setProfileError] = useState('');
  const pageSize = 8;
  const filteredRoasts = data.roasts.filter((roast) => {
    const matchesText = `${roast.name} ${roast.lot} ${roast.profile}`.toLowerCase().includes(search.toLowerCase());
    return matchesText && (status === 'Todos' || roast.status === status);
  });
  const pageCount = Math.max(1, Math.ceil(filteredRoasts.length / pageSize));
  const visibleRoasts = filteredRoasts.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  const saveAsProfile = (event) => {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const measurements = profileSource.measurements || [];
    const name = String(form.get('name') || '').trim();
    if (data.profiles.some((profile) => profile.name.toLowerCase() === name.toLowerCase())) {
      setProfileError('Ya existe un perfil con ese nombre. Escribe otro nombre.');
      return;
    }
    onSaveProfile({
      id: Date.now(),
      name,
      description: String(form.get('description') || '').trim(),
      level: String(form.get('level') || 'Medio'),
      flavorNotes: String(form.get('flavorNotes') || '').trim(),
      initialTemp: measurements[0]?.temperature ?? 0,
      finalTemp: measurements.at(-1)?.temperature ?? 0,
      duration: profileSource.duration,
      sourceRoastId: profileSource.id,
      measurements: [...measurements],
      events: [...(profileSource.events || [])],
      adjustments: [...(profileSource.adjustments || [])],
    });
    setProfileSource(null);
  };

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

    {selectedRoast && <div className="modal-overlay" onMouseDown={(event) => { if (event.target === event.currentTarget) setSelectedRoast(null); }}><section className="modal-card curve-modal"><div className="curve-modal-header"><div><span className="eyebrow">CURVA GUARDADA</span><h3>{selectedRoast.name}</h3><p>{selectedRoast.date} · {selectedRoast.lot} · Perfil: {selectedRoast.profile || 'sin perfil'}</p></div><button className="icon-button" type="button" aria-label="Cerrar curva" onClick={() => setSelectedRoast(null)}><Icon name="close" /></button></div><TemperatureChart points={selectedRoast.measurements} events={selectedRoast.events || []} elapsed={selectedRoast.duration * 60} emptyLabel="Este registro no tiene lecturas guardadas." />{selectedRoast.events?.length > 0 && <div className="saved-event-list">{selectedRoast.events.map((event) => <span className={`saved-event-chip saved-event-chip-${event.type}`} key={event.type}><b>{event.short || event.type.toUpperCase()}</b> {Math.floor(event.seconds / 60).toString().padStart(2, '0')}:{(event.seconds % 60).toString().padStart(2, '0')}{event.temperature !== null ? ` · ${event.temperature} °C` : ''}</span>)}</div>}<div className="curve-summary"><span>{selectedRoast.measurements.length} lecturas</span><span>Máxima: {selectedRoast.maxTemp} °C</span><span>Duración: {selectedRoast.duration} min</span></div>{selectedRoast.measurements?.length > 1 && <div className="roast-actions"><button className="button button-primary" type="button" onClick={() => { setProfileError(''); setProfileSource(selectedRoast); }}>Guardar como perfil</button></div>}{selectedRoast.adjustments?.length > 0 && <div className="machine-adjustment-list machine-adjustment-readonly"><b>Ajustes de máquina</b>{selectedRoast.adjustments.map((adjustment) => <div key={adjustment.id}><b>{Math.floor(adjustment.seconds / 60).toString().padStart(2, '0')}:{(adjustment.seconds % 60).toString().padStart(2, '0')}</b><span>{adjustment.label}: {adjustment.value}</span>{adjustment.temperature !== null && <small>{adjustment.temperature} °C</small>}</div>)}</div>}</section></div>}

    {profileSource && <div className="modal-overlay profile-save-overlay" onMouseDown={(event) => { if (event.target === event.currentTarget) setProfileSource(null); }}><form className="modal-card" onSubmit={saveAsProfile}><div className="modal-header"><div><span className="eyebrow">RECETA REUTILIZABLE</span><h3>Guardar como perfil</h3><p>La bitácora original se conserva en el historial.</p></div><button type="button" className="icon-button" aria-label="Cerrar" onClick={() => setProfileSource(null)}><Icon name="close" /></button></div><div className="modal-fields"><label className="field field-wide">Nombre del perfil<input className="input" name="name" defaultValue={`${profileSource.name} - Perfil`} minLength="3" maxLength="100" required /></label><label className="field">Nivel de tueste<select className="input" name="level" defaultValue="Medio"><option>Claro</option><option>Medio</option><option>Oscuro</option></select></label><label className="field">Descripción<input className="input" name="description" placeholder="Qué quieres repetir de este tueste" /></label><label className="field field-wide">Notas de sabor<textarea className="input textarea" name="flavorNotes" defaultValue={profileSource.observations || ''} rows="3" placeholder="Aroma, dulzor, acidez, cuerpo…" /></label></div>{profileError && <div className="error-note" role="alert">{profileError}</div>}<div className="modal-actions"><button type="button" className="button button-outline" onClick={() => setProfileSource(null)}>Cancelar</button><button className="button button-primary">Guardar perfil y conservar bitácora</button></div></form></div>}
  </>;
}
