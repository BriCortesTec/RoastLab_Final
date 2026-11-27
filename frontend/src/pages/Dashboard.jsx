import { useEffect, useState } from 'react';
import { NAVIGATION } from '../data/navigation.js';
import Icon from '../components/Icon.jsx';
import EmptyState from '../components/EmptyState.jsx';
import { reportService, resourceService } from '../services/api.js';
import '../styles/dashboard.css';

const API_MODE = Boolean(import.meta.env.VITE_API_URL);

export default function Dashboard({ user, data, navigate }) {
  const [remoteData, setRemoteData] = useState(null);
  const [remoteError, setRemoteError] = useState('');

  useEffect(() => {
    if (!API_MODE) return undefined;
    let active = true;
    Promise.all([
      reportService.summary(),
      resourceService.list('lotes', { page: 0, size: 50 }),
      resourceService.list('perfiles', { page: 0, size: 50 }),
      resourceService.list('cafes', { page: 0, size: 50 }),
      resourceService.list('tuestes', { page: 0, size: 4 }),
    ]).then(([summary, lots, profiles, cafes, roasts]) => {
      if (!active) return;
      const recentRoasts = (roasts.content || []).map((roast) => ({
        ...roast,
        date: String(roast.date || '').slice(0, 10),
        lot: roast.lotCode ? `${roast.lotCode} · ${roast.lot || ''}` : roast.lot || '—',
      }));
      setRemoteData({
        summary,
        lots: lots.content || [],
        profiles: profiles.content || [],
        cafes: cafes.content || [],
        roasts: recentRoasts,
      });
    }).catch((error) => {
      if (active) setRemoteError(error.message);
    });
    return () => { active = false; };
  }, []);

  const dashboardData = API_MODE ? remoteData : data;
  const completed = dashboardData
    ? (API_MODE ? Number(dashboardData.summary.totalRoasts || 0) : dashboardData.roasts.filter((item) => item.status === 'Completado').length)
    : '…';
  const totalStock = dashboardData
    ? dashboardData.lots.reduce((sum, item) => sum + Number(item.weight || 0), 0)
    : 0;
  const pages = NAVIGATION[user.role] || NAVIGATION.tostador;
  const quick = [
    {
      label: 'Registrar tueste',
      detail: 'Inicia un nuevo registro',
      icon: 'flame',
      page: 'roasts',
      className: 'quick-green'
    },
    {
      label: user.role === 'tostador' ? 'Consultar lotes' : 'Agregar lote',
      detail: user.role === 'tostador' ? 'Consulta las existencias' : 'Actualiza tu inventario',
      icon: 'layers',
      page: 'lots',
      className: 'quick-gold'
    },
    {
      label: 'Ver historial',
      detail: 'Revisa tus resultados',
      icon: 'clock',
      page: 'history',
      className: 'quick-cream'
    }
  ];

  return (
    <div className="dashboard-content">
      <section className="welcome-card">
        <div>
          <span className="eyebrow">PANEL DE CONTROL</span>
          <h2>
            Buenos días, {user.name} <span>☕</span>
          </h2>
          <p>Todo listo para preparar un gran día de tueste.</p>
          {pages.includes('roasts') && <button className="button button-white" onClick={() => navigate('roasts')}>
            Registrar un tueste <Icon name="arrow" size={17} />
          </button>}
        </div>

        <div className="welcome-art">
          <div className="sun-disc" />
          <div className="coffee-cup">
            <Icon name="coffee" size={57} />
          </div>
          <span className="steam steam-one" />
          <span className="steam steam-two" />
        </div>
      </section>

      {remoteError && <div className="error-note" role="alert">No se pudo cargar el resumen desde la API: {remoteError}</div>}

      <div className="stat-grid">
        <StatCard
          label="Tueste registrados"
          value={completed}
          change="En total"
          icon="flame"
        />
        <StatCard
          label="Café disponible"
          value={dashboardData ? `${totalStock.toFixed(1)} kg` : '…'}
          change={`${dashboardData?.lots.length ?? '…'} lotes activos`}
          icon="layers"
        />
        <StatCard
          label="Perfiles guardados"
          value={dashboardData?.profiles.length ?? '…'}
          change="Listos para usar"
          icon="sliders"
        />
        <StatCard
          label="Cafés de origen"
          value={dashboardData?.cafes.length ?? '…'}
          change="En tu catálogo"
          icon="bean"
        />
      </div>

      <div className="dashboard-grid">
        <section className="panel recent-panel">
          <div className="panel-head">
            <div>
              <h3>Tu actividad reciente</h3>
              <p>Tus últimos registros de tueste</p>
            </div>
            <button className="text-link" onClick={() => navigate('history')}>
              Ver historial <Icon name="arrow" size={15} />
            </button>
          </div>

          <div className="recent-list">
            {(dashboardData?.roasts || []).slice(0, 4).map((item, index) => (
              <div className="recent-row" key={item.id}>
                <span className={`recent-icon roast-color-${index % 3}`}>
                  <Icon name="flame" size={18} />
                </span>
                <div className="recent-main">
                  <b>{item.name}</b>
                  <small>
                    {item.date} · {item.lot.split(' · ')[0]}
                  </small>
                </div>
                <span className="roast-weight">{item.output} kg</span>
                <span className="status status-done">{item.status}</span>
              </div>
            ))}
            {dashboardData && dashboardData.roasts.length === 0 && (
              <EmptyState
                title="Sin tuestes todavía"
                detail="Tu primer registro aparecerá aquí."
              />
            )}
          </div>
        </section>

        <section className="panel quick-panel">
          <div className="panel-head">
            <div>
              <h3>Accesos rápidos</h3>
              <p>Continúa con lo que necesitas</p>
            </div>
          </div>

          <div className="quick-list">
            {quick.filter(item => pages.includes(item.page)).map((item) => (
              <button
                className={`quick-link ${item.className}`}
                key={item.page}
                onClick={() => navigate(item.page)}
              >
                <span className="quick-icon">
                  <Icon name={item.icon} />
                </span>
                <span>
                  <b>{item.label}</b>
                  <small>{item.detail}</small>
                </span>
                <Icon name="arrow" size={17} />
              </button>
            ))}
          </div>

          <div className="tip-card">
            <span>✦ Consejo de tueste</span>
            <p>
              Registra temperatura y duración al finalizar cada tanda para comparar
              perfiles.
            </p>
          </div>
        </section>
      </div>
    </div>
  );
}

function StatCard({ label, value, change, icon }) {
  return (
    <div className="stat-card">
      <div className="stat-card-top">
        <span>{label}</span>
        <span className="stat-icon">
          <Icon name={icon} size={19} />
        </span>
      </div>
      <b className="stat-value">{value}</b>
      <small className="stat-change">{change}</small>
    </div>
  );
}
