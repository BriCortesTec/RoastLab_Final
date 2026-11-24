import Icon from '../components/Icon.jsx';
import EmptyState from '../components/EmptyState.jsx';
import '../styles/dashboard.css';
export default function Dashboard({ user, data, navigate }) {
  const completed = data.roasts.filter((item) => item.status === 'Completado').length;
  const totalStock = data.lots.reduce((sum, item) => sum + Number(item.weight || 0), 0);
  const quick = [
    {
      label: 'Registrar tueste',
      detail: 'Inicia un nuevo registro',
      icon: 'flame',
      page: 'roasts',
      className: 'quick-green'
    },
    {
      label: 'Agregar lote',
      detail: 'Actualiza tu inventario',
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
          <button className="button button-white" onClick={() => navigate('roasts')}>
            Registrar un tueste <Icon name="arrow" size={17} />
          </button>
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

      <div className="stat-grid">
        <StatCard
          label="Tueste registrados"
          value={completed}
          change="En total"
          icon="flame"
        />
        <StatCard
          label="Café disponible"
          value={`${totalStock.toFixed(1)} kg`}
          change={`${data.lots.length} lotes activos`}
          icon="layers"
        />
        <StatCard
          label="Perfiles guardados"
          value={data.profiles.length}
          change="Listos para usar"
          icon="sliders"
        />
        <StatCard
          label="Cafés de origen"
          value={data.cafes.length}
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
            {data.roasts.slice(0, 4).map((item, index) => (
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
            {data.roasts.length === 0 && (
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
            {quick.map((item) => (
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