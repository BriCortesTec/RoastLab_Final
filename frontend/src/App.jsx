import { useEffect, useState } from 'react';
import { INITIAL_DATA } from './data/demoData.js';
import { NAVIGATION, PAGE_LABELS, ROLE_NAMES } from './data/navigation.js';
import Login from './pages/Login.jsx';
import Dashboard from './pages/Dashboard.jsx';
import Cafes from './pages/Cafes.jsx';
import Lots from './pages/Lots.jsx';
import Profiles from './pages/Profiles.jsx';
import RoastForm from './pages/RoastForm.jsx';
import History from './pages/History.jsx';
import Reports from './pages/Reports.jsx';
import Sidebar from './components/Sidebar.jsx';
import Topbar from './components/Topbar.jsx';
import Toast from './components/Toast.jsx';
import Accounts from './pages/Accounts.jsx';
import AuthAction from './pages/AuthAction.jsx';
import { resourceService, authService } from './services/api.js';

function readData() {
  try { return { ...INITIAL_DATA, ...JSON.parse(localStorage.getItem('roastlab_data')) }; }
  catch { return INITIAL_DATA; }
}

export default function App() {
  const authActionPath = window.location.pathname;
  const authActionMode = authActionPath === '/verify-email' ? 'verify-email'
    : authActionPath === '/reset-password' ? 'reset-password' : null;
  const authActionToken = new URLSearchParams(window.location.search).get('token') || '';
  const [user, setUser] = useState(null);
  const [checking, setChecking] = useState(Boolean(localStorage.getItem('roastlab_token')));
  useEffect(() => {
    if (authActionMode) return;
    if (!localStorage.getItem('roastlab_token')) return;
    let current = true;
    authService.me().then(account => { if (current) setUser(account); })
      .catch(error => { if (current && error.status === 401) localStorage.removeItem('roastlab_token'); })
      .finally(() => { if (current) setChecking(false); });
    return () => { current = false; };
  }, []);
  const [page, setPage] = useState('dashboard');
  const [data, setData] = useState(readData);
  const [toastMessage, setToastMessage] = useState('');
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

  const demoMode = !import.meta.env.VITE_API_URL;
  const notify = (message) => {
    setToastMessage(message);
    window.setTimeout(() => setToastMessage(''), 2800);
  };

  const saveData = (updatedData) => {
    setData(updatedData);
    localStorage.setItem('roastlab_data', JSON.stringify(updatedData));
  };

  const signIn = (account) => {
    setUser(account);
    setPage('dashboard');
  };

  const signOut = async () => {
    try { await authService.logout(); }
    catch { /* Cierra la sesión local incluso si se perdió la conexión. */ }
    finally {
      localStorage.removeItem('roastlab_token');
      sessionStorage.removeItem('roastlab_user');
      setUser(null);
    }
  };

  const navigate = (nextPage) => {
    setPage(nextPage);
    setMobileMenuOpen(false);
  };

  const toggleMenu = () => {
    if (window.matchMedia('(max-width: 740px)').matches) {
      setMobileMenuOpen((open) => !open);
      return;
    }
    setSidebarCollapsed((collapsed) => !collapsed);
  };

  if (authActionMode) return <AuthAction mode={authActionMode} token={authActionToken} />;
  if (checking) return <p role="status">Verificando sesión…</p>;
  if (!user) return <Login onLogin={signIn} />;

  const allowedPages = NAVIGATION[user.role] || NAVIGATION.tostador;
  const activePage = allowedPages.includes(page) ? page : 'dashboard';
  const pageTitle = PAGE_LABELS[activePage];
  let pageContent;

  if (activePage === 'users' || activePage === 'account') {
    pageContent = <Accounts key={activePage} personal={activePage === 'account'} user={user} onProfile={setUser} notify={notify} />;
  } else if (activePage === 'dashboard') {
    pageContent = <Dashboard user={user} data={data} navigate={navigate} />;
  } else if (activePage === 'cafes') {
    pageContent = <Cafes items={data.cafes} onSave={(cafes) => saveData({ ...data, cafes })} notify={notify} user={user} />;
  } else if (activePage === 'lots') {
    pageContent = <Lots items={data.lots} cafes={data.cafes} onSave={(lots) => saveData({ ...data, lots })} notify={notify} user={user} />;
  } else if (activePage === 'profiles') {
    pageContent = <Profiles items={data.profiles} lots={data.lots} onSave={(profiles) => saveData({ ...data, profiles })} notify={notify} user={user} />;
  } else if (activePage === 'roasts') {
    pageContent = <RoastForm data={data} onSave={async (roast) => {
      if (demoMode) {
        const lots = data.lots.map((lot) => lot.code === roast.lotCode
          ? { ...lot, weight: Number((Number(lot.weight) - roast.input).toFixed(2)) }
          : lot);
        saveData({ ...data, lots, roasts: [roast, ...data.roasts] });
      } else {
        await resourceService.create('tuestes', {
          name: roast.name,
          date: roast.date,
          lot: roast.lot,
          lotCode: roast.lotCode,
          profileId: roast.profileId,
          profile: roast.profile,
          duration: roast.duration,
          input: roast.input,
          output: roast.output,
          maxTemp: roast.maxTemp,
          status: roast.status,
          observations: roast.observations,
          lots: [{ lotId: roast.lotId, quantityKg: roast.input }],
          measurements: roast.measurements.map(({ seconds, temperature, eventType }) => ({ seconds, temperature, eventType })),
          events: roast.events.map((event) => ({ type: event.type, shortName: event.short, label: event.label, seconds: event.seconds, temperature: event.temperature })),
          adjustments: roast.adjustments.map(({ type, label, value, unit, seconds, temperature }) => ({ type, label, value: Number(value), unit: unit || '', seconds, temperature })),
        });
      }
      navigate('history');
      notify('Tueste guardado.');
    }} />;
  } else if (activePage === 'history') {
    pageContent = <History data={data} onSaveProfile={(profile) => {
      if (demoMode) {
        saveData({ ...data, profiles: [profile, ...data.profiles] });
        notify('Perfil guardado con su curva y ajustes. El tueste sigue en el historial.');
        return;
      }
      return resourceService.create('perfiles', {
        name: profile.name,
        description: profile.description,
        flavorNotes: profile.flavorNotes,
        referenceLotId: profile.referenceLotId,
        referenceLotCode: profile.referenceLotCode,
        referenceInputKg: profile.referenceInputKg,
        referenceOutputKg: profile.referenceOutputKg,
        initialTemp: profile.initialTemp,
        finalTemp: profile.finalTemp,
        duration: profile.duration,
        level: profile.level,
        measurements: profile.measurements.map(({ seconds, temperature, eventType }) => ({ seconds, temperature, eventType })),
        adjustments: profile.adjustments.map(({ type, label, value, unit, seconds, temperature }) => ({ type, label, value: Number(value), unit: unit || '', seconds, temperature })),
      }).then(() => notify('Perfil guardado con su curva y ajustes. El tueste sigue en el historial.'));
    }} notify={notify} canManageProfiles={['admin', 'gerente'].includes(user.role)} />;
  } else {
    pageContent = <Reports data={data} notify={notify} />;
  }

  return <div className={`app-layout ${sidebarCollapsed ? 'sidebar-is-collapsed' : ''}`}>
    <Sidebar user={user} activePage={activePage} onNavigate={navigate} onLogout={signOut} isOpen={mobileMenuOpen} isCollapsed={sidebarCollapsed} onClose={() => setMobileMenuOpen(false)} />
    <main className="main-area">
      <Topbar user={user} title={pageTitle} demoMode={demoMode} onOpenMenu={toggleMenu} onNotify={() => notify('No tienes notificaciones nuevas.')} />
      <div className="page-wrap">
        <div className="page-heading">
          <div><span className="eyebrow">ROASTLAB · {(ROLE_NAMES[user.role] || ROLE_NAMES.tostador).toUpperCase()}</span><h1>{pageTitle}</h1></div>
          {activePage === 'dashboard' && <div className="today-chip"><span>☀</span> {new Intl.DateTimeFormat('es-MX', { weekday: 'long', day: 'numeric', month: 'long' }).format(new Date())}</div>}
        </div>
        {pageContent}
      </div>
    </main>
    <Toast message={toastMessage} />
  </div>;
}
