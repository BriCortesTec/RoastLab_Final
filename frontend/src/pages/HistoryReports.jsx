import { useState } from 'react';
import EmptyState from '../components/EmptyState.jsx';
import Icon from '../components/Icon.jsx';
import '../styles/reports.css';

export default function HistoryReports({ data, page }) {
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('Todos');
  const [current, setCurrent] = useState(1);
  const pageSize = 8;

  const list = data.roasts.filter(
    (r) =>
      `${r.name} ${r.lot} ${r.profile}`
        .toLowerCase()
        .includes(search.toLowerCase()) &&
      (status === 'Todos' || r.status === status)
  );
  const pages = Math.max(1, Math.ceil(list.length / pageSize));
  const rows = list.slice((current - 1) * pageSize, current * pageSize);

  if (page === 'reports') {
    return (
      <div className="reports-grid">
        <div className="panel report-highlight">
          <span className="eyebrow">RESUMEN DE PRODUCCIÓN</span>
          <h2>Resultados de tus tuestes</h2>
          <p>Una vista general de las tandas registradas.</p>
          <div className="report-numbers">
            <div>
              <b>{data.roasts.length}</b>
              <span>Tuestes registrados</span>
            </div>
            <div>
              <b>
                {data.roasts
                  .reduce((sum, r) => sum + Number(r.output || 0), 0)
                  .toFixed(1)}{' '}
                kg
              </b>
              <span>Café tostado</span>
            </div>
            <div>
              <b>
                {data.roasts.length
                  ? Math.round(
                      data.roasts.reduce(
                        (sum, r) => sum + (100 * (r.input - r.output)) / r.input,
                        0
                      ) / data.roasts.length
                    )
                  : 0}
                %
              </b>
              <span>Merma promedio</span>
            </div>
          </div>
        </div>

        <div className="panel report-panel">
          <h3>Tueste por nivel</h3>
          {data.profiles.map((p, i) => (
            <div className="bar-row" key={p.id}>
              <span>{p.level}</span>
              <div>
                <i style={{ width: `${[72, 45, 28][i % 3]}%` }} />
              </div>
              <b>
                {Math.max(
                  0,
                  data.roasts.filter((r) => r.profile === p.name).length
                )}
              </b>
            </div>
          ))}
          <small className="report-note">
            La gráfica se actualizará con los registros reales de la API.
          </small>
        </div>

        <div className="panel report-panel">
          <h3>Inventario por origen</h3>
          {data.lots.map((l) => (
            <div className="inventory-row" key={l.id}>
              <div>
                <b>{l.origin}</b>
                <small>
                  {l.code} · {l.coffee}
                </small>
              </div>
              <strong>{Number(l.weight).toFixed(1)} kg</strong>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <section className="panel data-panel">
      <div className="panel-head data-panel-head">
        <div>
          <h3>Historial de tuestes</h3>
          <p>Consulta y filtra tus tandas anteriores.</p>
        </div>
        <div className="history-count">{list.length} registros</div>
      </div>

      <div className="table-tools">
        <label className="search-box">
          <Icon name="search" size={17} />
          <input
            placeholder="Buscar tueste, lote o perfil…"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setCurrent(1);
            }}
          />
        </label>

        <select
          className="input status-filter"
          value={status}
          onChange={(e) => {
            setStatus(e.target.value);
            setCurrent(1);
          }}
        >
          <option>Todos</option>
          <option>Completado</option>
          <option>Programado</option>
        </select>
      </div>

      <div className="table-scroll">
        <table>
          <thead>
            <tr>
              <th>Tueste</th>
              <th>Fecha</th>
              <th>Lote</th>
              <th>Perfil</th>
              <th>Entrada</th>
              <th>Salida</th>
              <th>Duración</th>
              <th>Estado</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.id}>
                <td>
                  <b>{r.name}</b>
                </td>
                <td>{r.date}</td>
                <td>{r.lot}</td>
                <td>{r.profile || '—'}</td>
                <td>{r.input} kg</td>
                <td>{r.output} kg</td>
                <td>{r.duration} min</td>
                <td>
                  <span className="status status-done">{r.status}</span>
                </td>
              </tr>
            ))}
            {!rows.length && (
              <tr>
                <td colSpan="8">
                  <EmptyState
                    title="No hay tuestes"
                    detail="Ajusta los filtros o registra tu primera tanda."
                  />
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="table-footer">
        <span>
          Página {current} de {pages}
        </span>
        <div>
          <button
            className="button button-outline button-small"
            disabled={current <= 1}
            onClick={() => setCurrent(current - 1)}
          >
            Anterior
          </button>
          <button
            className="button button-outline button-small"
            disabled={current >= pages}
            onClick={() => setCurrent(current + 1)}
          >
            Siguiente
          </button>
        </div>
      </div>
    </section>
  );
}