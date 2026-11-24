import Icon from './Icon.jsx';
import { NAVIGATION, PAGE_LABELS, ROLE_NAMES } from '../data/navigation.js';
import '../styles/layout.css';
export default function Sidebar({ user, activePage, onNavigate, onLogout, isOpen, onClose }) {
  const pages = NAVIGATION[user.role] || NAVIGATION.tostador;

  return (
    <>
      {isOpen && (
        <button
          className="mobile-backdrop"
          aria-label="Cerrar menú"
          onClick={onClose}
        />
      )}

      <aside className={`sidebar ${isOpen ? 'sidebar-open' : ''}`}>
        <div className="brand brand-sidebar">
          <span className="brand-icon">
            <Icon name="bean" size={25} />
          </span>
          <span>
            Roast<span className="brand-highlight">Lab</span>
          </span>
        </div>

        <div className="workspace">
          <span className="workspace-icon">R</span>
          <div>
            <b>RoastLab Café</b>
            <small>Espacio de trabajo</small>
          </div>
          <span className="workspace-chevron">⌄</span>
        </div>

        <div className="side-caption">MENÚ PRINCIPAL</div>

        <nav className="side-nav">
          {pages.map((page) => (
            <button
              key={page}
              className={`nav-item ${activePage === page ? 'nav-active' : ''}`}
              onClick={() => onNavigate(page)}
            >
              <Icon
                name={
                  page === 'dashboard'
                    ? 'grid'
                    : page === 'cafes'
                      ? 'bean'
                      : page === 'lots'
                        ? 'layers'
                        : page === 'profiles'
                          ? 'sliders'
                          : page === 'roasts'
                            ? 'flame'
                            : page === 'history'
                              ? 'clock'
                              : 'chart'
                }
              />
              <span>{PAGE_LABELS[page]}</span>
              {page === 'roasts' && <span className="nav-plus">+</span>}
            </button>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <div className="side-help">
            <div className="help-mark">?</div>
            <b>¿Necesitas ayuda?</b>
            <p>Aprende a sacar el mejor provecho de RoastLab.</p>
            <button onClick={() => window.alert('Pronto encontrarás aquí la guía de RoastLab.')}>
              Ver guía <Icon name="arrow" size={15} />
            </button>
          </div>

          <button className="user-card" onClick={onLogout}>
            <span className="avatar">{user.name?.[0]?.toUpperCase() || 'N'}</span>
            <span className="user-meta">
              <b>
                {user.name} {user.surname || ''}
              </b>
              <small>{ROLE_NAMES[user.role] || 'Tostador'}</small>
            </span>
            <Icon name="logout" size={17} />
          </button>
        </div>
      </aside>
    </>
  );
}