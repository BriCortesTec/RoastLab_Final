import Icon from './Icon.jsx';
import { NAVIGATION, PAGE_LABELS, ROLE_NAMES } from '../data/navigation.js';
import '../styles/layout.css';

const PAGE_ICONS = {
  dashboard: 'grid',
  cafes: 'bean',
  lots: 'layers',
  profiles: 'sliders',
  roasts: 'flame',
  history: 'clock',
  reports: 'chart',
  users: 'users',
  account: 'userRound',
};

export default function Sidebar({
  user,
  activePage,
  onNavigate,
  onLogout,
  isOpen,
  isCollapsed,
  onClose,
}) {
  const pages = NAVIGATION[user.role] || NAVIGATION.tostador;
  const userInitial = user.name?.[0]?.toUpperCase() || 'N';
  const userFullName = `${user.name} ${user.surname || ''}`.trim();
  const userRoleLabel = ROLE_NAMES[user.role] || 'Tostador';

  const sidebarClasses = [
    'sidebar',
    isOpen ? 'sidebar-open' : '',
    isCollapsed ? 'sidebar-collapsed' : '',
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <>
      {isOpen && (
        <button
          className="mobile-backdrop"
          aria-label="Cerrar menú"
          onClick={onClose}
        />
      )}

      <aside className={sidebarClasses}>
        {/* Marca */}
        <div className="brand brand-sidebar">
          <span className="brand-icon brand-monogram" aria-hidden="true">R</span>
          <span>
            Roast<span className="brand-highlight">Lab</span>
          </span>
        </div>

        {/* Navegación principal */}
        <div className="side-caption">MENÚ PRINCIPAL</div>
        <nav className="side-nav">
          {pages.map((page) => {
            const isActive = activePage === page;
            const label = PAGE_LABELS[page];
            const iconName = PAGE_ICONS[page] || 'chart';

            return (
              <button
                key={page}
                aria-label={label}
                title={isCollapsed ? label : undefined}
                className={`nav-item ${isActive ? 'nav-active' : ''}`}
                onClick={() => onNavigate(page)}
              >
                <Icon name={iconName} />
                <span>{label}</span>
                {page === 'roasts' && <span className="nav-plus">+</span>}
              </button>
            );
          })}
        </nav>

        {/* Sección inferior */}
        <div className="sidebar-bottom">
          <button className="user-card" aria-label="Abrir mi perfil" onClick={() => onNavigate('account')}>
            <span className="avatar">{userInitial}</span>
            <span className="user-meta">
              <b>{userFullName}</b>
              <small>{user.email}</small>
              <small>{userRoleLabel}</small>
            </span>
          </button>
          <button className="nav-item" aria-label="Cerrar sesión" onClick={onLogout}><Icon name="logout" size={17}/><span>Cerrar sesión</span></button>
        </div>
      </aside>
    </>
  );
}
