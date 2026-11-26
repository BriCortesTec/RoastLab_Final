import Icon from './Icon.jsx';
import '../styles/layout.css';

/**
 * Barra superior de navegación (Topbar).
 * Muestra el botón para controlar el menú lateral, la ruta actual (breadcrumb),
 * el estado de conexión de datos, notificaciones y el perfil rápido del usuario.
 */
export default function Topbar({ user, title, demoMode, onOpenMenu, onNotify }) {
  // Extrae la inicial del nombre en mayúscula para el avatar (usa 'N' por defecto)
  const userInitial = user.name?.[0]?.toUpperCase() || 'N';
  
  // Define la etiqueta según si la app usa datos locales de prueba o conexión real
  const connectionLabel = demoMode ? 'Demo local' : 'Acceso por API';

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

        {/* Ruta actual (Breadcrumb) */}
        <div className="crumb">
          Workspace <span>/</span> <b>{title}</b>
        </div>
      </div>

      {/* Sección derecha: Indicador de entorno, notificaciones y usuario activo */}
      <div className="top-actions">
        {/* Píldora que indica el origen de los datos */}
        <span className="mode-pill">
          <i /> {connectionLabel}
        </span>

        {/* Botón de notificaciones (el <i /> interno suele funcionar como punto indicador) */}
        <button
          className="icon-button notification-button"
          type="button"
          aria-label="Notificaciones"
          onClick={onNotify}
        >
          <Icon name="bell" />
          <i />
        </button>

        {/* Avatar y nombre del usuario conectado */}
        <div className="top-user">
          <span className="avatar avatar-small">{userInitial}</span>
          <span>{user.name}</span>
        </div>
      </div>
    </header>
  );
}