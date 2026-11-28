import Icon from './Icon.jsx';
import '../styles/layout.css';

/**
 * Barra superior de navegación (Topbar).
 * Muestra el botón del menú, la sección actual y el perfil rápido.
 */
export default function Topbar({ user, title, onOpenMenu }) {
  // Extrae la inicial del nombre en mayúscula para el avatar (usa 'N' por defecto)
  const userInitial = user.name?.[0]?.toUpperCase() || 'N';
  
  return (
    <header className="topbar">
      {/* Sección izquierda: Botón de menú y ruta de navegación */}
      <div className="topbar-start">
        <button
          className="icon-button menu-toggle"
          type="button"
          aria-label="Mostrar u ocultar menú"
          title="Mostrar u ocultar menú"
          onClick={onOpenMenu}
        >
          <Icon name="menu" />
        </button>

        <div className="crumb"><b>{title}</b></div>
      </div>

      {/* Perfil del usuario activo */}
      <div className="top-actions">
        <div className="top-user">
          <span className="avatar avatar-small">{userInitial}</span>
          <span>{user.name}</span>
        </div>
      </div>
    </header>
  );
}
