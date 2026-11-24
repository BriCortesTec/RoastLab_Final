import { useState } from 'react';
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

function readUser() {
  try {
    return JSON.parse(sessionStorage.getItem('roastlab_user'));
  } catch {
    return null;
  }
}

function readData() {
  try {
    return {
      ...INITIAL_DATA,
      ...JSON.parse(localStorage.getItem('roastlab_data'))
    };
  } catch {
    return INITIAL_DATA;
  }
}

export default function App() {
  const [user, setUser] = useState(readUser);
  const [page, setPage] = useState('dashboard');
  const [data, setData] = useState(readData);
  const [toastMessage, setToastMessage] = useState('');
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const demoMode = !import.meta.env.VITE_API_URL;

  const notify = (message) => {
    setToastMessage(message);
    window.setTimeout(() => setToastMessage(''), 2800);
  };

  const saveData = (updatedData) => {
    setData(updatedData);
    localStorage.setItem('roastlab_data', JSON.stringify(updatedData));
  };

  const signIn = (newUser) => {
    setUser(newUser);
    sessionStorage.setItem('roastlab_user', JSON.stringify(newUser));
  };

  const signOut = () => {
    setUser(null);
    sessionStorage.removeItem('roastlab_user');
    localStorage.removeItem('roastlab_token');
  };

  const navigate = (nextPage) => {
    setPage(nextPage);
    setMobileMenuOpen(false);
  };

  if (!user) {
    return <Login onLogin={signIn} />;
  }

  const allowedPages = NAVIGATION[user.role] || NAVIGATION.tostador;
  const activePage = allowedPages.includes(page) ? page : 'dashboard';
  const pageTitle = PAGE_LABELS[activePage];
  let pageContent;

  if (activePage === 'dashboard') {
    pageContent = <Dashboard user={user} data={data} navigate={navigate} />;
  } else if (activePage === 'cafes') {
    pageContent = (
      <Cafes
        items={data.cafes}
        onSave={(cafes) => saveData({ ...data, cafes })}
        notify={notify}
      />
    );
  } else if (activePage === 'lots') {
    pageContent = (
      <Lots
        items={data.lots}
        cafes={data.cafes}
        onSave={(lots) => saveData({ ...data, lots })}
        notify={notify}
      />
    );
  } else if (activePage === 'profiles') {
    pageContent = (
      <Profiles
        items={data.profiles}
        onSave={(profiles) => saveData({ ...data, profiles })}
        notify={notify}
      />
    );
  } else if (activePage === 'roasts') {
    pageContent = (
      <RoastForm
        data={data}
        onSave={(roast) => {
          const lots = data.lots.map((lot) =>
            lot.code === roast.lotCode
              ? { ...lot, weight: Number((Number(lot.weight) - roast.input).toFixed(2)) }
              : lot
          );
          saveData({ ...data, lots, roasts: [roast, ...data.roasts] });
          navigate('history');
          notify('Tueste guardado.');
        }}
      />
    );
  } else if (activePage === 'history') {
    pageContent = <History data={data} />;
  } else {
    pageContent = <Reports data={data} />;
  }

  return (
    <div className="app-layout">
      <Sidebar
        user={user}
        activePage={activePage}
        onNavigate={navigate}
        onLogout={signOut}
        isOpen={mobileMenuOpen}
        onClose={() => setMobileMenuOpen(false)}
      />

      <main className="main-area">
        <Topbar
          user={user}
          title={pageTitle}
          demoMode={demoMode}
          onOpenMenu={() => setMobileMenuOpen(true)}
          onNotify={() => notify('No tienes notificaciones nuevas.')}
        />

        <div className="page-wrap">
          <div className="page-heading">
            <div>
              <span className="eyebrow">
                ROASTLAB · {(ROLE_NAMES[user.role] || ROLE_NAMES.tostador).toUpperCase()}
              </span>
              <h1>{pageTitle}</h1>
            </div>

            {activePage === 'dashboard' && (
              <div className="today-chip">
                <span>☀</span>{' '}
                {new Intl.DateTimeFormat('es-MX', {
                  weekday: 'long',
                  day: 'numeric',
                  month: 'long'
                }).format(new Date())}
              </div>
            )}
          </div>

          <div className="demo-banner">
            <span>ℹ</span> Los catálogos de esta versión guardan cambios en este
            navegador; requieren endpoints backend para guardar en MySQL.
          </div>

          {pageContent}
        </div>
      </main>

      <Toast message={toastMessage} />
    </div>
  );
}