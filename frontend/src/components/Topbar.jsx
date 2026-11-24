import Icon from './Icon.jsx';
import '../styles/layout.css';
export default function Topbar({ user, title, demoMode, onOpenMenu, onNotify }) {
  return (
    <header className="topbar">
      <button
        className="icon-button mobile-menu"
        aria-label="Abrir menú"
        onClick={onOpenMenu}
      >
        <Icon name="menu" />
      </button>

      <div className="crumb">
        Workspace <span>/</span> <b>{title}</b>
      </div>

      <div className="top-actions">
        <span className="mode-pill">
          <i /> {demoMode ? 'Demo local' : 'Acceso por API'}
        </span>

        <button
          className="icon-button notification-button"
          aria-label="Notificaciones"
          onClick={onNotify}
        >
          <Icon name="bell" />
          <i />
        </button>

        <div className="top-user">
          <span className="avatar avatar-small">
            {user.name?.[0]?.toUpperCase() || 'N'}
          </span>
          <span>{user.name}</span>
        </div>
      </div>
    </header>
  );
}