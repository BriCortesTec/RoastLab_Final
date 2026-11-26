import { useState } from 'react';
import Icon from '../components/Icon.jsx';
import { authService } from '../services/api.js';
import '../styles/auth.css';

const normalizeRole = (value) => {
  const role = String(value || '').toLowerCase();
  return role.includes('admin') || role.includes('administr')
    ? 'admin'
    : role.includes('geren')
      ? 'gerente'
      : 'tostador';
};

export default function Login({ onLogin }) {
  const [mode, setMode] = useState('login');
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const [role, setRole] = useState('tostador');
  const [form, setForm] = useState({
    name: '',
    surname: '',
    email: '',
    password: ''
  });
  const demoMode = !import.meta.env.VITE_API_URL;

  const submit = async (event) => {
    event.preventDefault();
    setError('');

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      return setError('Escribe un correo válido.');
    }
    if (form.password.length < 8) {
      return setError('La contraseña debe tener al menos 8 caracteres.');
    }
    if (mode === 'register' && !/^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/.test(form.password)) {
      return setError('Usa al menos 8 caracteres, una mayúscula, un número y un carácter especial.');
    }
    if (mode === 'register' && (!form.name.trim() || !form.surname.trim())) {
      return setError('Completa tu nombre y apellido.');
    }

    setBusy(true);
    try {
      let user;
      if (demoMode) {
        user = {
          name: mode === 'register' ? form.name.trim() : form.email.split('@')[0],
          surname: mode === 'register' ? form.surname.trim() : 'RoastLab',
          email: form.email,
          role
        };
      } else {
        const data =
          mode === 'register'
            ? await authService.register({
                name: form.name,
                surname: form.surname,
                email: form.email,
                password: form.password
              })
            : await authService.login({
                email: form.email,
                password: form.password
              });

        if (data.token) {
          localStorage.setItem('roastlab_token', data.token);
        }

        const responseUser = data.usuario || data.user || data;
        user = {
          name: responseUser.name || responseUser.nombre || form.email.split('@')[0],
          surname: responseUser.surname || responseUser.apellido || '',
          email: responseUser.email || responseUser.correo || form.email,
          role: normalizeRole(responseUser.role || responseUser.rol?.nombre || role)
        };
      }

      onLogin({ ...user, role: user.role || normalizeRole(role) });
    } catch (e) {
      setError(e.message || 'No fue posible iniciar sesión.');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="auth-layout">
      <section className="auth-photo">
        <div className="photo-shade" />
        <div className="brand brand-light">
          <span className="brand-icon">
            <Icon name="bean" size={28} />
          </span>
          <span>
            Roast<span className="brand-highlight">Lab</span>
          </span>
        </div>

        <div className="photo-caption">
          <span className="eyebrow">DEL GRANO A LA TAZA</span>
          <h1>
            Tu arte.
            <br />
            Tueste a tu manera.
          </h1>
          <p>El espacio para crear, perfeccionar y compartir cada tueste.</p>
        </div>

        <span className="photo-credit">
          Cultivado con intención. Tostado con pasión.
        </span>
      </section>

      <section className="auth-panel">
        <div className="auth-card">
          <div className="brand brand-mobile">
            <span className="brand-icon">
              <Icon name="bean" size={26} />
            </span>
            <span>
              Roast<span className="brand-highlight">Lab</span>
            </span>
          </div>

          <span className="eyebrow">BIENVENIDA A ROASTLAB</span>
          <h2>{mode === 'login' ? 'Qué gusto verte' : 'Crea tu cuenta'}</h2>
          <p className="muted">
            {mode === 'login'
              ? 'Inicia sesión para continuar con tus tuestes.'
              : 'Completa tus datos para empezar.'}
          </p>

          {demoMode && (
            <div className="demo-note">
              <span className="demo-dot" /> Vista de demostración · puedes usar
              cualquier correo
            </div>
          )}

          {error && (
            <div className="error-note" role="alert">
              {error}
            </div>
          )}

          <form className="auth-form" onSubmit={submit} noValidate>
            {mode === 'register' && (
              <div className="auth-two">
                <label className="field">
                  Nombre
                  <input
                    className="input"
                    autoComplete="given-name"
                    value={form.name}
                    onChange={(e) => setForm({ ...form, name: e.target.value })}
                    required
                  />
                </label>

                <label className="field">
                  Apellido
                  <input
                    className="input"
                    autoComplete="family-name"
                    value={form.surname}
                    onChange={(e) =>
                      setForm({ ...form, surname: e.target.value })
                    }
                    required
                  />
                </label>
              </div>
            )}

            <label className="field">
              Correo electrónico
              <input
                className="input"
                type="email"
                autoComplete="email"
                placeholder="tu@cafeteria.com"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                required
              />
            </label>

            <label className="field">
              Contraseña
              <input
                className="input"
                type="password"
                autoComplete={
                  mode === 'login' ? 'current-password' : 'new-password'
                }
                placeholder={mode === 'register' ? '8+ caracteres, mayúscula, número y símbolo' : 'Tu contraseña'}
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                required
                minLength={8}
              />
            </label>

            {demoMode && (
              <label className="field">
                Entrar como
                <select
                  className="input"
                  value={role}
                  onChange={(e) => setRole(e.target.value)}
                >
                  <option value="tostador">Tostador</option>
                  <option value="gerente">Gerencia</option>
                  <option value="admin">Administración</option>
                </select>
              </label>
            )}

            <button
              className="button button-primary button-full"
              disabled={busy}
            >
              {busy
                ? 'Un momento…'
                : mode === 'login'
                  ? 'Iniciar sesión'
                  : 'Crear cuenta'}{' '}
              <Icon name="arrow" size={18} />
            </button>
          </form>

          <p className="auth-switch">
            {mode === 'login' ? '¿Aún no tienes cuenta?' : '¿Ya tienes cuenta?'}{' '}
            <button
              onClick={() => {
                setMode(mode === 'login' ? 'register' : 'login');
                setError('');
              }}
            >
              {mode === 'login' ? 'Regístrate' : 'Inicia sesión'}
            </button>
          </p>

          <p className="auth-foot">
            Al continuar, aceptas nuestros términos de servicio.
          </p>
        </div>
      </section>
    </div>
  );
}
